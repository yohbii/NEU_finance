package com.advisor.mapper;

import com.advisor.entity.FactorAnalysisResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException; // 用于测试完整性约束

import java.time.LocalDateTime;
import java.util.UUID; // 用于生成唯一的analysisId

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
// 使用内存数据库 H2 进行测试，或者如果您想使用真实的数据库，可以将 replace 设置为 NONE
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class FactorAnalysisResultMapperTest {

    @Autowired
    private FactorAnalysisResultMapper factorAnalysisResultMapper;

    // 可以选择在每个测试方法前清空表，确保测试的独立性
    // 或者通过 SQL 脚本自动初始化测试数据库
    // 对于 @MybatisTest 结合 @AutoConfigureTestDatabase(replace = ANY)
    // 每次测试都会创建一个新的内存数据库，所以通常不需要手动清空表
    @BeforeEach
    void setUp() {
        // 可以在这里插入一些初始数据，如果某些测试依赖于预设数据
        // 例如：factorAnalysisResultMapper.insert(someInitialData);
    }

    @Test
    @DisplayName("insert - 插入分析结果成功")
    void testInsert() {
        FactorAnalysisResult result = new FactorAnalysisResult();
        String analysisId = UUID.randomUUID().toString(); // 生成一个唯一的ID
        result.setAnalysisId(analysisId);
        result.setFundId(1L);
        result.setFactorType("风格因子");
        result.setAnalysisDate(LocalDateTime.now());
        result.setResultData("{\"key\": \"value\"}"); // 假设是JSON字符串
        result.setStatus("COMPLETED");
        result.setCreatedAt(LocalDateTime.now());
        result.setUpdatedAt(LocalDateTime.now());

        int affectedRows = factorAnalysisResultMapper.insert(result);
        assertEquals(1, affectedRows, "应该插入一条记录");

        // 验证是否成功插入
        FactorAnalysisResult foundResult = factorAnalysisResultMapper.findByAnalysisId(analysisId);
        assertNotNull(foundResult, "应该能找到插入的结果");
        assertEquals(analysisId, foundResult.getAnalysisId());
        assertEquals(result.getFundId(), foundResult.getFundId());
        assertEquals(result.getFactorType(), foundResult.getFactorType());
        assertEquals(result.getResultData(), foundResult.getResultData());
        assertEquals(result.getStatus(), foundResult.getStatus());
        // 对于 LocalDateTime 比较，可能需要考虑数据库存储的精度，或者只比较关键部分
        // 例如：assertTrue(result.getAnalysisDate().isEqual(foundResult.getAnalysisDate()));
    }

    @Test
    @DisplayName("findByAnalysisId - 根据ID查询结果")
    void testFindByAnalysisId() {
        // 先插入一条数据用于查询
        FactorAnalysisResult resultToInsert = new FactorAnalysisResult();
        String analysisId = UUID.randomUUID().toString();
        resultToInsert.setAnalysisId(analysisId);
        resultToInsert.setFundId(2L);
        resultToInsert.setFactorType("行业因子");
        resultToInsert.setAnalysisDate(LocalDateTime.now());
        resultToInsert.setResultData("{\"industry\": \"tech\"}");
        resultToInsert.setStatus("PROCESSING");
        resultToInsert.setCreatedAt(LocalDateTime.now());
        resultToInsert.setUpdatedAt(LocalDateTime.now());
        factorAnalysisResultMapper.insert(resultToInsert);

        // 执行查询
        FactorAnalysisResult foundResult = factorAnalysisResultMapper.findByAnalysisId(analysisId);
        assertNotNull(foundResult, "应该能找到结果");
        assertEquals(analysisId, foundResult.getAnalysisId());
        assertEquals(resultToInsert.getFundId(), foundResult.getFundId());

        // 查询不存在的ID
        FactorAnalysisResult notFoundResult = factorAnalysisResultMapper.findByAnalysisId("nonexistent_id");
        assertNull(notFoundResult, "不应该找到不存在的ID的结果");
    }

    @Test
    @DisplayName("updateByAnalysisId - 更新分析结果")
    void testUpdateByAnalysisId() {
        // 先插入一条数据用于更新
        FactorAnalysisResult resultToUpdate = new FactorAnalysisResult();
        String analysisId = UUID.randomUUID().toString();
        resultToUpdate.setAnalysisId(analysisId);
        resultToUpdate.setFundId(3L);
        resultToUpdate.setFactorType("初始类型");
        resultToUpdate.setAnalysisDate(LocalDateTime.now());
        resultToUpdate.setResultData("{\"initial\": \"data\"}");
        resultToUpdate.setStatus("PENDING");
        resultToUpdate.setCreatedAt(LocalDateTime.now());
        resultToUpdate.setUpdatedAt(LocalDateTime.now());
        factorAnalysisResultMapper.insert(resultToUpdate);

        // 准备更新数据
        resultToUpdate.setFactorType("更新类型");
        resultToUpdate.setResultData("{\"updated\": \"data\"}");
        resultToUpdate.setStatus("COMPLETED");
        resultToUpdate.setUpdatedAt(LocalDateTime.now().plusHours(1)); // 更新时间也应改变

        int affectedRows = factorAnalysisResultMapper.updateByAnalysisId(resultToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        // 验证更新是否成功
        FactorAnalysisResult updatedResult = factorAnalysisResultMapper.findByAnalysisId(analysisId);
        assertNotNull(updatedResult);
        assertEquals("更新类型", updatedResult.getFactorType());
        assertEquals("COMPLETED", updatedResult.getStatus());
        assertEquals("{\"updated\": \"data\"}", updatedResult.getResultData());
        // 确保其他字段未被意外修改
        assertEquals(resultToUpdate.getFundId(), updatedResult.getFundId());
    }

    @Test
    @DisplayName("updateByAnalysisId - 更新不存在的ID不影响任何记录")
    void testUpdateByAnalysisId_NotFound() {
        FactorAnalysisResult result = new FactorAnalysisResult();
        result.setAnalysisId("nonexistent_id_for_update");
        result.setFundId(99L);
        result.setFactorType("任意类型");
        result.setResultData("任意数据");
        result.setStatus("任意状态");
        result.setUpdatedAt(LocalDateTime.now());

        int affectedRows = factorAnalysisResultMapper.updateByAnalysisId(result);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }


    @Test
    @DisplayName("deleteByAnalysisId - 删除分析结果")
    void testDeleteByAnalysisId() {
        // 先插入一条数据用于删除
        FactorAnalysisResult resultToDelete = new FactorAnalysisResult();
        String analysisId = UUID.randomUUID().toString();
        resultToDelete.setAnalysisId(analysisId);
        resultToDelete.setFundId(4L);
        resultToDelete.setFactorType("待删除");
        resultToDelete.setAnalysisDate(LocalDateTime.now());
        resultToDelete.setResultData("{\"to\": \"delete\"}");
        resultToDelete.setStatus("COMPLETED");
        resultToDelete.setCreatedAt(LocalDateTime.now());
        resultToDelete.setUpdatedAt(LocalDateTime.now());
        factorAnalysisResultMapper.insert(resultToDelete);

        // 执行删除
        int affectedRows = factorAnalysisResultMapper.deleteByAnalysisId(analysisId);
        assertEquals(1, affectedRows, "应该删除一条记录");

        // 验证是否已删除
        FactorAnalysisResult deletedResult = factorAnalysisResultMapper.findByAnalysisId(analysisId);
        assertNull(deletedResult, "删除后不应该能找到结果");
    }

    @Test
    @DisplayName("deleteByAnalysisId - 删除不存在的ID不影响任何记录")
    void testDeleteByAnalysisId_NotFound() {
        int affectedRows = factorAnalysisResultMapper.deleteByAnalysisId("nonexistent_id_for_delete");
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    // 额外的测试：测试插入重复ID（如果数据库层面analysisId是唯一约束）
    @Test
    @DisplayName("insert - 插入重复analysisId应抛出异常")
    void testInsert_DuplicateAnalysisId() {
        FactorAnalysisResult result1 = new FactorAnalysisResult();
        String analysisId = UUID.randomUUID().toString();
        result1.setAnalysisId(analysisId);
        result1.setFundId(5L);
        result1.setFactorType("重复测试");
        result1.setAnalysisDate(LocalDateTime.now());
        result1.setResultData("{}");
        result1.setStatus("NEW");
        result1.setCreatedAt(LocalDateTime.now());
        result1.setUpdatedAt(LocalDateTime.now());
        factorAnalysisResultMapper.insert(result1); // 第一次插入

        FactorAnalysisResult result2 = new FactorAnalysisResult();
        result2.setAnalysisId(analysisId); // 使用相同的ID
        result2.setFundId(6L); // 其他字段可以不同
        result2.setFactorType("重复测试2");
        result2.setAnalysisDate(LocalDateTime.now());
        result2.setResultData("{}");
        result2.setStatus("NEW");
        result2.setCreatedAt(LocalDateTime.now());
        result2.setUpdatedAt(LocalDateTime.now());

        // 假设 analysisId 在数据库中定义了唯一约束（例如是主键或唯一索引），
        // 那么再次插入相同ID时，会抛出 DataIntegrityViolationException
        assertThrows(DataIntegrityViolationException.class, () -> {
            factorAnalysisResultMapper.insert(result2);
        }, "插入重复的analysisId应该抛出DataIntegrityViolationException");
    }
}