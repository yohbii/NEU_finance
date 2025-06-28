package org.project.backend.service;

import org.project.backend.DTO.HttpResponse;
import org.project.backend.DTO.PortfolioFundParam;
import org.project.backend.entity.Portfolio;
import org.project.backend.mapper.PortfolioMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

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
     *
     * @param portfolioName 组合名称
     * @param createdBy     创建人
     * @param fundList      基金及权重列表，每个元素包含fundCode和weight
     * @param note          组合备注信息，可选
     * @return 新组合ID的HttpResponse，失败时返回-1
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

        int succCount = 0;
        for (PortfolioFundParam item : fundList) {
            int result = portfolioMapper.addFundToPortfolio(portfolioId, item.getFundCode(), item.getWeight());
            succCount += result;
        }
        if (succCount != fundList.size()) {
            return new HttpResponse<>(-1, portfolioId, "部分基金加入组合失败", "部分基金加入组合失败");
        }
        return new HttpResponse<>(0, portfolioId, "基金组合添加成功", null);
    }
}
