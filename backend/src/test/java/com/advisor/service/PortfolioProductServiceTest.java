package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.PortfolioProduct;
import com.advisor.mapper.PortfolioProductMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioProductServiceTest {

    @Mock
    private PortfolioProductMapper portfolioProductMapper;

    @InjectMocks
    private PortfolioProductService portfolioProductService;

    @Test
    @DisplayName("getProductList - 成功分页查询产品列表")
    void getProductList_Success() {
        // GIVEN
        String productType = "股票组合";
        Integer riskLevel = 3;
        String keyword = "稳健";
        Integer current = 2;
        Integer size = 10;
        Integer offset = (current - 1) * size; // (2-1)*10 = 10

        List<PortfolioProduct> mockRecords = List.of(new PortfolioProduct(), new PortfolioProduct());
        Long mockTotal = 25L;

        when(portfolioProductMapper.findList(productType, riskLevel, keyword, offset, size))
                .thenReturn(mockRecords);
        when(portfolioProductMapper.countList(productType, riskLevel, keyword))
                .thenReturn(mockTotal);

        // WHEN
        PageResult<PortfolioProduct> result = portfolioProductService.getProductList(
                productType, riskLevel, keyword, current, size);

        // THEN
        assertNotNull(result);
        assertEquals(mockRecords, result.getRecords());
        assertEquals(mockTotal, result.getTotal());
        assertEquals(current, result.getCurrent());
        assertEquals(size, result.getSize());

        verify(portfolioProductMapper, times(1))
                .findList(productType, riskLevel, keyword, offset, size);
        verify(portfolioProductMapper, times(1))
                .countList(productType, riskLevel, keyword);
    }

    @Test
    @DisplayName("getProductList - 分页参数为null或无效时使用默认值")
    void getProductList_NullOrInvalidPagination_UsesDefaults() {
        // GIVEN
        String productType = null;
        Integer riskLevel = null;
        String keyword = null;
        Integer current = 0; // 无效值
        Integer size = -5;   // 无效值

        // 预期默认值为 current=1, size=20, offset=0
        when(portfolioProductMapper.findList(any(), any(), any(), eq(0), eq(20)))
                .thenReturn(Collections.emptyList());
        when(portfolioProductMapper.countList(any(), any(), any()))
                .thenReturn(0L);

        // WHEN
        PageResult<PortfolioProduct> result = portfolioProductService.getProductList(
                productType, riskLevel, keyword, current, size);

        // THEN
        assertNotNull(result);
        assertEquals(1, result.getCurrent());
        assertEquals(20, result.getSize());
        assertEquals(0L, result.getTotal());
        assertTrue(result.getRecords().isEmpty());

        verify(portfolioProductMapper, times(1))
                .findList(null, null, null, 0, 20);
        verify(portfolioProductMapper, times(1))
                .countList(null, null, null);
    }

    @Test
    @DisplayName("getProductById - 成功获取产品详情")
    void getProductById_Success() {
        // GIVEN
        Long productId = 1L;
        PortfolioProduct mockProduct = new PortfolioProduct();
        mockProduct.setId(productId);
        mockProduct.setProductName("测试产品A");

        when(portfolioProductMapper.findById(productId)).thenReturn(mockProduct);

        // WHEN
        PortfolioProduct result = portfolioProductService.getProductById(productId);

        // THEN
        assertNotNull(result);
        assertEquals(productId, result.getId());
        assertEquals("测试产品A", result.getProductName());

        verify(portfolioProductMapper, times(1)).findById(productId);
    }

    @Test
    @DisplayName("getProductById - ID为null时抛出异常")
    void getProductById_NullId_ThrowsException() {
        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.getProductById(null));
        assertEquals("产品ID不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper); // 确保没有调用mapper
    }

    @Test
    @DisplayName("getProductById - 产品不存在时抛出异常")
    void getProductById_NotFound_ThrowsException() {
        // GIVEN
        when(portfolioProductMapper.findById(anyLong())).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.getProductById(99L));
        assertEquals("产品不存在", exception.getMessage());

        verify(portfolioProductMapper, times(1)).findById(99L);
    }

    @Test
    @DisplayName("getProductByCode - 成功获取产品详情")
    void getProductByCode_Success() {
        // GIVEN
        String productCode = "P001";
        PortfolioProduct mockProduct = new PortfolioProduct();
        mockProduct.setProductCode(productCode);
        mockProduct.setProductName("测试产品B");

        when(portfolioProductMapper.findByProductCode(productCode)).thenReturn(mockProduct);

        // WHEN
        PortfolioProduct result = portfolioProductService.getProductByCode(productCode);

        // THEN
        assertNotNull(result);
        assertEquals(productCode, result.getProductCode());
        assertEquals("测试产品B", result.getProductName());

        verify(portfolioProductMapper, times(1)).findByProductCode(productCode);
    }

    @Test
    @DisplayName("getProductByCode - 产品代码为空时抛出异常")
    void getProductByCode_EmptyCode_ThrowsException() {
        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.getProductByCode(""));
        assertEquals("产品代码不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("getProductByCode - 产品不存在时抛出异常")
    void getProductByCode_NotFound_ThrowsException() {
        // GIVEN
        when(portfolioProductMapper.findByProductCode(anyString())).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.getProductByCode("NON_EXIST_CODE"));
        assertEquals("产品不存在", exception.getMessage());

        verify(portfolioProductMapper, times(1)).findByProductCode("NON_EXIST_CODE");
    }

    @Test
    @DisplayName("createProduct - 成功创建产品")
    void createProduct_Success() {
        // GIVEN
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("NEW_P001");
        newProduct.setProductName("新产品");

        when(portfolioProductMapper.findByProductCode("NEW_P001")).thenReturn(null); // 模拟产品代码不存在
        // 模拟插入成功，并为新产品设置ID（通常由数据库生成）
        doAnswer(invocation -> {
            PortfolioProduct argProduct = invocation.getArgument(0);
            argProduct.setId(100L); // 设置一个模拟ID
            return 1; // 模拟插入成功，返回影响行数1
        }).when(portfolioProductMapper).insert(any(PortfolioProduct.class));

        // WHEN
        Long createdId = portfolioProductService.createProduct(newProduct);

        // THEN
        assertNotNull(createdId);
        assertEquals(100L, createdId);
        assertEquals(1, newProduct.getStatus()); // 验证状态是否被设置为1
        verify(portfolioProductMapper, times(1)).findByProductCode("NEW_P001");
        verify(portfolioProductMapper, times(1)).insert(newProduct);
    }

    @Test
    @DisplayName("createProduct - 产品信息为null时抛出异常")
    void createProduct_NullProduct_ThrowsException() {
        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.createProduct(null));
        assertEquals("产品信息不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("createProduct - 产品代码为空时抛出异常")
    void createProduct_EmptyCode_ThrowsException() {
        // GIVEN
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductName("产品A"); // 代码为空

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.createProduct(newProduct));
        assertEquals("产品代码不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("createProduct - 产品名称为空时抛出异常")
    void createProduct_EmptyName_ThrowsException() {
        // GIVEN
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("P_CODE");
        newProduct.setProductName(""); // 名称为空

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.createProduct(newProduct));
        assertEquals("产品名称不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("createProduct - 产品代码已存在时抛出异常")
    void createProduct_CodeAlreadyExists_ThrowsException() {
        // GIVEN
        PortfolioProduct existingProduct = new PortfolioProduct();
        existingProduct.setProductCode("EXIST_P001");

        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("EXIST_P001");
        newProduct.setProductName("重复产品");

        when(portfolioProductMapper.findByProductCode("EXIST_P001")).thenReturn(existingProduct); // 模拟产品代码已存在

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.createProduct(newProduct));
        assertEquals("产品代码已存在", exception.getMessage());

        verify(portfolioProductMapper, times(1)).findByProductCode("EXIST_P001");
        verify(portfolioProductMapper, never()).insert(any(PortfolioProduct.class)); // 确保没有尝试插入
    }

    @Test
    @DisplayName("createProduct - 插入数据库失败时抛出异常")
    void createProduct_InsertFails_ThrowsException() {
        // GIVEN
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("FAIL_P001");
        newProduct.setProductName("失败产品");

        when(portfolioProductMapper.findByProductCode("FAIL_P001")).thenReturn(null);
        when(portfolioProductMapper.insert(any(PortfolioProduct.class))).thenReturn(0); // 模拟插入失败，返回0行

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.createProduct(newProduct));
        assertEquals("创建产品失败", exception.getMessage());

        verify(portfolioProductMapper, times(1)).insert(newProduct);
    }

    @Test
    @DisplayName("updateProduct - 成功更新产品")
    void updateProduct_Success() {
        // GIVEN
        Long productId = 1L;
        PortfolioProduct existingProduct = new PortfolioProduct();
        existingProduct.setId(productId);
        existingProduct.setProductName("旧名称");

        PortfolioProduct updatedProduct = new PortfolioProduct();
        updatedProduct.setId(productId);
        updatedProduct.setProductName("新名称");

        when(portfolioProductMapper.findById(productId)).thenReturn(existingProduct);
        when(portfolioProductMapper.update(updatedProduct)).thenReturn(1);

        // WHEN & THEN
        assertDoesNotThrow(() -> portfolioProductService.updateProduct(updatedProduct));

        verify(portfolioProductMapper, times(1)).findById(productId);
        verify(portfolioProductMapper, times(1)).update(updatedProduct);
    }

    @Test
    @DisplayName("updateProduct - 产品信息或ID为null时抛出异常")
    void updateProduct_NullProductOrId_ThrowsException() {
        // WHEN & THEN
        RuntimeException exception1 = assertThrows(RuntimeException.class,
                () -> portfolioProductService.updateProduct(null));
        assertEquals("产品ID不能为空", exception1.getMessage());

        PortfolioProduct productWithNullId = new PortfolioProduct();
        RuntimeException exception2 = assertThrows(RuntimeException.class,
                () -> portfolioProductService.updateProduct(productWithNullId));
        assertEquals("产品ID不能为空", exception2.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("updateProduct - 更新的产品不存在时抛出异常")
    void updateProduct_NotFound_ThrowsException() {
        // GIVEN
        PortfolioProduct updatedProduct = new PortfolioProduct();
        updatedProduct.setId(99L);

        when(portfolioProductMapper.findById(anyLong())).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.updateProduct(updatedProduct));
        assertEquals("产品不存在", exception.getMessage());

        verify(portfolioProductMapper, times(1)).findById(99L);
        verify(portfolioProductMapper, never()).update(any(PortfolioProduct.class));
    }

    @Test
    @DisplayName("updateProduct - 更新数据库失败时抛出异常")
    void updateProduct_UpdateFails_ThrowsException() {
        // GIVEN
        Long productId = 1L;
        PortfolioProduct existingProduct = new PortfolioProduct();
        existingProduct.setId(productId);

        PortfolioProduct updatedProduct = new PortfolioProduct();
        updatedProduct.setId(productId);

        when(portfolioProductMapper.findById(productId)).thenReturn(existingProduct);
        when(portfolioProductMapper.update(updatedProduct)).thenReturn(0); // 模拟更新失败

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.updateProduct(updatedProduct));
        assertEquals("更新产品失败", exception.getMessage());

        verify(portfolioProductMapper, times(1)).update(updatedProduct);
    }

    @Test
    @DisplayName("deleteProduct - 成功删除产品")
    void deleteProduct_Success() {
        // GIVEN
        Long productId = 1L;
        when(portfolioProductMapper.findById(productId)).thenReturn(new PortfolioProduct());
        when(portfolioProductMapper.deleteById(productId)).thenReturn(1);

        // WHEN & THEN
        assertDoesNotThrow(() -> portfolioProductService.deleteProduct(productId));

        verify(portfolioProductMapper, times(1)).findById(productId);
        verify(portfolioProductMapper, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("deleteProduct - ID为null时抛出异常")
    void deleteProduct_NullId_ThrowsException() {
        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.deleteProduct(null));
        assertEquals("产品ID不能为空", exception.getMessage());

        verifyNoInteractions(portfolioProductMapper);
    }

    @Test
    @DisplayName("deleteProduct - 删除的产品不存在时抛出异常")
    void deleteProduct_NotFound_ThrowsException() {
        // GIVEN
        when(portfolioProductMapper.findById(anyLong())).thenReturn(null);

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.deleteProduct(99L));
        assertEquals("产品不存在", exception.getMessage());

        verify(portfolioProductMapper, times(1)).findById(99L);
        verify(portfolioProductMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("deleteProduct - 删除数据库失败时抛出异常")
    void deleteProduct_DeleteFails_ThrowsException() {
        // GIVEN
        Long productId = 1L;
        when(portfolioProductMapper.findById(productId)).thenReturn(new PortfolioProduct());
        when(portfolioProductMapper.deleteById(productId)).thenReturn(0); // 模拟删除失败

        // WHEN & THEN
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> portfolioProductService.deleteProduct(productId));
        assertEquals("删除产品失败", exception.getMessage());

        verify(portfolioProductMapper, times(1)).deleteById(productId);
    }

    @Test
    @DisplayName("getAllProductTypes - 成功获取所有产品类型")
    void getAllProductTypes_Success() {
        // GIVEN
        List<String> mockTypes = List.of("股票组合", "债券组合", "混合组合");
        when(portfolioProductMapper.findAllProductTypes()).thenReturn(mockTypes);

        // WHEN
        List<String> result = portfolioProductService.getAllProductTypes();

        // THEN
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
        assertEquals(mockTypes, result);

        verify(portfolioProductMapper, times(1)).findAllProductTypes();
    }

    @Test
    @DisplayName("getAllProductTypes - 没有产品类型时返回空列表")
    void getAllProductTypes_EmptyList() {
        // GIVEN
        when(portfolioProductMapper.findAllProductTypes()).thenReturn(Collections.emptyList());

        // WHEN
        List<String> result = portfolioProductService.getAllProductTypes();

        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(portfolioProductMapper, times(1)).findAllProductTypes();
    }
}