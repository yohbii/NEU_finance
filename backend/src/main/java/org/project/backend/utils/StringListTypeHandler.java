package org.project.backend.utils;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.util.StringUtils;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@MappedTypes(List.class) // 指定这个 Handler 要处理的 Java 类型是 List
@MappedJdbcTypes(JdbcType.VARCHAR) // 指定对应的数据库字段类型是 VARCHAR
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final String SEPARATOR = ",";

    /**
     * 将 Java 的 List<String> 类型转换为数据库需要的字符串
     * @param ps PreparedStatement 对象
     * @param i 参数位置
     * @param parameter List<String> 类型的参数
     * @param jdbcType 数据库类型
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null || parameter.isEmpty()) {
            ps.setString(i, "");
        } else {
            // 使用逗号将 List 中的元素连接成一个字符串
            String result = parameter.stream().collect(Collectors.joining(SEPARATOR));
            ps.setString(i, result);
        }
    }

    /**
     * 从数据库中获取字符串，并转换为 List<String> (通过列名)
     * @param rs ResultSet 对象
     * @param columnName 列名
     * @return 转换后的 List<String>
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String resultString = rs.getString(columnName);
        if (!StringUtils.hasText(resultString)) {
            return Collections.emptyList();
        }
        // 按逗号分割字符串，并转换为 List
        return Arrays.asList(resultString.split(SEPARATOR));
    }

    /**
     * 从数据库中获取字符串，并转换为 List<String> (通过列索引)
     */
    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String resultString = rs.getString(columnIndex);
        if (!StringUtils.hasText(resultString)) {
            return Collections.emptyList();
        }
        return Arrays.asList(resultString.split(SEPARATOR));
    }

    /**
     * 从数据库中获取字符串，并转换为 List<String> (用于存储过程)
     */
    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String resultString = cs.getString(columnIndex);
        if (!StringUtils.hasText(resultString)) {
            return Collections.emptyList();
        }
        return Arrays.asList(resultString.split(SEPARATOR));
    }
}