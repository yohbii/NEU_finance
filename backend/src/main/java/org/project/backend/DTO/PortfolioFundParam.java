package org.project.backend.DTO; // 或其它你喜欢的包

public class PortfolioFundParam {
    private String fundCode;
    private Double weight;

    public PortfolioFundParam() {}

    public PortfolioFundParam(String fundCode, Double weight) {
        this.fundCode = fundCode;
        this.weight = weight;
    }

    public String getFundCode() {
        return fundCode;
    }
    public void setFundCode(String fundCode) {
        this.fundCode = fundCode;
    }
    public Double getWeight() {
        return weight;
    }
    public void setWeight(Double weight) {
        this.weight = weight;
    }
}