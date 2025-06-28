package org.project.backend.mapper;

import org.apache.ibatis.annotations.*;
import org.project.backend.entity.Portfolio;
import org.project.backend.DTO.PortfolioFundParam;

import java.util.List;

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

    // 查询所有组合（不带持仓，简单版）
    @Select("SELECT portfolioId, portfolioName, createdBy, note FROM portfolio")
    List<Portfolio> getAllPortfolios();

    // 查询指定创建人下所有组合
    @Select("SELECT portfolioId, portfolioName, createdBy, note FROM portfolio WHERE createdBy = #{createdBy}")
    List<Portfolio> getPortfoliosByCreator(@Param("createdBy") String createdBy);

    // 查询单个组合及其持仓（推荐用ResultMap/两步查）
    @Select("SELECT portfolioId, portfolioName, createdBy, note FROM portfolio WHERE portfolioId = #{portfolioId}")
    @Results({
            @Result(property = "portfolioId", column = "portfolioId"),
            @Result(property = "portfolioName", column = "portfolioName"),
            @Result(property = "createdBy", column = "createdBy"),
            @Result(property = "note", column = "note"),
            @Result(property = "funds", column = "portfolioId",
                    many = @Many(select = "getPortfolioFundsByPortfolioId"))
    })
    Portfolio getPortfolioById(@Param("portfolioId") Integer portfolioId);

    // 查询组合持仓明细
    @Select("SELECT fundCode, weight FROM portfolioFund WHERE portfolioId = #{portfolioId}")
    List<PortfolioFundParam> getPortfolioFundsByPortfolioId(@Param("portfolioId") Integer portfolioId);

}