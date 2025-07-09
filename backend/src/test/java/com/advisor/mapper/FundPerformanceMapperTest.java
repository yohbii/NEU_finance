package com.advisor.mapper;

import com.advisor.entity.FundPerformance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FundPerformanceMapperTest {

    @Autowired
    private FundPerformanceMapper fundPerformanceMapper;

    // 定义用于测试的基金ID和代码
    private Long fundId1 = 1001L;
    private String fundCode1 = "F" + UUID.randomUUID().toString().substring(0, 7); // 随机生成，保证唯一
    private Long fundId2 = 1002L;
    private String fundCode2 = "F" + UUID.randomUUID().toString().substring(0, 7);

    // 辅助方法：创建并插入一个 FundPerformance 对象
    private FundPerformance createAndInsertPerformance(Long fundId, String fundCode, LocalDate date,
                                                       BigDecimal totalReturn, BigDecimal annualizedReturn,
                                                       BigDecimal sharpeRatio, BigDecimal maxDrawdown) {
        FundPerformance performance = new FundPerformance();
        performance.setFundId(fundId);
        performance.setFundCode(fundCode);
        performance.setDate(date);
        performance.setTotalReturn(totalReturn);
        performance.setAnnualizedReturn(annualizedReturn);
        performance.setSharpeRatio(sharpeRatio);
        performance.setMaxDrawdown(maxDrawdown);
        fundPerformanceMapper.insert(performance);
        assertNotNull(performance.getId(), "插入后ID不应为空");
        return performance;
    }

    @BeforeEach
    void setUp() {
        // 插入一些测试数据
        // Fund 1001 (fundCode1) 的业绩数据
        createAndInsertPerformance(fundId1, fundCode1, LocalDate.of(2023, 1, 31),
                BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.12), BigDecimal.valueOf(1.5), BigDecimal.valueOf(-0.02));
        createAndInsertPerformance(fundId1, fundCode1, LocalDate.of(2023, 2, 28),
                BigDecimal.valueOf(0.08), BigDecimal.valueOf(0.15), BigDecimal.valueOf(1.8), BigDecimal.valueOf(-0.03));
        createAndInsertPerformance(fundId1, fundCode1, LocalDate.of(2023, 3, 31),
                BigDecimal.valueOf(0.10), BigDecimal.valueOf(0.18), BigDecimal.valueOf(2.0), BigDecimal.valueOf(-0.025));

        // Fund 1002 (fundCode2) 的业绩数据
        createAndInsertPerformance(fundId2, fundCode2, LocalDate.of(2023, 1, 31),
                BigDecimal.valueOf(0.03), BigDecimal.valueOf(0.10), BigDecimal.valueOf(1.2), BigDecimal.valueOf(-0.015));
        createAndInsertPerformance(fundId2, fundCode2, LocalDate.of(2023, 2, 28),
                BigDecimal.valueOf(0.06), BigDecimal.valueOf(0.13), BigDecimal.valueOf(1.4), BigDecimal.valueOf(-0.02));
    }

    @Test
    @DisplayName("insert - 插入基金业绩成功")
    void testInsert() {
        Long newFundId = 1003L;
        String newFundCode = "F" + UUID.randomUUID().toString().substring(0, 7);
        LocalDate newDate = LocalDate.now();

        FundPerformance newPerformance = new FundPerformance();
        newPerformance.setFundId(newFundId);
        newPerformance.setFundCode(newFundCode);
        newPerformance.setDate(newDate);
        newPerformance.setTotalReturn(BigDecimal.valueOf(0.15));
        newPerformance.setAnnualizedReturn(BigDecimal.valueOf(0.25));
        newPerformance.setSharpeRatio(BigDecimal.valueOf(2.5));
        newPerformance.setMaxDrawdown(BigDecimal.valueOf(-0.05));

        int affectedRows = fundPerformanceMapper.insert(newPerformance);
        assertEquals(1, affectedRows, "应该插入一条记录");
        assertNotNull(newPerformance.getId(), "插入后ID不应为空");

        FundPerformance found = fundPerformanceMapper.findById(newPerformance.getId());
        assertNotNull(found, "应该能通过ID找到新插入的业绩数据");
        assertEquals(newFundCode, found.getFundCode());
        assertEquals(newDate, found.getDate());
        assertEquals(BigDecimal.valueOf(0.15).stripTrailingZeros(), found.getTotalReturn().stripTrailingZeros());
    }

    @Test
    @DisplayName("insert - 插入重复基金代码和日期组合应抛出异常")
    void testInsert_DuplicateFundCodeAndDate() {
        // 尝试插入一个与 setUp 中已存在的完全相同 (fundCode, date) 的记录
        assertThrows(DataIntegrityViolationException.class, () -> {
            createAndInsertPerformance(fundId1, fundCode1, LocalDate.of(2023, 1, 31),
                    BigDecimal.valueOf(9.99), BigDecimal.valueOf(9.99), BigDecimal.ZERO, BigDecimal.ZERO);
        }, "插入重复的基金代码和日期组合应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("findList - 按基金代码查询，带分页")
    void testFindList_ByFundCodeWithPagination() {
        List<FundPerformance> list = fundPerformanceMapper.findList(fundCode1, 0, 10);
        assertNotNull(list);
        assertEquals(3, list.size()); // fundCode1 有3条数据

        // 验证排序是否按日期倒序
        assertEquals(LocalDate.of(2023, 3, 31), list.get(0).getDate());
        assertEquals(LocalDate.of(2023, 2, 28), list.get(1).getDate());
        assertEquals(LocalDate.of(2023, 1, 31), list.get(2).getDate());

        // 测试分页
        list = fundPerformanceMapper.findList(fundCode1, 0, 2);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(LocalDate.of(2023, 3, 31), list.get(0).getDate());
        assertEquals(LocalDate.of(2023, 2, 28), list.get(1).getDate());
    }

    @Test
    @DisplayName("findList - 查询所有基金业绩，带分页")
    void testFindList_AllWithPagination() {
        List<FundPerformance> list = fundPerformanceMapper.findList(null, 0, 3);
        assertNotNull(list);
        assertEquals(3, list.size()); // 总共5条，取前3条

        list = fundPerformanceMapper.findList(null, 3, 10);
        assertNotNull(list);
        assertEquals(2, list.size()); // 总共5条，跳过前3条，取剩余2条
    }

    @Test
    @DisplayName("findList - 查询无匹配结果")
    void testFindList_NoMatch() {
        List<FundPerformance> list = fundPerformanceMapper.findList("NON_EXISTENT_CODE", 0, 10);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("countList - 统计总数")
    void testCountList() {
        Long count = fundPerformanceMapper.countList(null);
        assertEquals(5L, count, "总数应为5"); // setUp 中插入了5条数据

        count = fundPerformanceMapper.countList(fundCode1);
        assertEquals(3L, count, "特定基金代码的总数应为3");

        count = fundPerformanceMapper.countList("NON_EXISTENT_CODE");
        assertEquals(0L, count, "不存在基金代码的总数应为0");
    }

    @Test
    @DisplayName("findById - 根据ID查询基金业绩")
    void testFindById() {
        FundPerformance performance = createAndInsertPerformance(1004L, "TEMP_F_CODE", LocalDate.now(),
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO);
        Long id = performance.getId();

        FundPerformance found = fundPerformanceMapper.findById(id);
        assertNotNull(found, "应该能找到插入的业绩数据");
        assertEquals("TEMP_F_CODE", found.getFundCode());

        FundPerformance notFound = fundPerformanceMapper.findById(99999L); // 不存在的ID
        assertNull(notFound, "不应该找到不存在ID的业绩数据");
    }

    @Test
    @DisplayName("findLatestByFundCode - 根据基金代码查询最新业绩")
    void testFindLatestByFundCode() {
        FundPerformance latest = fundPerformanceMapper.findLatestByFundCode(fundCode1);
        assertNotNull(latest);
        assertEquals(fundCode1, latest.getFundCode());
        assertEquals(LocalDate.of(2023, 3, 31), latest.getDate());
        assertEquals(BigDecimal.valueOf(0.10).stripTrailingZeros(), latest.getTotalReturn().stripTrailingZeros());

        FundPerformance latest2 = fundPerformanceMapper.findLatestByFundCode(fundCode2);
        assertNotNull(latest2);
        assertEquals(fundCode2, latest2.getFundCode());
        assertEquals(LocalDate.of(2023, 2, 28), latest2.getDate());
        assertEquals(BigDecimal.valueOf(0.06).stripTrailingZeros(), latest2.getTotalReturn().stripTrailingZeros());

        FundPerformance notFound = fundPerformanceMapper.findLatestByFundCode("NON_EXISTENT_LATEST");
        assertNull(notFound);
    }

    @Test
    @DisplayName("update - 更新基金业绩")
    void testUpdate() {
        // 查找一条记录进行更新
        FundPerformance performanceToUpdate = fundPerformanceMapper.findLatestByFundCode(fundCode1);
        assertNotNull(performanceToUpdate);

        // 更新字段
        performanceToUpdate.setTotalReturn(BigDecimal.valueOf(0.11));
        performanceToUpdate.setSharpeRatio(BigDecimal.valueOf(2.1));
        performanceToUpdate.setAnnualizedReturn(BigDecimal.valueOf(0.19));

        int affectedRows = fundPerformanceMapper.update(performanceToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        // 验证更新是否成功
        FundPerformance updated = fundPerformanceMapper.findById(performanceToUpdate.getId());
        assertNotNull(updated);
        assertEquals(BigDecimal.valueOf(0.11).stripTrailingZeros(), updated.getTotalReturn().stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(2.1).stripTrailingZeros(), updated.getSharpeRatio().stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(0.19).stripTrailingZeros(), updated.getAnnualizedReturn().stripTrailingZeros());
    }

    @Test
    @DisplayName("update - 更新不存在的ID不影响任何记录")
    void testUpdate_NotFound() {
        FundPerformance nonExistent = new FundPerformance();
        nonExistent.setId(99999L);
        nonExistent.setFundId(1L);
        nonExistent.setFundCode("dummy");
        nonExistent.setDate(LocalDate.now());
        nonExistent.setTotalReturn(BigDecimal.ONE);

        int affectedRows = fundPerformanceMapper.update(nonExistent);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteById - 删除基金业绩")
    void testDeleteById() {
        FundPerformance performanceToDelete = createAndInsertPerformance(1005L, "DEL_F_CODE", LocalDate.now(),
                BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO);
        Long idToDelete = performanceToDelete.getId();

        int affectedRows = fundPerformanceMapper.deleteById(idToDelete);
        assertEquals(1, affectedRows, "应该删除一条记录");

        FundPerformance deleted = fundPerformanceMapper.findById(idToDelete);
        assertNull(deleted, "删除后不应该能找到业绩数据");
    }

    @Test
    @DisplayName("deleteById - 删除不存在的ID不影响任何记录")
    void testDeleteById_NotFound() {
        int affectedRows = fundPerformanceMapper.deleteById(99999L);
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteByFundCode - 根据基金代码删除业绩数据")
    void testDeleteByFundCode() {
        // 先创建一些特定基金代码的数据
        createAndInsertPerformance(1006L, "TO_DELETE_CODE", LocalDate.of(2023, 1, 1), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO);
        createAndInsertPerformance(1006L, "TO_DELETE_CODE", LocalDate.of(2023, 1, 2), BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ONE, BigDecimal.ZERO);

        Long countBeforeDelete = fundPerformanceMapper.countList("TO_DELETE_CODE");
        assertEquals(2L, countBeforeDelete);

        int affectedRows = fundPerformanceMapper.deleteByFundCode("TO_DELETE_CODE");
        assertEquals(2, affectedRows, "应该删除2条记录");

        Long countAfterDelete = fundPerformanceMapper.countList("TO_DELETE_CODE");
        assertEquals(0L, countAfterDelete);
    }

    @Test
    @DisplayName("deleteByFundCode - 删除不存在的基金代码不影响任何记录")
    void testDeleteByFundCode_NotFound() {
        int affectedRows = fundPerformanceMapper.deleteByFundCode("NON_EXISTENT_DELETE_CODE");
        assertEquals(0, affectedRows, "删除不存在的基金代码不应该影响任何记录");
    }

    @Test
    @DisplayName("getAvgReturnByMonth - 获取指定月份的平均收益率")
    void testGetAvgReturnByMonth() {
        // 2023年1月有两条记录: F1001 (0.05), F1002 (0.03) -> Avg = (0.05 + 0.03) / 2 = 0.04
        Double avgJan = fundPerformanceMapper.getAvgReturnByMonth(2023, 1);
        assertNotNull(avgJan);
        assertEquals(0.04, avgJan, 0.0001, "2023年1月平均收益率不正确");

        // 2023年2月有两条记录: F1001 (0.08), F1002 (0.06) -> Avg = (0.08 + 0.06) / 2 = 0.07
        Double avgFeb = fundPerformanceMapper.getAvgReturnByMonth(2023, 2);
        assertNotNull(avgFeb);
        assertEquals(0.07, avgFeb, 0.0001, "2023年2月平均收益率不正确");

        // 2023年3月只有一条记录: F1001 (0.10) -> Avg = 0.10
        Double avgMar = fundPerformanceMapper.getAvgReturnByMonth(2023, 3);
        assertNotNull(avgMar);
        assertEquals(0.10, avgMar, 0.0001, "2023年3月平均收益率不正确");

        // 没有数据的月份
        Double avgApril = fundPerformanceMapper.getAvgReturnByMonth(2023, 4);
        assertNull(avgApril, "没有数据的月份平均收益率应为null");
    }

    @Test
    @DisplayName("getCurrentRiskMetrics - 获取当前风险指标")
    void testGetCurrentRiskMetrics() {
        // 总共有5条数据，夏普比率分别为：1.5, 1.8, 2.0, 1.2, 1.4
        // 平均夏普比率：(1.5 + 1.8 + 2.0 + 1.2 + 1.4) / 5 = 1.58
        // 最大回撤分别为：-0.02, -0.03, -0.025, -0.015, -0.02
        // 最大回撤（绝对值最大）：-0.03
        Map<String, Object> metrics = fundPerformanceMapper.getCurrentRiskMetrics();
        assertNotNull(metrics);
        assertFalse(metrics.isEmpty());

        assertEquals(BigDecimal.valueOf(1.58).stripTrailingZeros(), ((BigDecimal) metrics.get("avgSharpeRatio")).stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(-0.03).stripTrailingZeros(), ((BigDecimal) metrics.get("maxDrawdownOverall")).stripTrailingZeros());
    }

    @Test
    @DisplayName("findLatestByFundId - 根据基金ID查询最新业绩")
    void testFindLatestByFundId() {
        FundPerformance latest = fundPerformanceMapper.findLatestByFundId(fundId1);
        assertNotNull(latest);
        assertEquals(fundId1, latest.getFundId());
        assertEquals(LocalDate.of(2023, 3, 31), latest.getDate());
        assertEquals(BigDecimal.valueOf(0.10).stripTrailingZeros(), latest.getTotalReturn().stripTrailingZeros());

        FundPerformance latest2 = fundPerformanceMapper.findLatestByFundId(fundId2);
        assertNotNull(latest2);
        assertEquals(fundId2, latest2.getFundId());
        assertEquals(LocalDate.of(2023, 2, 28), latest2.getDate());
        assertEquals(BigDecimal.valueOf(0.06).stripTrailingZeros(), latest2.getTotalReturn().stripTrailingZeros());

        FundPerformance notFound = fundPerformanceMapper.findLatestByFundId(9999L);
        assertNull(notFound);
    }
}