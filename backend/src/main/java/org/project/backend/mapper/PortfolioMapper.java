package org.project.backend.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.project.backend.entity.Portfolio;

@Mapper
public interface PortfolioMapper {
    // 生成组合（自增id回填）
    @Insert("INSERT INTO portfolio (portfolioName, createdBy, note) VALUES (#{portfolioName}, #{createdBy}, #{note})")
    @Options(useGeneratedKeys = true, keyProperty = "portfolioId", keyColumn = "portfolioId")
    int insertPortfolio(Portfolio portfolio);

    // 添加基金到组合
    @Insert("INSERT INTO portfolioFund (portfolioId, fundCode, weight) VALUES (#{portfolioId}, #{fundCode}, #{weight})")
    int addFundToPortfolio(@Param("portfolioId") Integer portfolioId,
                           @Param("fundCode") String fundCode,
                           @Param("weight") Double weight);
}