package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.DTO.PortfolioFundParam;
import org.project.backend.entity.Portfolio;
import org.project.backend.mapper.PortfolioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class PortfolioService {
    /**
     * 基金组合与持仓权重Mapper
     */
    @Autowired
    private PortfolioMapper portfolioMapper;

    /**
     * 创建一个新的基金组合，并批量向组合中添加基金及对应权重
     */
    @Transactional
    public HttpResponse<Integer> addFundPortfolio(String portfolioName, String createdBy, List<PortfolioFundParam> fundList, String note) {
        Portfolio pf = new Portfolio();
        pf.setPortfolioName(portfolioName);
        pf.setCreatedBy(createdBy);
        pf.setNote(note);
        int insertCount = portfolioMapper.insertPortfolio(pf);
        if (insertCount <= 0 || pf.getPortfolioId() == null) {
            return new HttpResponse<>(-1, null, "组合新增失败", "组合新增失败");
        }
        Integer portfolioId = pf.getPortfolioId();
        for (PortfolioFundParam item : fundList) {
            int result = portfolioMapper.addFundToPortfolio(portfolioId, item.getFundCode(), item.getWeight());
        }
        return new HttpResponse<>(0, portfolioId, "ok", null);
    }

    /**
     * 查询所有基金组合
     */
    public HttpResponse<List<Portfolio>> getAllPortfolios() {
        List<Portfolio> portfolios = portfolioMapper.getAllPortfolios();
        if (portfolios == null || portfolios.isEmpty()) {
            return new HttpResponse<>(-1, null, "无组合数据", "无组合数据");
        }
        return new HttpResponse<>(0, portfolios, "ok", null);
    }

    /**
     * 根据组合ID查询单个基金组合（含明细）
     */
    public HttpResponse<Portfolio> getPortfolioById(Integer portfolioId) {
        Portfolio portfolio = portfolioMapper.getPortfolioById(portfolioId);
        if (portfolio == null) {
            return new HttpResponse<>(-1, null, "未找到该组合", "未找到该组合");
        }
        return new HttpResponse<>(0, portfolio, "ok", null);
    }

    /**
     * 查询某个用户创建的所有基金组合
     */
    public HttpResponse<List<Portfolio>> getPortfoliosByCreator(String createdBy) {
        List<Portfolio> portfolios = portfolioMapper.getPortfoliosByCreator(createdBy);
        if (portfolios == null || portfolios.isEmpty()) {
            return new HttpResponse<>(-1, null, "该用户暂无组合", "该用户暂无组合");
        }
        return new HttpResponse<>(0, portfolios, "ok", null);
    }
}