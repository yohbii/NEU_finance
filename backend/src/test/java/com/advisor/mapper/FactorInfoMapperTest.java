package com.advisor.mapper;

import com.advisor.entity.FactorInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException; // 用于测试唯一约束
import org.springframework.dao.DuplicateKeyException; // 也可以是这个异常，取决于具体驱动和MyBatis版本

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 使用内存H2数据库
class FactorInfoMapperTest {

    @Autowired
    private FactorInfoMapper factorInfoMapper;

    // 辅助方法：创建并插入一个 FactorInfo 对象
    private FactorInfo createAndInsertFactor(String factorCode, String factorName, String factorType, String category, String description) {
        FactorInfo factor = new FactorInfo();
        factor.setFactorCode(factorCode);
        factor.setFactorName(factorName);
        factor.setFactorType(factorType);
        factor.setCategory(category);
        factor.setDescription(description);
        factor.setCreatedAt(LocalDateTime.now());
        factor.setUpdatedAt(LocalDateTime.now());
        factorInfoMapper.insert(factor);
        assertNotNull(factor.getId(), "插入后ID不应为空"); // 确保自增ID被设置
        return factor;
    }

    // 在每个测试方法执行前，清空表并准备一些基础数据
    // 由于 @AutoConfigureTestDatabase(replace = ANY) 会为每个测试创建一个新数据库，
    // 所以这里的 @BeforeEach 更多是为了确保每个测试的初始状态相同，或者插入测试所需的基础数据。
    @BeforeEach
    void setUp() {
        // 插入一些测试数据
        createAndInsertFactor("FACTOR_A", "因子A名称", "类型1", "分类X", "描述A");
        createAndInsertFactor("FACTOR_B", "因子B名称", "类型1", "分类Y", "描述B");
        createAndInsertFactor("FACTOR_C", "因子C名称", "类型2", "分类X", "描述C");
        createAndInsertFactor("FACTOR_D", "因子D名称", "类型2", "分类Z", "描述D");
    }


    @Test
    @DisplayName("insert - 插入因子信息成功")
    void testInsert() {
        FactorInfo newFactor = new FactorInfo();
        newFactor.setFactorCode("NEW_FACTOR_CODE");
        newFactor.setFactorName("新因子名称");
        newFactor.setFactorType("新类型");
        newFactor.setCategory("新分类");
        newFactor.setDescription("这是新因子的描述");
        newFactor.setCreatedAt(LocalDateTime.now());
        newFactor.setUpdatedAt(LocalDateTime.now());

        int affectedRows = factorInfoMapper.insert(newFactor);
        assertEquals(1, affectedRows, "应该插入一条记录");
        assertNotNull(newFactor.getId(), "插入后实体ID不应为空");

        FactorInfo found = factorInfoMapper.findById(newFactor.getId());
        assertNotNull(found, "应该能通过ID找到新插入的因子");
        assertEquals("NEW_FACTOR_CODE", found.getFactorCode());
        assertEquals("新因子名称", found.getFactorName());
    }

    @Test
    @DisplayName("insert - 插入重复因子代码应抛出异常")
    void testInsert_DuplicateFactorCode() {
        FactorInfo factor = new FactorInfo();
        factor.setFactorCode("FACTOR_A"); // 使用已存在代码
        factor.setFactorName("另一个名称"); // 名称不同
        factor.setFactorType("类型");
        factor.setCategory("分类");
        factor.setDescription("描述");
        factor.setCreatedAt(LocalDateTime.now());
        factor.setUpdatedAt(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> {
            factorInfoMapper.insert(factor);
        }, "插入重复因子代码应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("insert - 插入重复因子名称应抛出异常")
    void testInsert_DuplicateFactorName() {
        FactorInfo factor = new FactorInfo();
        factor.setFactorCode("UNIQUE_CODE_X");
        factor.setFactorName("因子A名称"); // 使用已存在名称
        factor.setFactorType("类型");
        factor.setCategory("分类");
        factor.setDescription("描述");
        factor.setCreatedAt(LocalDateTime.now());
        factor.setUpdatedAt(LocalDateTime.now());

        assertThrows(DataIntegrityViolationException.class, () -> {
            factorInfoMapper.insert(factor);
        }, "插入重复因子名称应抛出DataIntegrityViolationException");
    }

    @Test
    @DisplayName("findList - 按类型和分类查询")
    void testFindList_ByTypeAndCategory() {
        List<FactorInfo> list = factorInfoMapper.findList("类型1", "分类X", null, 0, 10);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("因子A名称", list.get(0).getFactorName());
    }

    @Test
    @DisplayName("findList - 按关键词查询")
    void testFindList_ByKeyword() {
        List<FactorInfo> list = factorInfoMapper.findList(null, null, "因子B", 0, 10);
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("因子B名称", list.get(0).getFactorName());

        list = factorInfoMapper.findList(null, null, "描述", 0, 10);
        assertNotNull(list);
        assertEquals(4, list.size()); // 所有描述都包含“描述”
    }

    @Test
    @DisplayName("findList - 分页查询")
    void testFindList_Pagination() {
        List<FactorInfo> list = factorInfoMapper.findList(null, null, null, 0, 2);
        assertNotNull(list);
        assertEquals(2, list.size());

        list = factorInfoMapper.findList(null, null, null, 2, 2);
        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    @DisplayName("findList - 查询无匹配结果")
    void testFindList_NoMatch() {
        List<FactorInfo> list = factorInfoMapper.findList("不存在的类型", null, null, 0, 10);
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    @DisplayName("countList - 统计总数")
    void testCountList() {
        Long count = factorInfoMapper.countList(null, null, null);
        assertEquals(4L, count, "总数应为4");

        count = factorInfoMapper.countList("类型1", null, null);
        assertEquals(2L, count, "类型1的总数应为2");

        count = factorInfoMapper.countList(null, "分类X", null);
        assertEquals(2L, count, "分类X的总数应为2");

        count = factorInfoMapper.countList(null, null, "描述A");
        assertEquals(1L, count, "关键词'描述A'的总数应为1");
    }

    @Test
    @DisplayName("findById - 根据ID查询因子")
    void testFindById() {
        FactorInfo factor = createAndInsertFactor("TEMP_ID", "临时名称", "临时", "临时", "临时描述");
        Long id = factor.getId();

        FactorInfo found = factorInfoMapper.findById(id);
        assertNotNull(found, "应该能找到插入的因子");
        assertEquals("临时名称", found.getFactorName());

        FactorInfo notFound = factorInfoMapper.findById(9999L); // 不存在的ID
        assertNull(notFound, "不应该找到不存在ID的因子");
    }

    @Test
    @DisplayName("findByFactorCode - 根据因子代码查询因子")
    void testFindByFactorCode() {
        FactorInfo found = factorInfoMapper.findByFactorCode("FACTOR_B");
        assertNotNull(found, "应该能找到因子B");
        assertEquals("因子B名称", found.getFactorName());

        FactorInfo notFound = factorInfoMapper.findByFactorCode("NON_EXISTENT_CODE");
        assertNull(notFound, "不应该找到不存在代码的因子");
    }

    @Test
    @DisplayName("findByName - 根据因子名称查询因子")
    void testFindByName() {
        FactorInfo found = factorInfoMapper.findByName("因子C名称");
        assertNotNull(found, "应该能找到因子C");
        assertEquals("类型2", found.getFactorType());

        FactorInfo notFound = factorInfoMapper.findByName("不存在的名称");
        assertNull(notFound, "不应该找到不存在名称的因子");
    }

    @Test
    @DisplayName("update - 更新因子信息")
    void testUpdate() {
        FactorInfo factorToUpdate = factorInfoMapper.findByFactorCode("FACTOR_A");
        assertNotNull(factorToUpdate);

        factorToUpdate.setFactorName("更新后的因子A名称");
        factorToUpdate.setCategory("更新分类");
        factorToUpdate.setUpdatedAt(LocalDateTime.now().plusHours(1));

        int affectedRows = factorInfoMapper.update(factorToUpdate);
        assertEquals(1, affectedRows, "应该更新一条记录");

        FactorInfo updated = factorInfoMapper.findById(factorToUpdate.getId());
        assertNotNull(updated);
        assertEquals("更新后的因子A名称", updated.getFactorName());
        assertEquals("更新分类", updated.getCategory());
        // 验证其他字段未受影响
        assertEquals("FACTOR_A", updated.getFactorCode());
    }

    @Test
    @DisplayName("update - 更新不存在的因子不影响任何记录")
    void testUpdate_NotFound() {
        FactorInfo nonExistentFactor = new FactorInfo();
        nonExistentFactor.setId(9999L);
        nonExistentFactor.setFactorCode("NON_EXISTENT");
        nonExistentFactor.setFactorName("Non Existent");
        nonExistentFactor.setFactorType("Type");
        nonExistentFactor.setCategory("Category");
        nonExistentFactor.setDescription("Description");
        nonExistentFactor.setUpdatedAt(LocalDateTime.now());

        int affectedRows = factorInfoMapper.update(nonExistentFactor);
        assertEquals(0, affectedRows, "更新不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("deleteById - 删除因子信息")
    void testDeleteById() {
        FactorInfo factorToDelete = factorInfoMapper.findByFactorCode("FACTOR_D");
        assertNotNull(factorToDelete);
        Long idToDelete = factorToDelete.getId();

        int affectedRows = factorInfoMapper.deleteById(idToDelete);
        assertEquals(1, affectedRows, "应该删除一条记录");

        FactorInfo deleted = factorInfoMapper.findById(idToDelete);
        assertNull(deleted, "删除后不应该能找到因子");
    }

    @Test
    @DisplayName("deleteById - 删除不存在的ID不影响任何记录")
    void testDeleteById_NotFound() {
        int affectedRows = factorInfoMapper.deleteById(9999L); // 不存在的ID
        assertEquals(0, affectedRows, "删除不存在的ID不应该影响任何记录");
    }

    @Test
    @DisplayName("findAllFactorTypes - 查询所有因子类型")
    void testFindAllFactorTypes() {
        List<String> types = factorInfoMapper.findAllFactorTypes();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        // 预期 '类型1', '类型2', '新类型' (来自 insert 测试)
        assertTrue(types.contains("类型1"));
        assertTrue(types.contains("类型2"));
        assertEquals(2, types.size()); // setUp中只有两种类型
    }

    @Test
    @DisplayName("findAllCategories - 查询所有因子分类")
    void testFindAllCategories() {
        List<String> categories = factorInfoMapper.findAllCategories();
        assertNotNull(categories);
        assertFalse(categories.isEmpty());
        // 预期 '分类X', '分类Y', '分类Z', '新分类' (来自 insert 测试)
        assertTrue(categories.contains("分类X"));
        assertTrue(categories.contains("分类Y"));
        assertTrue(categories.contains("分类Z"));
        assertEquals(3, categories.size()); // setUp中只有三种分类
    }
}