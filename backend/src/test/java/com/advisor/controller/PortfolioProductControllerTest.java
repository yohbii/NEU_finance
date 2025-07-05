package com.advisor.controller;

import com.advisor.common.PageResult;
import com.advisor.common.Result;
import com.advisor.entity.PortfolioProduct;
import com.advisor.service.PortfolioProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class PortfolioProductControllerTest {

    @Mock
    private PortfolioProductService portfolioProductService;

    @InjectMocks
    private PortfolioProductController portfolioProductController;

    private PortfolioProduct mockProduct;
    private PageResult<PortfolioProduct> mockPageResult;

    @BeforeEach
    void setUp() {
        mockProduct = new PortfolioProduct();
        mockProduct.setId(1L);
        mockProduct.setProductCode("PROD001");
        mockProduct.setProductName("稳健增长组合");
        mockProduct.setProductType("混合型");
        mockProduct.setStrategyId(101L);
        mockProduct.setRiskLevel(3);
        mockProduct.setMinInvestment(BigDecimal.valueOf(10000));
        mockProduct.setManagementFee(BigDecimal.valueOf(0.015));
        mockProduct.setDescription("一个用于测试的组合产品");
        mockProduct.setStatus(1);
        mockProduct.setCreatedAt(LocalDateTime.now());
        mockProduct.setUpdatedAt(LocalDateTime.now());

        mockPageResult = PageResult.of(Collections.singletonList(mockProduct), 1L, 1, 20);
    }

    @Test
    void getProductList_success() {
        String productType = "混合型";
        Integer riskLevel = 3;
        String keyword = "增长";
        Integer current = 1;
        Integer size = 20;

        when(portfolioProductService.getProductList(productType, riskLevel, keyword, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<PortfolioProduct>> result = portfolioProductController.getProductList(
                productType, riskLevel, keyword, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(portfolioProductService, times(1)).getProductList(
                productType, riskLevel, keyword, current, size);
    }

    @Test
    void getProductList_failure() {
        String errorMessage = "Product list query failed";
        when(portfolioProductService.getProductList(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<PortfolioProduct>> result = portfolioProductController.getProductList(
                null, null, null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("查询产品列表失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).getProductList(any(), any(), any(), any(), any());
    }

    @Test
    void getProductById_success() {
        Long id = 1L;
        when(portfolioProductService.getProductById(id)).thenReturn(mockProduct);

        Result<PortfolioProduct> result = portfolioProductController.getProductById(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockProduct, result.getData());
        verify(portfolioProductService, times(1)).getProductById(id);
    }

    @Test
    void getProductById_failure() {
        Long id = 99L;
        String errorMessage = "Product not found";
        when(portfolioProductService.getProductById(id)).thenThrow(new RuntimeException(errorMessage));

        Result<PortfolioProduct> result = portfolioProductController.getProductById(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("获取产品详情失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).getProductById(id);
    }

    @Test
    void getProductByCode_success() {
        String productCode = "PROD001";
        when(portfolioProductService.getProductByCode(productCode)).thenReturn(mockProduct);

        Result<PortfolioProduct> result = portfolioProductController.getProductByCode(productCode);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockProduct, result.getData());
        verify(portfolioProductService, times(1)).getProductByCode(productCode);
    }

    @Test
    void getProductByCode_failure() {
        String productCode = "NON_EXISTENT";
        String errorMessage = "Product not found by code";
        when(portfolioProductService.getProductByCode(productCode)).thenThrow(new RuntimeException(errorMessage));

        Result<PortfolioProduct> result = portfolioProductController.getProductByCode(productCode);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("获取产品详情失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).getProductByCode(productCode);
    }

    @Test
    void createProduct_success() {
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("NEW_PROD");
        newProduct.setProductName("新产品");
        Long createdId = 2L;

        when(portfolioProductService.createProduct(any(PortfolioProduct.class))).thenReturn(createdId);

        Result<Long> result = portfolioProductController.createProduct(newProduct);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(createdId, result.getData());
        verify(portfolioProductService, times(1)).createProduct(newProduct);
    }

    @Test
    void createProduct_failure() {
        PortfolioProduct newProduct = new PortfolioProduct();
        newProduct.setProductCode("EXISTING_PROD");
        String errorMessage = "Product code already exists";
        when(portfolioProductService.createProduct(any(PortfolioProduct.class))).thenThrow(new RuntimeException(errorMessage));

        Result<Long> result = portfolioProductController.createProduct(newProduct);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("创建产品失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).createProduct(newProduct);
    }

    @Test
    void updateProduct_success() {
        Long id = 1L;
        PortfolioProduct updatedProduct = new PortfolioProduct();
        updatedProduct.setId(id);
        updatedProduct.setProductName("更新后的产品名");

        doNothing().when(portfolioProductService).updateProduct(any(PortfolioProduct.class));

        Result<Void> result = portfolioProductController.updateProduct(id, updatedProduct);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).updateProduct(updatedProduct);
        assertEquals(id, updatedProduct.getId());
    }

    @Test
    void updateProduct_failure() {
        Long id = 1L;
        PortfolioProduct updatedProduct = new PortfolioProduct();
        updatedProduct.setId(id);
        String errorMessage = "Update validation failed";
        doThrow(new RuntimeException(errorMessage)).when(portfolioProductService).updateProduct(any(PortfolioProduct.class));

        Result<Void> result = portfolioProductController.updateProduct(id, updatedProduct);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("更新产品失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).updateProduct(updatedProduct);
    }

    @Test
    void deleteProduct_success() {
        Long id = 1L;
        doNothing().when(portfolioProductService).deleteProduct(id);

        Result<Void> result = portfolioProductController.deleteProduct(id);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).deleteProduct(id);
    }

    @Test
    void deleteProduct_failure() {
        Long id = 99L;
        String errorMessage = "Product not found for deletion";
        doThrow(new RuntimeException(errorMessage)).when(portfolioProductService).deleteProduct(id);

        Result<Void> result = portfolioProductController.deleteProduct(id);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("删除产品失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).deleteProduct(id);
    }

    @Test
    void getAllProductTypes_success() {
        List<String> types = Arrays.asList("股票型", "债券型", "混合型");
        when(portfolioProductService.getAllProductTypes()).thenReturn(types);

        Result<List<String>> result = portfolioProductController.getAllProductTypes();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(types, result.getData());
        verify(portfolioProductService, times(1)).getAllProductTypes();
    }

    @Test
    void getAllProductTypes_failure() {
        String errorMessage = "Error fetching product types";
        when(portfolioProductService.getAllProductTypes()).thenThrow(new RuntimeException(errorMessage));

        Result<List<String>> result = portfolioProductController.getAllProductTypes();

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("获取产品类型失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).getAllProductTypes();
    }

    @Test
    void getApprovalList_success() {
        String status = "pending";
        Integer current = 1;
        Integer size = 20;

        when(portfolioProductService.getProductList(null, null, null, current, size))
                .thenReturn(mockPageResult);

        Result<PageResult<PortfolioProduct>> result = portfolioProductController.getApprovalList(status, current, size);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(mockPageResult, result.getData());
        verify(portfolioProductService, times(1)).getProductList(null, null, null, current, size);
    }

    @Test
    void getApprovalList_failure() {
        String errorMessage = "Approval list query failed";
        when(portfolioProductService.getProductList(any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException(errorMessage));

        Result<PageResult<PortfolioProduct>> result = portfolioProductController.getApprovalList(null, 1, 20);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertEquals("查询审核列表失败：" + errorMessage, result.getMessage());
        assertNull(result.getData());
        verify(portfolioProductService, times(1)).getProductList(any(), any(), any(), any(), any());
    }

    @Test
    void submitApproval_success() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("productId", "1");
        requestData.put("action", "approve");
        requestData.put("comment", "Looks good");

        Result<Void> result = portfolioProductController.submitApproval(requestData);

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void submitApproval_failure_invalidProductId() {
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("productId", "invalid_id");
        requestData.put("action", "approve");

        Result<Void> result = portfolioProductController.submitApproval(requestData);

        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("提交审核失败："));
        assertNull(result.getData());
    }

    @Test
    void getApprovalStats_success() {
        Map<String, Object> expectedStats = new HashMap<>();
        expectedStats.put("pending", 12);
        expectedStats.put("approved", 45);
        expectedStats.put("rejected", 8);
        expectedStats.put("total", 65);

        Result<Map<String, Object>> result = portfolioProductController.getApprovalStats();

        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertEquals("操作成功", result.getMessage());
        assertEquals(expectedStats, result.getData());
    }
}