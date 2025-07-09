package com.advisor.mapper;

import com.advisor.entity.FundNetValue;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FundNetValueMapperTest {

    @Autowired
    private FundNetValueMapper fundNetValueMapper;

    // 定义用于测试的基金ID
    private Long fundId1 = 101L;
    private Long fundId2 = 102L;

    // 辅助方法：创建并插入一个 FundNetValue 对象
    private FundNetValue createAndInsertNetValue(Long fundId, LocalDate tradeDate, BigDecimal unitNetValue, BigDecimal accumulatedNetValue, BigDecimal dailyReturn) {
        FundNetValue netValue = new FundNetValue();
        netValue.setFundId(fundId);
        netValue.setTradeDate(tradeDate);
        netValue.setUnitNetValue(unitNetValue);
        netValue.setAccumulatedNetValue(accumulatedNetValue);
        netValue.setDailyReturn(dailyReturn);
        fundNetValueMapper.insert(netValue);
        assertNotNull(netValue.getId(), "插入后ID不应为空");
        return netValue;
    }

    @BeforeEach
    void setUp() {
        // 插入一些测试数据
        // Fund 101 的净值数据
        createAndInsertNetValue(fundId1, LocalDate.of(2023, 1, 1), BigDecimal.valueOf(1.0000), BigDecimal.valueOf(1.0000), BigDecimal.valueOf(0.0000));
        createAndInsertNetValue(fundId1, LocalDate.of(2023, 1, 2), BigDecimal.valueOf(1.0100), BigDecimal.valueOf(1.0100), BigDecimal.valueOf(0.0100));
        createAndInsertNetValue(fundId1, LocalDate.of(2023, 1, 3), BigDecimal.valueOf(1.0050), BigDecimal.valueOf(1.0050), BigDecimal.valueOf(-0.0050));
        createAndInsertNetValue(fundId1, LocalDate.of(2023, 2, 1), BigDecimal.valueOf(1.0200), BigDecimal.valueOf(1.0200), BigDecimal.valueOf(0.0150));
        createAndInsertNetValue(fundId1, LocalDate.of(2023, 2, 2), BigDecimal.valueOf(1.0300), BigDecimal.valueOf(1.0300), BigDecimal.valueOf(0.0100));

        // Fund 102 的净值数据
        createAndInsertNetValue(fundId2, LocalDate.of(2023, 1, 15), BigDecimal.valueOf(2.0000), BigDecimal.valueOf(2.0000), BigDecimal.valueOf(0.0000));
        createAndInsertNetValue(fundId2, LocalDate.of(2023, 1, 16), BigDecimal.valueOf(2.0200), BigDecimal.valueOf(2.0200), BigDecimal.valueOf(0.0100));
    }

    @Test
    @DisplayName("insert - 插入基金净值成功")
    void testInsert() {
        Long newFundId = 103L;
        LocalDate newTradeDate = LocalDate.now();
        BigDecimal newUnitNetValue = BigDecimal.valueOf(3.0000);
        BigDecimal newAccumulatedNetValue = BigDecimal.valueOf(3.0000);
        BigDecimal newDailyReturn = BigDecimal.valueOf(0.0050);

        FundNetValue newNetValue = new FundNetValue();
        newNetValue.setFundId(newFundId);
        newNetValue.setTradeDate(newTradeDate);
        newNetValue.setUnitNetValue(newUnitNetValue);
        newNetValue.setAccumulatedNetValue(newAccumulatedNetValue);
        newNetValue.setDailyReturn(newDailyReturn);

        int affectedRows = fundNetValueMapper.insert(newNetValue);
        assertEquals(1, affectedRows, "应该插入一条记录");
        assertNotNull(newNetValue.getId(), "插入后ID不应为空");

        FundNetValue found = fundNetValueMapper.findById(newNetValue.getId());
        assertNotNull(found, "应该能通过ID找到新插入的净值数据");
        assertEquals(newFundId, found.getFundId());
        assertEquals(newTradeDate, found.getTradeDate());
        assertEquals(newUnitNetValue.stripTrailingZeros(), found.getUnitNetValue().stripTrailingZeros());
    }

    @Test
    @DisplayName("insert - 插入重复基金ID和日期组合应抛出异常")
    void testInsert_DuplicateFundIdAndDate() {
        // 尝试插入一个与 setUp 中已存在的完全相同 (fundId, tradeDate) 的记录
        assertThrows(DataIntegrityViolationException.class, () -> {
            createAndInsertNetValue(fundId1, LocalDate.of(2023, 1, 1), BigDecimal.valueOf(9.9999), BigDecimal.valueOf(9.9999), BigDecimal.ZERO);
        }, "插入重复的基金ID和日期组合应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("findByFundId - 按基金ID和日期范围查询，带分页")
    void testFindByFundId_WithDateRangeAndPagination() {
        List<FundNetValue> list = fundNetValueMapper.findByFundId(fundId1, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 3), 0, 10);
        assertNotNull(list);
        assertEquals(3, list.size()); // 1月1,2,3日

        // 验证排序是否按日期倒序
        assertEquals(LocalDate.of(2023, 1, 3), list.get(0).getTradeDate());
        assertEquals(LocalDate.of(2023, 1, 2), list.get(1).getTradeDate());
        assertEquals(LocalDate.of(2023, 1, 1), list.get(2).getTradeDate());

        // 测试分页
        list = fundNetValueMapper.findByFundId(fundId1, null, null, 0, 2);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(LocalDate.of(2023, 2, 2), list.get(0).getTradeDate()); // 最新两天
        assertEquals(LocalDate.of(2023, 2, 1), list.get(1).getTradeDate());

        list = fundNetValueMapper.findByFundId(fundId1, null, null, 2, 2);
        assertNotNull(list);
        assertEquals(2, list.size());
        assertEquals(LocalDate.of(2023, 1, 3), list.get(0).getTradeDate());
        assertEquals(LocalDate.of(2023, 1, 2), list.get(1).getTradeDate());
    }

    @Test
    @DisplayName("findByFundId - 无匹配结果")
    void testFindByFundId_NoMatch() {
        List<FundNetValue> list = fundNetValueMapper.findByFundId(999L, null, null, 0, 10);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("findByFundIdAndDate - 根据基金ID和日期查询净值")
    void testFindByFundIdAndDate() {
        FundNetValue found = fundNetValueMapper.findByFundIdAndDate(fundId1, LocalDate.of(2023, 1, 2));
        assertNotNull(found);
        assertEquals(fundId1, found.getFundId());
        assertEquals(LocalDate.of(2023, 1, 2), found.getTradeDate());
        assertEquals(BigDecimal.valueOf(1.0100).stripTrailingZeros(), found.getUnitNetValue().stripTrailingZeros());

        FundNetValue notFound = fundNetValueMapper.findByFundIdAndDate(fundId1, LocalDate.of(2023, 1, 4)); // 不存在的日期
        assertNull(notFound);

        notFound = fundNetValueMapper.findByFundIdAndDate(999L, LocalDate.of(2023, 1, 1)); // 不存在的基金ID
        assertNull(notFound);
    }

    @Test
    @DisplayName("findLatestByFundId - 查询基金最新净值")
    void testFindLatestByFundId() {
        FundNetValue latest = fundNetValueMapper.findLatestByFundId(fundId1);
        assertNotNull(latest);
        assertEquals(fundId1, latest.getFundId());
        assertEquals(LocalDate.of(2023, 2, 2), latest.getTradeDate());
        assertEquals(BigDecimal.valueOf(1.0300).stripTrailingZeros(), latest.getUnitNetValue().stripTrailingZeros());

        FundNetValue latest2 = fundNetValueMapper.findLatestByFundId(fundId2);
        assertNotNull(latest2);
        assertEquals(fundId2, latest2.getFundId());
        assertEquals(LocalDate.of(2023, 1, 16), latest2.getTradeDate());
        assertEquals(BigDecimal.valueOf(2.0200).stripTrailingZeros(), latest2.getUnitNetValue().stripTrailingZeros());

        FundNetValue notFound = fundNetValueMapper.findLatestByFundId(999L);
        assertNull(notFound);
    }

    @Test
    @DisplayName("update - 更新基金净值")
    void testUpdate() {
        // 查找一条记录进行更新
        FundNetValue netValueToUpdate = fundNetValueMapper.findByFundIdAndDate(fundId1, LocalDate.of(2023, 1, 1));
        assertNotNull(netValueToUpdate);

        // 更新字段
        netValueToUpdate.setUnitNetValue(BigDecimal.valueOf(1.0005));
        netValueToUpdate.setDailyReturn(BigDecimal.valueOf(0.0005));
        netValueToUpdate.setAccumulatedNetValue(BigDecimal.valueOf(1.0005));

        int affectedRows = fundNetValueMapper.update(netValueToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        // 验证更新是否成功
        FundNetValue updated = fundNetValueMapper.findById(netValueToUpdate.getId());
        assertNotNull(updated);
        assertEquals(BigDecimal.valueOf(1.0005).stripTrailingZeros(), updated.getUnitNetValue().stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(0.0005).stripTrailingZeros(), updated.getDailyReturn().stripTrailingZeros());
        assertEquals(BigDecimal.valueOf(1.0005).stripTrailingZeros(), updated.getAccumulatedNetValue().stripTrailingZeros());
    }

    @Test
    @DisplayName("update - 更新不存在的ID不影响任何记录")
    void testUpdate_NotFound() {
        FundNetValue nonExistent = new FundNetValue();
        nonExistent.setId(99999L);
        nonExistent.setFundId(1L);
        nonExistent.setTradeDate(LocalDate.now());
        nonExistent.setUnitNetValue(BigDecimal.ONE);
        nonExistent.setAccumulatedNetValue(BigDecimal.ONE);
        nonExistent.setDailyReturn(BigDecimal.ZERO);

        int affectedRows = fundNetValueMapper.update(nonExistent);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteById - 删除基金净值")
    void testDeleteById() {
        FundNetValue netValueToDelete = createAndInsertNetValue(fundId1, LocalDate.of(2023, 3, 1), BigDecimal.valueOf(1.1), BigDecimal.valueOf(1.1), BigDecimal.ZERO);
        Long idToDelete = netValueToDelete.getId();

        int affectedRows = fundNetValueMapper.deleteById(idToDelete);
        assertEquals(1, affectedRows, "应该删除一条记录");

        FundNetValue deleted = fundNetValueMapper.findById(idToDelete);
        assertNull(deleted, "删除后不应该能找到净值数据");
    }

    @Test
    @DisplayName("deleteById - 删除不存在的ID不影响任何记录")
    void testDeleteById_NotFound() {
        int affectedRows = fundNetValueMapper.deleteById(99999L);
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("batchInsert - 批量插入基金净值")
    void testBatchInsert() {
        Long batchFundId = 104L;
        List<FundNetValue> batchList = Arrays.asList(
                createFundNetValueObject(batchFundId, LocalDate.of(2023, 4, 1), BigDecimal.valueOf(1.50), BigDecimal.valueOf(1.50), BigDecimal.valueOf(0.005)),
                createFundNetValueObject(batchFundId, LocalDate.of(2023, 4, 2), BigDecimal.valueOf(1.51), BigDecimal.valueOf(1.51), BigDecimal.valueOf(0.010)),
                createFundNetValueObject(batchFundId, LocalDate.of(2023, 4, 3), BigDecimal.valueOf(1.49), BigDecimal.valueOf(1.49), BigDecimal.valueOf(-0.020))
        );

        int affectedRows = fundNetValueMapper.batchInsert(batchList);
        // 如果XML的batchInsert使用了 ON DUPLICATE KEY UPDATE，则AffectedRows可能表示插入或更新的总行数
        // 对于H2，如果ON DUPLICATE KEY UPDATE且数据实际更新，影响行数通常是1。如果插入，影响行数是1。
        // 所以对于3条新数据，affectedRows 通常是3。
        assertEquals(3, affectedRows, "应该批量插入3条记录");

        // 验证插入结果
        List<FundNetValue> foundList = fundNetValueMapper.findByFundId(batchFundId, null, null, 0, 10);
        assertNotNull(foundList);
        assertEquals(3, foundList.size());
        // 验证特定数据是否存在
        assertTrue(foundList.stream().anyMatch(f -> f.getTradeDate().equals(LocalDate.of(2023, 4, 1)) && f.getUnitNetValue().compareTo(BigDecimal.valueOf(1.50)) == 0));
    }

    @Test
    @DisplayName("batchInsert - 批量插入包含重复数据（ON DUPLICATE KEY UPDATE 行为）")
    void testBatchInsert_WithDuplicates() {
        // 插入一条现有数据
        FundNetValue existing = createAndInsertNetValue(105L, LocalDate.of(2023, 5, 1), BigDecimal.valueOf(1.0), BigDecimal.valueOf(1.0), BigDecimal.ZERO);

        // 准备批量插入，其中包含一条重复数据（更新其值）和一条新数据
        List<FundNetValue> batchList = Arrays.asList(
                createFundNetValueObject(105L, LocalDate.of(2023, 5, 1), BigDecimal.valueOf(1.05), BigDecimal.valueOf(1.05), BigDecimal.valueOf(0.05)), // 重复的日期，更新值
                createFundNetValueObject(105L, LocalDate.of(2023, 5, 2), BigDecimal.valueOf(1.06), BigDecimal.valueOf(1.06), BigDecimal.valueOf(0.01))  // 新数据
        );

        int affectedRows = fundNetValueMapper.batchInsert(batchList);
        // 对于H2，ON DUPLICATE KEY UPDATE，一条插入算1，一条更新算1。所以这里预期影响2条
        assertEquals(2, affectedRows, "批量插入应影响2条记录（1插入1更新）");

        // 验证数据状态
        FundNetValue updatedDuplicate = fundNetValueMapper.findByFundIdAndDate(105L, LocalDate.of(2023, 5, 1));
        assertNotNull(updatedDuplicate);
        assertEquals(BigDecimal.valueOf(1.05).stripTrailingZeros(), updatedDuplicate.getUnitNetValue().stripTrailingZeros(), "重复数据的值应该被更新");

        FundNetValue newEntry = fundNetValueMapper.findByFundIdAndDate(105L, LocalDate.of(2023, 5, 2));
        assertNotNull(newEntry, "新数据应该被插入");
    }

    @Test
    @DisplayName("getAvgNetValueByMonth - 获取指定月份的平均净值")
    void testGetAvgNetValueByMonth() {
        // 基金101在2023年1月有3条数据: 1.0000, 1.0100, 1.0050
        // (1.0000 + 1.0100 + 1.0050) / 3 = 1.0050
        Double avgJanuary = fundNetValueMapper.getAvgNetValueByMonth(2023, 1);
        assertNotNull(avgJanuary);
        // 使用 delta 进行浮点数比较
        assertEquals(1.0050, avgJanuary, 0.0001, "2023年1月平均净值应为1.0050");

        // 基金101在2023年2月有2条数据: 1.0200, 1.0300
        // (1.0200 + 1.0300) / 2 = 1.0250
        Double avgFebruary = fundNetValueMapper.getAvgNetValueByMonth(2023, 2);
        assertNotNull(avgFebruary);
        assertEquals(1.0250, avgFebruary, 0.0001, "2023年2月平均净值应为1.0250");

        // 查询没有数据的月份
        Double avgMarch = fundNetValueMapper.getAvgNetValueByMonth(2023, 3);
        assertNull(avgMarch, "没有数据的月份平均净值应为null"); // AVG函数在无数据时返回NULL
    }

    @Test
    @DisplayName("findByFundIdAndDateRange - 根据基金ID和字符串日期范围查询")
    void testFindByFundIdAndDateRange() {
        List<FundNetValue> list = fundNetValueMapper.findByFundIdAndDateRange(
                fundId1, "2023-01-01", "2023-01-31");
        assertNotNull(list);
        assertEquals(3, list.size()); // 1月1,2,3日
        // 验证排序是否按日期升序
        List<LocalDate> dates = list.stream().map(FundNetValue::getTradeDate).collect(Collectors.toList());
        assertEquals(Arrays.asList(LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 2), LocalDate.of(2023, 1, 3)), dates);

        list = fundNetValueMapper.findByFundIdAndDateRange(
                fundId1, "2023-02-01", "2023-02-28");
        assertNotNull(list);
        assertEquals(2, list.size()); // 2月1,2日
        dates = list.stream().map(FundNetValue::getTradeDate).collect(Collectors.toList());
        assertEquals(Arrays.asList(LocalDate.of(2023, 2, 1), LocalDate.of(2023, 2, 2)), dates);

        list = fundNetValueMapper.findByFundIdAndDateRange(
                fundId1, "2023-03-01", "2023-03-31"); // 没有数据的日期范围
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // 辅助方法，用于创建 FundNetValue 对象，避免重复代码
    private FundNetValue createFundNetValueObject(Long fundId, LocalDate tradeDate, BigDecimal unitNetValue, BigDecimal accumulatedNetValue, BigDecimal dailyReturn) {
        FundNetValue ffv = new FundNetValue();
        ffv.setFundId(fundId);
        ffv.setTradeDate(tradeDate);
        ffv.setUnitNetValue(unitNetValue);
        ffv.setAccumulatedNetValue(accumulatedNetValue);
        ffv.setDailyReturn(dailyReturn);
        return ffv;
    }
}