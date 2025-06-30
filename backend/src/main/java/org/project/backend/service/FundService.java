package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.DTO.PortfolioFundParam;
import org.project.backend.entity.Fund;
import org.project.backend.mapper.FundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.project.backend.mapper.PortfolioMapper;
import org.project.backend.entity.Portfolio;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 基金相关服务，包括基金信息查询、组合创建等操作
 */
@Service
public class FundService {

    /**
     * 基金信息Mapper
     */
    @Autowired
    private FundMapper fundMapper;


    /**
     * 按基金代码和标签Id列表查询基金
     *
     * @param code   基金代码，不可为空
     * @param tagIds 标签ID列表，可为空
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFunds(String code, List<Integer> tagIds) {
        List<Fund> funds = fundMapper.selectByCodeOrTags(code, tagIds);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    /**
     * 按基金代码列表查询基金
     *
     * @param code 基金代码，可为空
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFunds(String code) {
        List<Fund> funds = fundMapper.selectByCodeOrTags(code, null);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    /**
     * 按基金公司 名称和标签Id列表查询基金
     *
     * @param companyName 公司名称
     * @param tagIds      公司标签ID列表
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFundsByCompany(String companyName, List<Integer> tagIds) {
        List<Fund> funds = fundMapper.selectByCompanyAndTags(companyName, tagIds);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    /**
     * 仅按基金公司名称查询基金
     *
     * @param companyName 公司名称
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFundsByCompany(String companyName) {
        List<Fund> funds = fundMapper.selectByCompanyAndTags(companyName, null);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    /**
     * 按基金经理 姓名和标签Id列表查询基金
     *
     * @param managerName 经理姓名
     * @param tagIds      经理标签ID列表
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFundsByManager(String managerName, List<Integer> tagIds) {
        List<Fund> funds = fundMapper.selectByManagerAndTags(managerName, tagIds);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }

    /**
     * 仅按基金经理名称查询基金
     *
     * @param managerName 经理姓名
     * @return 基金列表的HttpResponse
     */
    public HttpResponse<List<Fund>> queryFundsByManager(String managerName) {
        List<Fund> funds = fundMapper.selectByManagerAndTags(managerName, null);
        if (funds == null || funds.isEmpty()) {
            return new HttpResponse<>(-1, null, "没有查到相关基金", "没有查到相关基金");
        }
        return new HttpResponse<>(0, funds, "ok", null);
    }
}