package com.advisor.mapper;

import com.advisor.entity.FundFactorValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID; // 用于生成唯一的基金代码和因子代码

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FundFactorValueMapperTest {

    @Autowired
    private FundFactorValueMapper fundFactorValueMapper;

    private String testFundCode1;
    private String testFactorCode1;
    private String testFundCode2;
    private String testFactorCode2;

    @BeforeEach
    void setUp() {
        // 生成唯一的测试数据，防止不同测试之间相互影响
        testFundCode1 = "FUND_" + UUID.randomUUID().toString().substring(0, 8);
        testFactorCode1 = "FACTOR_" + UUID.randomUUID().toString().substring(0, 8);
        testFundCode2 = "FUND_" + UUID.randomUUID().toString().substring(0, 8);
        testFactorCode2 = "FACTOR_" + UUID.randomUUID().toString().substring(0, 8);

        // 插入一些初始数据
        insertFactorValue(testFundCode1, testFactorCode1, LocalDate.of(2023, 1, 1), BigDecimal.valueOf(10.5));
        insertFactorValue(testFundCode1, testFactorCode1, LocalDate.of(2023, 1, 2), BigDecimal.valueOf(10.6));
        insertFactorValue(testFundCode1, testFactorCode2, LocalDate.of(2023, 1, 1), BigDecimal.valueOf(20.1));
        insertFactorValue(testFundCode2, testFactorCode1, LocalDate.of(2023, 1, 1), BigDecimal.valueOf(30.2));
    }

    // 辅助方法：插入单个基金因子值
    private FundFactorValue insertFactorValue(String fundCode, String factorCode, LocalDate tradeDate, BigDecimal value) {
        FundFactorValue ffv = new FundFactorValue();
        ffv.setFundCode(fundCode);
        ffv.setFactorCode(factorCode);
        ffv.setTradeDate(tradeDate);
        ffv.setValue(value);
        fundFactorValueMapper.insert(ffv);
        assertNotNull(ffv.getId(), "插入后ID不应为空");
        return ffv;
    }

    @Test
    @DisplayName("insert - 插入基金因子值成功")
    void testInsert() {
        String newFundCode = "NEW_FUND_" + UUID.randomUUID().toString().substring(0, 4);
        String newFactorCode = "NEW_FACTOR_" + UUID.randomUUID().toString().substring(0, 4);
        LocalDate newTradeDate = LocalDate.now();
        BigDecimal newValue = BigDecimal.valueOf(100.0);

        FundFactorValue ffv = new FundFactorValue();
        ffv.setFundCode(newFundCode);
        ffv.setFactorCode(newFactorCode);
        ffv.setTradeDate(newTradeDate);
        ffv.setValue(newValue);

        int affectedRows = fundFactorValueMapper.insert(ffv);
        assertEquals(1, affectedRows, "应该插入一条记录");
        assertNotNull(ffv.getId(), "插入后ID不应为空");

        FundFactorValue found = fundFactorValueMapper.findById(ffv.getId());
        assertNotNull(found, "应该能通过ID找到新插入的因子值");
        assertEquals(newFundCode, found.getFundCode());
        assertEquals(newFactorCode, found.getFactorCode());
        assertEquals(newTradeDate, found.getTradeDate());
        assertEquals(newValue.stripTrailingZeros(), found.getValue().stripTrailingZeros());
    }

    @Test
    @DisplayName("insert - 插入重复组合应抛出异常 (或更新，取决于XML配置)")
    void testInsert_DuplicateCombo() {
        // 尝试插入一个与 setUp 中已存在的完全相同 (fundCode, factorCode, tradeDate) 的记录
        String existingFundCode = testFundCode1;
        String existingFactorCode = testFactorCode1;
        LocalDate existingTradeDate = LocalDate.of(2023, 1, 1);
        BigDecimal newValue = BigDecimal.valueOf(999.9); // 不同的值

        FundFactorValue ffv = new FundFactorValue();
        ffv.setFundCode(existingFundCode);
        ffv.setFactorCode(existingFactorCode);
        ffv.setTradeDate(existingTradeDate);
        ffv.setValue(newValue);

        // 如果 schema.sql 中有 UNIQUE 约束，且 XML 中没有 ON DUPLICATE KEY UPDATE，则会抛出 DataIntegrityViolationException
        // 如果 XML 中有 ON DUPLICATE KEY UPDATE，则会更新而不是抛异常，此时此测试会失败。
        // 请根据您的业务预期调整 XML 和此测试。
        assertThrows(DataIntegrityViolationException.class, () -> {
            fundFactorValueMapper.insert(ffv);
        }, "插入重复的基金-因子-日期组合应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("findList - 按基金代码和因子代码查询")
    void testFindList_ByFundAndFactorCode() {
        List<FundFactorValue> list = fundFactorValueMapper.findList(testFundCode1, testFactorCode1, 0, 10);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(BigDecimal.valueOf(10.5).stripTrailingZeros(), list.get(0).getValue().stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(10.6).stripTrailingZeros(), list.get(1).getValue().stripTrailingZeros());
    }

    @Test
    @DisplayName("findList - 按基金代码查询")
    void testFindList_ByFundCodeOnly() {
        List<FundFactorValue> list = fundFactorValueMapper.findList(testFundCode1, null, 0, 10);
        assertNotNull(list);
        assertEquals(3, list.size()); // testFundCode1 有两条 testFactorCode1 和一条 testFactorCode2
    }

    @Test
    @DisplayName("findList - 按因子代码查询")
    void testFindList_ByFactorCodeOnly() {
        List<FundFactorValue> list = fundFactorValueMapper.findList(null, testFactorCode1, 0, 10);
        assertNotNull(list);
        assertEquals(3, list.size()); // testFactorCode1 有两条 testFundCode1 和一条 testFundCode2
    }

    @Test
    @DisplayName("findList - 查询无匹配结果")
    void testFindList_NoMatch() {
        List<FundFactorValue> list = fundFactorValueMapper.findList("NON_EXISTENT_FUND", "NON_EXISTENT_FACTOR", 0, 10);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("findList - 分页查询")
    void testFindList_Pagination() {
        List<FundFactorValue> list = fundFactorValueMapper.findList(testFundCode1, testFactorCode1, 0, 1);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(BigDecimal.valueOf(10.5).stripTrailingZeros(), list.get(0).getValue().stripTrailingZeros());

        list = fundFactorValueMapper.findList(testFundCode1, testFactorCode1, 1, 1);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(BigDecimal.valueOf(10.6).stripTrailingZeros(), list.get(0).getValue().stripTrailingZeros());
    }

    @Test
    @DisplayName("countList - 统计总数")
    void testCountList() {
        Long count = fundFactorValueMapper.countList(null, null);
        assertEquals(4L, count, "总数应为4"); // setUp 中插入了4条数据

        count = fundFactorValueMapper.countList(testFundCode1, testFactorCode1);
        assertEquals(2L, count, "特定基金和因子的总数应为2");

        count = fundFactorValueMapper.countList(testFundCode1, null);
        assertEquals(3L, count, "特定基金的总数应为3");

        count = fundFactorValueMapper.countList(null, testFactorCode2);
        assertEquals(1L, count, "特定因子的总数应为1");

        count = fundFactorValueMapper.countList("NON_EXISTENT_FUND", null);
        assertEquals(0L, count, "不存在基金的总数应为0");
    }

    @Test
    @DisplayName("findById - 根据ID查询基金因子值")
    void testFindById() {
        FundFactorValue ffv = insertFactorValue("FIND_ID_FUND", "FIND_ID_FACTOR", LocalDate.now(), BigDecimal.valueOf(50.5));
        Long id = ffv.getId();

        FundFactorValue found = fundFactorValueMapper.findById(id);
        assertNotNull(found, "应该能找到插入的因子值");
        assertEquals("FIND_ID_FUND", found.getFundCode());

        FundFactorValue notFound = fundFactorValueMapper.findById(99999L); // 不存在的ID
        assertNull(notFound, "不应该找到不存在ID的因子值");
    }

    @Test
    @DisplayName("findByFundAndFactor - 根据基金代码和因子代码查询最新因子值")
    void testFindByFundAndFactor() {
        // testFundCode1, testFactorCode1 有两条数据：2023-01-01 和 2023-01-02
        FundFactorValue found = fundFactorValueMapper.findByFundAndFactor(testFundCode1, testFactorCode1);
        assertNotNull(found, "应该能找到结果");
        // 假设XML中findbyFundAndFactor是获取最新一条，所以应是10.6
        assertEquals(BigDecimal.valueOf(10.6).stripTrailingZeros(), found.getValue().stripTrailingZeros());
        assertEquals(LocalDate.of(2023, 1, 2), found.getTradeDate());

        FundFactorValue notFound = fundFactorValueMapper.findByFundAndFactor("NON_EXISTENT_FUND", "NON_EXISTENT_FACTOR");
        assertNull(notFound, "不应该找到不存在组合的因子值");
    }

    @Test
    @DisplayName("update - 更新基金因子值")
    void testUpdate() {
        FundFactorValue ffvToUpdate = insertFactorValue("UPDATE_FUND", "UPDATE_FACTOR", LocalDate.of(2023, 2, 1), BigDecimal.valueOf(1.0));
        Long idToUpdate = ffvToUpdate.getId();

        ffvToUpdate.setValue(BigDecimal.valueOf(2.5));
        ffvToUpdate.setTradeDate(LocalDate.of(2023, 2, 2)); // 也可以更新日期

        int affectedRows = fundFactorValueMapper.update(ffvToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        FundFactorValue updated = fundFactorValueMapper.findById(idToUpdate);
        assertNotNull(updated);
        assertEquals(BigDecimal.valueOf(2.5).stripTrailingZeros(), updated.getValue().stripTrailingZeros());
        assertEquals(LocalDate.of(2023, 2, 2), updated.getTradeDate());
        assertEquals("UPDATE_FUND", updated.getFundCode());
        assertEquals("UPDATE_FACTOR", updated.getFactorCode());
    }

    @Test
    @DisplayName("update - 更新不存在的ID不影响任何记录")
    void testUpdate_NotFound() {
        FundFactorValue nonExistent = new FundFactorValue();
        nonExistent.setId(99999L);
        nonExistent.setFundCode("dummy");
        nonExistent.setFactorCode("dummy");
        nonExistent.setTradeDate(LocalDate.now());
        nonExistent.setValue(BigDecimal.ONE);

        int affectedRows = fundFactorValueMapper.update(nonExistent);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteById - 删除基金因子值")
    void testDeleteById() {
        FundFactorValue ffvToDelete = insertFactorValue("DELETE_FUND", "DELETE_FACTOR", LocalDate.now(), BigDecimal.valueOf(7.7));
        Long idToDelete = ffvToDelete.getId();

        int affectedRows = fundFactorValueMapper.deleteById(idToDelete);
        assertEquals(1, affectedRows, "应该删除一条记录");

        FundFactorValue deleted = fundFactorValueMapper.findById(idToDelete);
        assertNull(deleted, "删除后不应该能找到因子值");
    }

    @Test
    @DisplayName("deleteById - 删除不存在的ID不影响任何记录")
    void testDeleteById_NotFound() {
        int affectedRows = fundFactorValueMapper.deleteById(99999L);
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("findByFundCode - 根据基金代码查询所有因子值")
    void testFindByFundCode() {
        List<FundFactorValue> list = fundFactorValueMapper.findByFundCode(testFundCode1);
        assertNotNull(list);
        assertEquals(3, list.size(), "特定基金代码应返回3条记录");

        list = fundFactorValueMapper.findByFundCode("NON_EXISTENT_FUND");
        assertNotNull(list);
        assertTrue(list.isEmpty(), "不存在的基金代码应返回空列表");
    }

    @Test
    @DisplayName("batchInsert - 批量插入基金因子值")
    void testBatchInsert() {
        String batchFundCode = "BATCH_FUND_" + UUID.randomUUID().toString().substring(0, 4);
        List<FundFactorValue> batchList = Arrays.asList(
                createFundFactorValueObject(batchFundCode, "B_FACTOR_1", LocalDate.of(2023, 3, 1), BigDecimal.valueOf(11.1)),
                createFundFactorValueObject(batchFundCode, "B_FACTOR_2", LocalDate.of(2023, 3, 1), BigDecimal.valueOf(22.2)),
                createFundFactorValueObject(batchFundCode, "B_FACTOR_3", LocalDate.of(2023, 3, 2), BigDecimal.valueOf(33.3))
        );

        int affectedRows = fundFactorValueMapper.batchInsert(batchList);
        assertEquals(3, affectedRows, "应该批量插入3条记录"); // 如果有ON DUPLICATE KEY UPDATE，这里可能是实际插入/更新的数量

        // 验证插入结果
        List<FundFactorValue> foundList = fundFactorValueMapper.findByFundCode(batchFundCode);
        assertNotNull(foundList);
        assertEquals(3, foundList.size());
        assertTrue(foundList.stream().anyMatch(f -> f.getFactorCode().equals("B_FACTOR_1") && f.getValue().compareTo(BigDecimal.valueOf(11.1)) == 0));
    }

    @Test
    @DisplayName("batchInsert - 批量插入包含重复数据（ON DUPLICATE KEY UPDATE 行为）")
    void testBatchInsert_WithDuplicates() {
        // 先插入一条数据
        FundFactorValue existing = insertFactorValue("DUPLICATE_FUND", "DUP_FACTOR", LocalDate.of(2023, 4, 1), BigDecimal.valueOf(1.0));

        // 准备批量插入，其中包含一条重复数据和一条新数据
        List<FundFactorValue> batchList = Arrays.asList(
                createFundFactorValueObject("DUPLICATE_FUND", "DUP_FACTOR", LocalDate.of(2023, 4, 1), BigDecimal.valueOf(5.0)), // 重复
                createFundFactorValueObject("DUPLICATE_FUND", "NEW_DUP_FACTOR", LocalDate.of(2023, 4, 1), BigDecimal.valueOf(10.0)) // 新数据
        );

        // 如果 XML 中有 ON DUPLICATE KEY UPDATE，affectedRows 会是插入的数量 + 更新的数量
        // 对于MySQL，插入算1，更新算2（如果实际值有变化），如果没有变化算0。H2可能是插入算1，更新算1。
        // 这里我们期望至少影响一条新数据。
        int affectedRows = fundFactorValueMapper.batchInsert(batchList);
        assertTrue(affectedRows >= 1, "批量插入应至少影响一条记录");

        // 验证数据状态
        FundFactorValue updatedDuplicate = fundFactorValueMapper.findByFundAndFactor("DUPLICATE_FUND", "DUP_FACTOR");
        assertNotNull(updatedDuplicate);
        assertEquals(BigDecimal.valueOf(5.0).stripTrailingZeros(), updatedDuplicate.getValue().stripTrailingZeros(), "重复数据的值应该被更新");

        FundFactorValue newDuplicate = fundFactorValueMapper.findByFundAndFactor("DUPLICATE_FUND", "NEW_DUP_FACTOR");
        assertNotNull(newDuplicate, "新数据应该被插入");
    }


    // 辅助方法，用于创建 FundFactorValue 对象，避免重复代码
    private FundFactorValue createFundFactorValueObject(String fundCode, String factorCode, LocalDate tradeDate, BigDecimal value) {
        FundFactorValue ffv = new FundFactorValue();
        ffv.setFundCode(fundCode);
        ffv.setFactorCode(factorCode);
        ffv.setTradeDate(tradeDate);
        ffv.setValue(value);
        return ffv;
    }
}