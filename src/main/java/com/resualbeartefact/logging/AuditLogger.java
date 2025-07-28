package com.resualbeartefact.logging;
import java.sql.*;

public class AuditLogger {
    private final Connection conn;

    public AuditLogger(String dbPath) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS audit (timestamp TEXT, topic TEXT, key TEXT, message TEXT, status TEXT, error TEXT)");
        }
    }

    public void log(String topic, String key, String msg, String status, String error) {
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO audit (timestamp, topic, key, message, status, error) VALUES (datetime('now'), ?, ?, ?, ?, ?)")) {
            ps.setString(1, topic);
            ps.setString(2, key);
            ps.setString(3, msg);
            ps.setString(4, status);
            ps.setString(5, error);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        conn.close();
    }
}

