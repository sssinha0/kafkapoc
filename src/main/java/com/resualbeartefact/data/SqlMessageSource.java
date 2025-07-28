package com.resualbeartefact.data;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.*;
import java.util.*;

public class SqlMessageSource {
    SqlInfo sqlInfo = new SqlInfo();
    public SqlMessageSource(SqlInfo sqlInfo) {
        this.sqlInfo = sqlInfo;
    }

    public List<Map<String, Object>> getMessages() throws Exception {
        List<String> messages = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        String jdbcUrl = "jdbc:mysql://"+sqlInfo.getHost()+":"+sqlInfo.getPort()+"/"+sqlInfo.getDb();
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(jdbcUrl, sqlInfo.getUser(), sqlInfo.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sqlInfo.getQuery())) {

            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= colCount; i++) {
                    row.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;

    }
}

