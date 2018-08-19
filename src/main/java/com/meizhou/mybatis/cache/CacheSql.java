package com.meizhou.mybatis.cache;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CacheSql {

    private String sql;

    private String table;

    private Map<String, Object> parameterMap;

    public static CacheSql buildCacheSql(String originSql, List<Object> objectList, String dbType) {
        CacheSql cacheSql = new CacheSql();
        String sql = SQLUtils.format(originSql, dbType, objectList);
        List<SQLStatement> stmtList = SQLUtils.parseStatements(sql, dbType);
        SchemaStatVisitor statVisitor = SQLUtils.createSchemaStatVisitor(dbType);
        for (SQLStatement stmt : stmtList) {
            stmt.accept(statVisitor);
        }
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        for (Map.Entry<TableStat.Name, TableStat> entry : statVisitor.getTables().entrySet()) {
            cacheSql.setTable(entry.getKey().getName());
            //当为insert语句的时候
            if (entry.getValue().getInsertCount() > 0) {
                List<Object> values = new ArrayList<>();
                ParameterizedOutputVisitorUtils.parameterize(sql, dbType, values);
                int i = 0;
                for (TableStat.Column column : statVisitor.getColumns()) {
                    parameterMap.put(column.getName(), values.get(i));
                    i++;
                }
            }
            break;
        }
        //取出来where条件
        for (TableStat.Condition entry : statVisitor.getConditions()) {
            if (entry.getOperator().equals("=")) {
                parameterMap.put(entry.getColumn().getName().replaceAll("`", ""), entry.getValues().get(0));
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

    @Override
    public String toString() {
        return "CacheSql{" +
                "sql='" + sql + '\'' +
                ", table='" + table + '\'' +
                ", parameterMap=" + parameterMap +
                '}';
    }
}
