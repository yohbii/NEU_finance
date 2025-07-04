package com.advisor.service;

import com.advisor.entity.FundInfo;
import com.advisor.mapper.FactorInfoMapper;
import com.advisor.mapper.FundInfoMapper;
import com.advisor.mapper.FundNetValueMapper;
import com.advisor.mapper.FundPerformanceMapper;
import com.advisor.mapper.StrategyInfoMapper;
import com.cjcrafter.openai.chat.tool.FunctionParameters;
import com.cjcrafter.openai.chat.tool.FunctionProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class FunctionExecutorService {

    private final Map<String, Function<JsonNode, Object>> functionMap = new HashMap<>();
    private final List<ChatFunction> functions = new ArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private FundInfoMapper fundInfoMapper;

    @Autowired
    private FundPerformanceMapper fundPerformanceMapper;

    @Autowired
    private StrategyInfoMapper strategyInfoMapper;

    @Autowired
    private FactorInfoMapper factorInfoMapper;

    @Autowired
    private FundNetValueMapper fundNetValueMapper;

    public FunctionExecutorService() {
        initializeFunctions();
    }

    private void initializeFunctions() {
        // Define functions with their descriptions and parameter schemas
        // search_funds
        Map<String, FunctionProperty> searchFundsProps = new HashMap<>();
        searchFundsProps.put("fund_type", new FunctionProperty("string", "基金类型，例如：股票型、债券型、混合型、指数型", Collections.emptyList()));
        searchFundsProps.put("risk_level", new FunctionProperty("integer", "基金风险等级，1-5整数", Collections.emptyList()));
        searchFundsProps.put("fund_company", new FunctionProperty("string", "基金管理公司名称，例如：华夏基金", Collections.emptyList()));
        FunctionParameters searchFundsParams = new FunctionParameters("object", searchFundsProps, new HashSet<>());
        registerFunction("search_funds", "根据多个条件（基金类型、风险等级、基金公司）筛选基金", searchFundsParams, this::searchFunds);

        // get_fund_info
        Map<String, FunctionProperty> getFundInfoProps = new HashMap<>();
        getFundInfoProps.put("fund_code", new FunctionProperty("string", "基金代码，例如：000001", Collections.emptyList()));
        Set<String> getFundInfoRequired = new HashSet<>();
        getFundInfoRequired.add("fund_code");
        FunctionParameters getFundInfoParams = new FunctionParameters("object", getFundInfoProps, getFundInfoRequired);
        registerFunction("get_fund_info", "根据基金代码获取基金的详细信息", getFundInfoParams, this::getFundInfo);
        
        // get_fund_historical_nav
        Map<String, FunctionProperty> getFundNavProps = new HashMap<>();
        getFundNavProps.put("fund_code", new FunctionProperty("string", "基金代码，例如：000001", Collections.emptyList()));
        getFundNavProps.put("start_date", new FunctionProperty("string", "查询起始日期，格式：YYYY-MM-DD", Collections.emptyList()));
        getFundNavProps.put("end_date", new FunctionProperty("string", "查询结束日期，格式：YYYY-MM-DD", Collections.emptyList()));
        Set<String> getFundNavRequired = new HashSet<>();
        getFundNavRequired.add("fund_code");
        getFundNavRequired.add("start_date");
        getFundNavRequired.add("end_date");
        FunctionParameters getFundNavParams = new FunctionParameters("object", getFundNavProps, getFundNavRequired);
        registerFunction("get_fund_historical_nav", "查询基金在指定日期范围内的历史净值", getFundNavParams, this::getFundHistoricalNav);

        // get_fund_performance
        Map<String, FunctionProperty> getFundPerfProps = new HashMap<>();
        getFundPerfProps.put("fund_code", new FunctionProperty("string", "基金代码，例如：000001", Collections.emptyList()));
        Set<String> getFundPerfRequired = new HashSet<>();
        getFundPerfRequired.add("fund_code");
        FunctionParameters getFundPerfParams = new FunctionParameters("object", getFundPerfProps, getFundPerfRequired);
        registerFunction("get_fund_performance", "根据基金代码获取最新的基金表现", getFundPerfParams, this::getFundPerformance);

        // list_funds
        Map<String, FunctionProperty> listFundsProps = new HashMap<>();
        listFundsProps.put("fund_type", new FunctionProperty("string", "基金类型，例如：股票型、债券型、混合型", Collections.emptyList()));
        Set<String> listFundsRequired = new HashSet<>();
        listFundsRequired.add("fund_type");
        FunctionParameters listFundsParams = new FunctionParameters("object", listFundsProps, listFundsRequired);
        registerFunction("list_funds", "根据基金类型列出基金", listFundsParams, this::listFunds);

        // get_strategy_info
        Map<String, FunctionProperty> getStrategyProps = new HashMap<>();
        getStrategyProps.put("strategy_name", new FunctionProperty("string", "投资策略名称", Collections.emptyList()));
        Set<String> getStrategyRequired = new HashSet<>();
        getStrategyRequired.add("strategy_name");
        FunctionParameters getStrategyParams = new FunctionParameters("object", getStrategyProps, getStrategyRequired);
        registerFunction("get_strategy_info", "根据策略名称获取策略的详细信息", getStrategyParams, this::getStrategyInfo);

        // list_strategies
        FunctionParameters listStrategiesParams = new FunctionParameters("object", new HashMap<>(), new HashSet<>());
        registerFunction("list_strategies", "列出所有可用的投资策略", listStrategiesParams, this::listStrategies);

        // get_factor_info
        Map<String, FunctionProperty> getFactorProps = new HashMap<>();
        getFactorProps.put("factor_name", new FunctionProperty("string", "因子名称", Collections.emptyList()));
        Set<String> getFactorRequired = new HashSet<>();
        getFactorRequired.add("factor_name");
        FunctionParameters getFactorParams = new FunctionParameters("object", getFactorProps, getFactorRequired);
        registerFunction("get_factor_info", "根据因子名称获取因子的详细信息", getFactorParams, this::getFactorInfo);
    }
    
    private void registerFunction(String name, String description, FunctionParameters parameters, Function<JsonNode, Object> executor) {
        functions.add(new ChatFunction(name, description, parameters));
        functionMap.put(name, executor);
    }
    
    public Object execute(String functionName, String jsonArgs) {
        if (!functionMap.containsKey(functionName)) {
            return "Unknown function: " + functionName;
        }
        try {
            JsonNode argsNode = objectMapper.readTree(jsonArgs);
            return functionMap.get(functionName).apply(argsNode);
        } catch (JsonProcessingException e) {
            return "Error parsing arguments for function " + functionName;
        }
    }

    public List<ChatFunction> getFunctions() {
        return functions;
    }

    // --- Function Implementations ---

    private Object searchFunds(JsonNode args) {
        Map<String, Object> queryParams = new HashMap<>();
        if (args.has("fund_type")) queryParams.put("fund_type", args.get("fund_type").asText());
        if (args.has("risk_level")) queryParams.put("risk_level", args.get("risk_level").asInt());
        if (args.has("fund_company")) queryParams.put("fund_company", args.get("fund_company").asText());
        return fundInfoMapper.searchFunds(queryParams);
    }

    private Object getFundInfo(JsonNode args) {
        String fundCode = args.get("fund_code").asText();
        return fundInfoMapper.findByCode(fundCode);
    }

    private Object getFundHistoricalNav(JsonNode args) {
        String fundCode = args.get("fund_code").asText();
        String startDate = args.get("start_date").asText();
        String endDate = args.get("end_date").asText();
        FundInfo fundInfo = fundInfoMapper.findByCode(fundCode);
        if (fundInfo == null) {
            return "错误：找不到对应的基金，请检查基金代码是否正确。";
        }
        return fundNetValueMapper.findByFundIdAndDateRange(fundInfo.getId(), startDate, endDate);
    }
    
    private Object getFundPerformance(JsonNode args) {
        String fundCode = args.get("fund_code").asText();
        FundInfo fundInfo = fundInfoMapper.findByCode(fundCode);
        if (fundInfo == null) return null;
        return fundPerformanceMapper.findLatestByFundId(fundInfo.getId());
    }

    private Object listFunds(JsonNode args) {
        String fundType = args.get("fund_type").asText();
        return fundInfoMapper.findByType(fundType);
    }

    private Object getStrategyInfo(JsonNode args) {
        String strategyName = args.get("strategy_name").asText();
        return strategyInfoMapper.findByName(strategyName);
    }

    private Object listStrategies(JsonNode args) {
        return strategyInfoMapper.findAll();
    }

    private Object getFactorInfo(JsonNode args) {
        String factorName = args.get("factor_name").asText();
        return factorInfoMapper.findByName(factorName);
    }

    // A simple DTO for function definition
    public static class ChatFunction {
        private String name;
        private String description;
        private FunctionParameters parameters;

        public ChatFunction(String name, String description, FunctionParameters parameters) {
            this.name = name;
            this.description = description;
            this.parameters = parameters;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public FunctionParameters getParameters() { return parameters; }
    }
}