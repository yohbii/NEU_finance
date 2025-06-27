package org.project.backend.service;
import org.project.backend.entity.Fund;
import org.project.backend.mapper.FundMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Collections;

@Service
public class FundService {
    @Autowired
    private FundMapper fundMapper;

    // 1.1 基金代码、标签查询
    public List<Fund> queryFunds(String code, List<Integer> tagIds) {
        return fundMapper.selectByCodeOrTags(code, tagIds);
    }

    // 1.2 只用基金代码查询
    public List<Fund> queryFunds(String code) {
        // 只用code查询，tagIds不给
        return fundMapper.selectByCodeOrTags(code, Collections.emptyList());
    }

    // 2.1 公司名称+标签
    public List<Fund> queryFundsByCompany(String companyName, List<Integer> tagIds) {
        return fundMapper.selectByCompanyAndTags(companyName, tagIds);
    }

    // 2.2 只用公司名称
    public List<Fund> queryFundsByCompany(String companyName) {
        return fundMapper.selectByCompanyAndTags(companyName, Collections.emptyList());
    }

    // 3.1 经理名称+标签
    public List<Fund> queryFundsByManager(String managerName, List<Integer> tagIds) {
        return fundMapper.selectByManagerAndTags(managerName, tagIds);
    }

    // 3.2 只用经理名称
    public List<Fund> queryFundsByManager(String managerName) {
        return fundMapper.selectByManagerAndTags(managerName, Collections.emptyList());
    }
}