package com.advisor.mapper;

import com.advisor.entity.FundInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException; // 用于测试唯一约束

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 使用内存H2数据库
class FundInfoMapperTest {

    @Autowired
    private FundInfoMapper fundInfoMapper;

    // 辅助方法：创建并插入一个 FundInfo 对象
    private FundInfo createAndInsertFund(String fundCode, String fundName, String fundType,
                                         String fundCompany, String fundManager, Integer riskLevel,
                                         BigDecimal minInvestment, String description) {
        FundInfo fund = new FundInfo();
        fund.setFundCode(fundCode);
        fund.setFundName(fundName);
        fund.setFundType(fundType);
        fund.setFundCompany(fundCompany);
        fund.setFundManager(fundManager);
        fund.setRiskLevel(riskLevel);
        fund.setMinInvestment(minInvestment);
        fund.setDescription(description);
        fund.setCreatedAt(LocalDateTime.now());
        fund.setUpdatedAt(LocalDateTime.now());
        fundInfoMapper.insert(fund);
        assertNotNull(fund.getId(), "插入后ID不应为空"); // 确保自增ID被设置
        return fund;
    }

    // 在每个测试方法执行前，插入一些基础数据
    @BeforeEach
    void setUp() {
        // 清空表（如果AutoConfigureTestDatabase没有完全重置）
        // 这里不需要显式清空，因为Replace.ANY会创建新数据库

        // 插入测试数据
        createAndInsertFund("F001", "沪深300指数基金", "指数型", "A基金公司", "张三", 3, BigDecimal.valueOf(100.00), "追踪沪深300指数");
        createAndInsertFund("F002", "中证500增强基金", "股票型", "B基金公司", "李四", 4, BigDecimal.valueOf(500.00), "主动管理，超额收益");
        createAndInsertFund("F003", "全球科技QDII", "QDII", "A基金公司", "张三", 5, BigDecimal.valueOf(1000.00), "投资全球科技股");
        createAndInsertFund("F004", "稳健债券基金", "债券型", "C基金公司", "王五", 2, BigDecimal.valueOf(100.00), "低风险固定收益");
        createAndInsertFund("F005", "医疗健康主题基金", "股票型", "B基金公司", "赵六", 4, BigDecimal.valueOf(200.00), "聚焦医疗健康产业");
    }

    @Test
    @DisplayName("insert - 插入基金信息成功")
    void testInsert() {
        FundInfo newFund = new FundInfo();
        String uniqueCode = "NEW_" + UUID.randomUUID().toString().substring(0, 5);
        String uniqueName = "新基金名称_" + UUID.randomUUID().toString().substring(0, 5);
        newFund.setFundCode(uniqueCode);
        newFund.setFundName(uniqueName);
        newFund.setFundType("混合型");
        newFund.setFundCompany("D基金公司");
        newFund.setFundManager("钱七");
        newFund.setRiskLevel(3);
        newFund.setMinInvestment(BigDecimal.valueOf(200.00));
        newFund.setDescription("一支新发行的混合型基金");
        newFund.setCreatedAt(LocalDateTime.now());
        newFund.setUpdatedAt(LocalDateTime.now());

        int affectedRows = fundInfoMapper.insert(newFund);
        assertEquals(1, affectedRows, "应该插入一条记录");
        assertNotNull(newFund.getId(), "插入后实体ID不应为空");

        FundInfo found = fundInfoMapper.findById(newFund.getId());
        assertNotNull(found, "应该能通过ID找到新插入的基金");
        assertEquals(uniqueCode, found.getFundCode());
        assertEquals(uniqueName, found.getFundName());
        assertEquals("混合型", found.getFundType());
    }

    @Test
    @DisplayName("insert - 插入重复基金代码应抛出异常")
    void testInsert_DuplicateFundCode() {
        FundInfo fund = new FundInfo();
        fund.setFundCode("F001"); // 使用已存在代码
        fund.setFundName("另一个名称" + UUID.randomUUID().toString().substring(0, 4)); // 确保名称唯一
        fund.setFundType("类型");
        fund.setFundCompany("公司");
        fund.setFundManager("经理");
        fund.setRiskLevel(1);
        fund.setMinInvestment(BigDecimal.ONE);
        fund.setDescription("描述");
        fund.setCreatedAt(LocalDateTime.now());
        fund.setUpdatedAt(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> {
            fundInfoMapper.insert(fund);
        }, "插入重复基金代码应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("insert - 插入重复基金名称应抛出异常")
    void testInsert_DuplicateFundName() {
        FundInfo fund = new FundInfo();
        fund.setFundCode("UNIQUE_CODE_" + UUID.randomUUID().toString().substring(0, 4)); // 确保代码唯一
        fund.setFundName("沪深300指数基金"); // 使用已存在名称
        fund.setFundType("类型");
        fund.setFundCompany("公司");
        fund.setFundManager("经理");
        fund.setRiskLevel(1);
        fund.setMinInvestment(BigDecimal.ONE);
        fund.setDescription("描述");
        fund.setCreatedAt(LocalDateTime.now());
        fund.setUpdatedAt(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> {
            fundInfoMapper.insert(fund);
        }, "插入重复基金名称应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("findList - 按基金类型查询")
    void testFindList_ByFundType() {
        List<FundInfo> list = fundInfoMapper.findList("股票型", null, null, null, null, null, 0, 10);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F002")));
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F005")));
    }

    @Test
    @DisplayName("findList - 按基金公司和基金经理查询")
    void testFindList_ByCompanyAndManager() {
        List<FundInfo> list = fundInfoMapper.findList(null, "A基金公司", "张三", null, null, null, 0, 10);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F001")));
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F003")));
    }

    @Test
    @DisplayName("findList - 按风险等级和最小投资额查询")
    void testFindList_ByRiskAndInvestment() {
        List<FundInfo> list = fundInfoMapper.findList(null, null, null, 4, BigDecimal.valueOf(200.00), null, 0, 10);
        assertNotNull(list);
        assertEquals(2, list.size()); // F002 (500), F005 (200) 都是风险4且投资>=200
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F002")));
        assertTrue(list.stream().anyMatch(f -> f.getFundCode().equals("F005")));
    }

    @Test
    @DisplayName("findList - 按关键词模糊查询")
    void testFindList_ByKeyword() {
        List<FundInfo> list = fundInfoMapper.findList(null, null, null, null, null, "指数", 0, 10);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("F001", list.get(0).getFundCode());

        list = fundInfoMapper.findList(null, null, null, null, null, "基金", 0, 10);
        assertNotNull(list);
        assertEquals(5, list.size()); // 所有基金都包含“基金”
    }

    @Test
    @DisplayName("findList - 组合条件查询")
    void testFindList_CombinedCriteria() {
        List<FundInfo> list = fundInfoMapper.findList("股票型", "B基金公司", null, 4, BigDecimal.valueOf(100.00), "医疗", 0, 10);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("F005", list.get(0).getFundCode());
    }

    @Test
    @DisplayName("findList - 分页查询")
    void testFindList_Pagination() {
        List<FundInfo> list = fundInfoMapper.findList(null, null, null, null, null, null, 0, 2);
        assertNotNull(list);
        assertEquals(2, list.size());

        list = fundInfoMapper.findList(null, null, null, null, null, null, 2, 2);
        assertNotNull(list);
        assertEquals(2, list.size());

        list = fundInfoMapper.findList(null, null, null, null, null, null, 4, 2);
        assertNotNull(list);
        assertEquals(1, list.size()); // 还有F005
    }

    @Test
    @DisplayName("findList - 查询无匹配结果")
    void testFindList_NoMatch() {
        List<FundInfo> list = fundInfoMapper.findList("不存在的类型", null, null, null, null, null, 0, 10);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("countList - 统计总数")
    void testCountList() {
        Long count = fundInfoMapper.countList(null, null, null, null, null, null);
        assertEquals(5L, count, "总数应为5");

        count = fundInfoMapper.countList("股票型", null, null, null, null, null);
        assertEquals(2L, count, "股票型基金的总数应为2");

        count = fundInfoMapper.countList(null, "A基金公司", null, null, null, null);
        assertEquals(2L, count, "A基金公司的总数应为2");

        count = fundInfoMapper.countList(null, null, null, 4, BigDecimal.valueOf(200.00), null);
        assertEquals(2L, count, "风险等级4且最小投资额>=200的总数应为2");
    }

    @Test
    @DisplayName("findById - 根据ID查询基金")
    void testFindById() {
        FundInfo fund = createAndInsertFund("TEMP_ID", "临时基金", "临时", "临时", "临时", 1, BigDecimal.TEN, "临时描述");
        Long id = fund.getId();

        FundInfo found = fundInfoMapper.findById(id);
        assertNotNull(found, "应该能找到插入的基金");
        assertEquals("临时基金", found.getFundName());

        FundInfo notFound = fundInfoMapper.findById(9999L); // 不存在的ID
        assertNull(notFound, "不应该找到不存在ID的基金");
    }

    @Test
    @DisplayName("findByFundCode - 根据基金代码查询基金")
    void testFindByFundCode() {
        FundInfo found = fundInfoMapper.findByFundCode("F002");
        assertNotNull(found, "应该能找到F002基金");
        assertEquals("中证500增强基金", found.getFundName());

        FundInfo notFound = fundInfoMapper.findByFundCode("NON_EXISTENT_CODE");
        assertNull(notFound, "不应该找到不存在代码的基金");
    }

    @Test
    @DisplayName("update - 更新基金信息")
    void testUpdate() {
        FundInfo fundToUpdate = fundInfoMapper.findByFundCode("F004");
        assertNotNull(fundToUpdate);

        fundToUpdate.setFundName("更新后的稳健债券基金");
        fundToUpdate.setFundManager("李四");
        fundToUpdate.setRiskLevel(3);
        fundToUpdate.setUpdatedAt(LocalDateTime.now().plusHours(1));

        int affectedRows = fundInfoMapper.update(fundToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        FundInfo updated = fundInfoMapper.findById(fundToUpdate.getId());
        assertNotNull(updated);
        assertEquals("更新后的稳健债券基金", updated.getFundName());
        assertEquals("李四", updated.getFundManager());
        assertEquals(3, updated.getRiskLevel());
        // 验证其他字段未受影响
        assertEquals("F004", updated.getFundCode());
    }

    @Test
    @DisplayName("update - 更新不存在的基金不影响任何记录")
    void testUpdate_NotFound() {
        FundInfo nonExistentFund = new FundInfo();
        nonExistentFund.setId(9999L);
        nonExistentFund.setFundCode("NON_EXISTENT_UPDATE");
        nonExistentFund.setFundName("Non Existent Update");
        nonExistentFund.setFundType("Type");
        nonExistentFund.setUpdatedAt(LocalDateTime.now());

        int affectedRows = fundInfoMapper.update(nonExistentFund);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteById - 删除基金信息")
    void testDeleteById() {
        FundInfo fundToDelete = fundInfoMapper.findByFundCode("F005");
        assertNotNull(fundToDelete);
        Long idToDelete = fundToDelete.getId();

        int affectedRows = fundInfoMapper.deleteById(idToDelete);
        assertEquals(1, affectedRows, "应该删除一条记录");

        FundInfo deleted = fundInfoMapper.findById(idToDelete);
        assertNull(deleted, "删除后不应该能找到基金");
    }

    @Test
    @DisplayName("deleteById - 删除不存在的ID不影响任何记录")
    void testDeleteById_NotFound() {
        int affectedRows = fundInfoMapper.deleteById(9999L); // 不存在的ID
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("findAllFundTypes - 查询所有基金类型")
    void testFindAllFundTypes() {
        List<String> types = fundInfoMapper.findAllFundTypes();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        assertEquals(4, types.size()); // 指数型, 股票型, QDII, 债券型
        assertTrue(types.contains("指数型"));
        assertTrue(types.contains("股票型"));
        assertTrue(types.contains("QDII"));
        assertTrue(types.contains("债券型"));
    }

    @Test
    @DisplayName("findAllFundCompanies - 查询所有基金公司")
    void testFindAllFundCompanies() {
        List<String> companies = fundInfoMapper.findAllFundCompanies();
        assertNotNull(companies);
        assertFalse(companies.isEmpty());
        assertEquals(3, companies.size()); // A基金公司, B基金公司, C基金公司
        assertTrue(companies.contains("A基金公司"));
        assertTrue(companies.contains("B基金公司"));
        assertTrue(companies.contains("C基金公司"));
    }

    @Test
    @DisplayName("findAllFundManagers - 查询所有基金经理")
    void testFindAllFundManagers() {
        List<String> managers = fundInfoMapper.findAllFundManagers();
        assertNotNull(managers);
        assertFalse(managers.isEmpty());
        assertEquals(4, managers.size()); // 张三, 李四, 王五, 赵六
        assertTrue(managers.contains("张三"));
        assertTrue(managers.contains("李四"));
        assertTrue(managers.contains("王五"));
        assertTrue(managers.contains("赵六"));
    }

    @Test
    @DisplayName("countAll - 统计基金总数")
    void testCountAll() {
        Long count = fundInfoMapper.countAll();
        assertEquals(5L, count, "总数应为5");
    }

    @Test
    @DisplayName("getFundTypeStats - 获取基金类型统计")
    void testGetFundTypeStats() {
        List<Map<String, Object>> stats = fundInfoMapper.getFundTypeStats();
        assertNotNull(stats);
        assertFalse(stats.isEmpty());
        assertEquals(4, stats.size()); // 指数型, 股票型, QDII, 债券型

        // 验证特定类型的数据
        Map<String, Object> stockType = stats.stream()
                .filter(m -> "股票型".equals(m.get("fund_type")))
                .findFirst().orElse(null);
        assertNotNull(stockType);
        assertEquals(2L, stockType.get("count")); // 股票型有2个

        Map<String, Object> indexType = stats.stream()
                .filter(m -> "指数型".equals(m.get("fund_type")))
                .findFirst().orElse(null);
        assertNotNull(indexType);
        assertEquals(1L, indexType.get("count")); // 指数型有1个
    }

    @Test
    @DisplayName("selectAll - 查询所有基金信息")
    void testSelectAll() {
        List<FundInfo> allFunds = fundInfoMapper.selectAll();
        assertNotNull(allFunds);
        assertEquals(5, allFunds.size());
        assertTrue(allFunds.stream().anyMatch(f -> f.getFundCode().equals("F001")));
        assertTrue(allFunds.stream().anyMatch(f -> f.getFundCode().equals("F005")));
    }

    @Test
    @DisplayName("findByCode - 根据基金代码查询基金")
    void testFindByCode() {
        FundInfo found = fundInfoMapper.findByCode("F003");
        assertNotNull(found, "应该能找到F003基金");
        assertEquals("全球科技QDII", found.getFundName());

        FundInfo notFound = fundInfoMapper.findByCode("NON_EXISTENT_CHAT_CODE");
        assertNull(notFound, "不应该找到不存在代码的基金");
    }

    @Test
    @DisplayName("findByType - 根据基金类型查询基金")
    void testFindByType() {
        List<FundInfo> funds = fundInfoMapper.findByType("QDII");
        assertNotNull(funds);
        assertEquals(1, funds.size());
        assertEquals("F003", funds.get(0).getFundCode());

        funds = fundInfoMapper.findByType("不存在的类型");
        assertNotNull(funds);
        assertTrue(funds.isEmpty());
    }

    @Test
    @DisplayName("searchFunds - 通过Map参数搜索基金")
    void testSearchFunds() {
        Map<String, Object> params = new HashMap<>();
        params.put("fundType", "股票型");
        params.put("fundCompany", "B基金公司");
        List<FundInfo> funds = fundInfoMapper.searchFunds(params);
        assertNotNull(funds);
        assertEquals(2, funds.size());
        assertTrue(funds.stream().anyMatch(f -> f.getFundCode().equals("F002")));
        assertTrue(funds.stream().anyMatch(f -> f.getFundCode().equals("F005")));

        params.clear();
        params.put("keyword", "指数");
        funds = fundInfoMapper.searchFunds(params);
        assertNotNull(funds);
        assertEquals(1, funds.size());
        assertEquals("F001", funds.get(0).getFundCode());

        params.clear();
        params.put("fundManager", "王五");
        params.put("riskLevel", 2);
        funds = fundInfoMapper.searchFunds(params);
        assertNotNull(funds);
        assertEquals(1, funds.size());
        assertEquals("F004", funds.get(0).getFundCode());

        params.clear();
        funds = fundInfoMapper.searchFunds(params); // 无参数查询所有
        assertNotNull(funds);
        assertEquals(5, funds.size());
    }
}