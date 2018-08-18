package com.meizhou.mybatis.cache;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheSql {

    private String sql;

    private String table;

    private Map<String, Object> parameterMap;

    public static CacheSql buildCacheSql(String originSql, List<Object> objectList) {
        CacheSql cacheSql = new CacheSql();
        String sql = SQLUtils.format(originSql, JdbcConstants.MYSQL, objectList);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(JdbcConstants.MYSQL);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(statVisitor);
        }
        for (Map.Entry<TableStat.Name, TableStat> entry : statVisitor.getTables().entrySet()) {
            cacheSql.setTable(entry.getKey().getName());
            break;
        }
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        for (TableStat.Condition entry : statVisitor.getConditions()) {
            if (entry.getOperator().equals("=")) {
                parameterMap.put(entry.getColumn().getName(), entry.getValues().get(0));
            }
        }
        cacheSql.setParameterMap(parameterMap);
        cacheSql.setSql(sql);
        return cacheSql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

}
