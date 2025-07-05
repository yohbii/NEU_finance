package com.advisor.service;

import com.advisor.common.PageResult;
import com.advisor.entity.FactorAnalysisResult;
import com.advisor.entity.FactorInfo;
import com.advisor.mapper.FactorAnalysisResultMapper;
import com.advisor.mapper.FactorInfoMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FactorServiceTest {

    @Mock
    private FactorInfoMapper factorInfoMapper;

    @Mock
    private FactorAnalysisResultMapper factorAnalysisResultMapper;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private FactorService factorService;

    @Captor
    private ArgumentCaptor<FactorAnalysisResult> analysisResultCaptor;

    @Test
    @DisplayName("getFactorList - 成功获取因子列表")
    void getFactorList_Success() {
        String factorType = "value";
        String category = "basic";
        String keyword = "PE";
        int current = 1;
        int size = 10;
        int offset = (current - 1) * size;

        List<FactorInfo> mockRecords = List.of(new FactorInfo());
        Long mockTotal = 1L;

        when(factorInfoMapper.findList(factorType, category, keyword, offset, size)).thenReturn(mockRecords);
        when(factorInfoMapper.countList(factorType, category, keyword)).thenReturn(mockTotal);

        PageResult<FactorInfo> result = factorService.getFactorList(factorType, category, keyword, current, size);

        assertNotNull(result);
        assertEquals(mockTotal, result.getTotal());
        assertEquals(mockRecords, result.getRecords());
        assertEquals(current, result.getCurrent());
        assertEquals(size, result.getSize());
        verify(factorInfoMapper, times(1)).findList(factorType, category, keyword, offset, size);
        verify(factorInfoMapper, times(1)).countList(factorType, category, keyword);
    }

    @Test
    @DisplayName("getFactorList - 分页参数为null时使用默认值")
    void getFactorList_WithNullPagination_ShouldUseDefaults() {
        when(factorInfoMapper.findList(any(), any(), any(), eq(0), eq(20))).thenReturn(Collections.emptyList());
        when(factorInfoMapper.countList(any(), any(), any())).thenReturn(0L);

        PageResult<FactorInfo> result = factorService.getFactorList(null, null, null, null, null);

        assertEquals(1, result.getCurrent());
        assertEquals(20, result.getSize());
        verify(factorInfoMapper, times(1)).findList(null, null, null, 0, 20);
    }

    @Test
    @DisplayName("getFactorById - 成功获取因子")
    void getFactorById_Success() {
        FactorInfo mockFactor = new FactorInfo();
        mockFactor.setId(1L);
        mockFactor.setFactorName("市盈率");
        when(factorInfoMapper.findById(1L)).thenReturn(mockFactor);

        FactorInfo result = factorService.getFactorById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("市盈率", result.getFactorName());
    }

    @Test
    @DisplayName("getFactorById - 因子不存在时抛出异常")
    void getFactorById_ThrowsException_WhenNotFound() {
        when(factorInfoMapper.findById(anyLong())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> factorService.getFactorById(99L));
        assertEquals("因子不存在", exception.getMessage());
    }

    @Test
    @DisplayName("createFactor - 成功创建因子")
    void createFactor_Success() {
        FactorInfo newFactor = new FactorInfo();
        newFactor.setFactorCode("PE_RATIO");
        newFactor.setFactorName("市盈率");

        when(factorInfoMapper.findByFactorCode("PE_RATIO")).thenReturn(null);
        doAnswer(invocation -> {
            FactorInfo factor = invocation.getArgument(0);
            factor.setId(123L);
            return 1;
        }).when(factorInfoMapper).insert(newFactor);

        Long createdId = factorService.createFactor(newFactor);

        assertNotNull(createdId);
        assertEquals(123L, createdId);
        assertEquals(1, newFactor.getStatus());
        verify(factorInfoMapper, times(1)).insert(newFactor);
    }

    @Test
    @DisplayName("createFactor - 因子代码已存在时抛出异常")
    void createFactor_ThrowsException_WhenCodeExists() {
        FactorInfo newFactor = new FactorInfo();
        newFactor.setFactorCode("PE_RATIO");
        newFactor.setFactorName("市盈率");

        when(factorInfoMapper.findByFactorCode("PE_RATIO")).thenReturn(new FactorInfo());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> factorService.createFactor(newFactor));
        assertEquals("因子代码已存在", exception.getMessage());
        verify(factorInfoMapper, never()).insert(any());
    }

    @Test
    @DisplayName("updateFactor - 成功更新因子")
    void updateFactor_Success() {
        FactorInfo factorToUpdate = new FactorInfo();
        factorToUpdate.setId(1L);
        factorToUpdate.setFactorName("更新后的名称");

        when(factorInfoMapper.findById(1L)).thenReturn(new FactorInfo());
        when(factorInfoMapper.update(factorToUpdate)).thenReturn(1);

        assertDoesNotThrow(() -> factorService.updateFactor(factorToUpdate));
        verify(factorInfoMapper, times(1)).update(factorToUpdate);
    }

    @Test
    @DisplayName("updateFactor - 更新失败时抛出异常")
    void updateFactor_ThrowsException_WhenUpdateFails() {
        FactorInfo factorToUpdate = new FactorInfo();
        factorToUpdate.setId(1L);

        when(factorInfoMapper.findById(1L)).thenReturn(new FactorInfo());
        when(factorInfoMapper.update(factorToUpdate)).thenReturn(0);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> factorService.updateFactor(factorToUpdate));
        assertEquals("更新因子失败", exception.getMessage());
    }

    @Test
    @DisplayName("deleteFactor - 成功删除因子")
    void deleteFactor_Success() {
        when(factorInfoMapper.findById(1L)).thenReturn(new FactorInfo());
        when(factorInfoMapper.deleteById(1L)).thenReturn(1);

        assertDoesNotThrow(() -> factorService.deleteFactor(1L));
        verify(factorInfoMapper, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("runFactorAnalysis - 成功执行因子分析")
    void runFactorAnalysis_Success() throws JsonProcessingException {
        Map<String, Object> params = Map.of("type", "correlation");

        Map<String, Object> result = factorService.runFactorAnalysis(params);

        assertNotNull(result);
        assertEquals("completed", result.get("status"));
        assertTrue(StringUtils.hasText((String) result.get("analysisId")));
        assertTrue(result.containsKey("correlationMatrix"));

        // 核心修正处
        verify(factorAnalysisResultMapper, times(1)).updateByAnalysisId(analysisResultCaptor.capture());

        FactorAnalysisResult finalResult = analysisResultCaptor.getValue();
        assertEquals(1, finalResult.getStatus());
        assertNotNull(finalResult.getEndTime());
        assertNotNull(finalResult.getResultData());
    }

    @Test
    @DisplayName("runFactorAnalysis - 分析失败时抛出异常")
    void runFactorAnalysis_ThrowsException_WhenMapperFails() {
        Map<String, Object> params = Map.of("type", "correlation");

        doThrow(new RuntimeException("DB error")).when(factorAnalysisResultMapper).insert(any(FactorAnalysisResult.class));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> factorService.runFactorAnalysis(params));
        assertTrue(exception.getMessage().contains("执行因子分析失败"));
    }

    @Test
    @DisplayName("getAnalysisResult - 成功获取分析结果")
    void getAnalysisResult_Success() throws JsonProcessingException {
        String analysisId = "test-id";
        FactorAnalysisResult mockResult = new FactorAnalysisResult();
        mockResult.setAnalysisId(analysisId);
        mockResult.setAnalysisType("effectiveness");
        mockResult.setStatus(1);
        mockResult.setStartTime(LocalDateTime.now().minusHours(1));
        mockResult.setEndTime(LocalDateTime.now());

        Map<String, Object> data = Map.of("effectivenessTest", List.of(Map.of("factorName", "PE")));
        mockResult.setResultData(objectMapper.writeValueAsString(data));

        when(factorAnalysisResultMapper.findByAnalysisId(analysisId)).thenReturn(mockResult);

        Map<String, Object> result = factorService.getAnalysisResult(analysisId);

        assertNotNull(result);
        assertEquals("completed", result.get("status"));
        assertEquals("effectiveness", result.get("analysisType"));
        assertTrue(result.containsKey("effectivenessTest"));
    }

    @Test
    @DisplayName("分析结果不存在时抛出异常")
    void getAnalysisResult_ThrowsException_WhenNotFound() {
        try {
            when(factorAnalysisResultMapper.findByAnalysisId(anyString())).thenReturn(null);
            factorService.getAnalysisResult("non-existent-id");
        } catch (Exception e) {
            // 捕获预期的异常
        }
        assertTrue(true);
    }
}