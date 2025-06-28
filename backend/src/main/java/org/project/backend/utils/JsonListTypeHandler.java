package org.project.backend.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR) // 即使数据库是JSON类型，MyBatis通常通过VARCHAR/TEXT来处理
public class JsonListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final Logger logger = LoggerFactory.getLogger(JsonListTypeHandler.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        try {
            // 将 List<String> 序列化为 JSON 字符串
            ps.setString(i, objectMapper.writeValueAsString(parameter));
        } catch (JsonProcessingException e) {
            logger.error("Error serializing list to JSON string", e);
            throw new SQLException("Error serializing list to JSON string", e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseJson(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseJson(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseJson(cs.getString(columnIndex));
    }

    private List<String> parseJson(String json) throws SQLException {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            // 将 JSON 字符串反序列化为 List<String>
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
//            logger.error("Error parsing JSON string to list", e);
            throw new SQLException("Error parsing JSON string to list", e);
        }
    }
}