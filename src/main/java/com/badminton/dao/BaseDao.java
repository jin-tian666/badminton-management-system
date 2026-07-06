package com.badminton.dao;

import com.badminton.util.DBUtil;

import java.sql.*;

/**
 * 基础数据访问类 — 提供通用的JDBC操作方法
 * 所有具体DAO类均继承此类，复用连接获取与资源关闭逻辑
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class BaseDao {

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException 连接失败时抛出
     */
    protected Connection getConnection() throws SQLException {
        return DBUtil.getConnection();
    }

    /**
     * 关闭数据库资源
     *
     * @param rs   ResultSet（可为null）
     * @param stmt Statement / PreparedStatement（可为null）
     * @param conn Connection（可为null）
     */
    protected void close(ResultSet rs, Statement stmt, Connection conn) {
        DBUtil.close(rs, stmt, conn);
    }

    /**
     * 执行INSERT/UPDATE/DELETE语句
     *
     * @param sql    SQL语句
     * @param params 参数值（可变参数）
     * @return 受影响的行数，失败返回-1
     */
    protected int executeUpdate(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB错误] " + e.getMessage());
            return -1;
        } finally {
            close(null, pstmt, conn);
        }
    }

    /**
     * 执行INSERT语句并返回自增主键
     *
     * @param sql    INSERT语句
     * @param params 参数值
     * @return 自增主键值，失败返回-1
     */
    protected int executeInsert(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            setParams(pstmt, params);
            pstmt.executeUpdate();
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("[DB错误] " + e.getMessage());
            return -1;
        } finally {
            close(rs, pstmt, conn);
        }
    }

    /**
     * 为PreparedStatement设置参数
     *
     * @param pstmt  PreparedStatement对象
     * @param params 参数值数组
     * @throws SQLException 设置参数失败时抛出
     */
    protected void setParams(PreparedStatement pstmt, Object... params) throws SQLException {
        if (params != null) {
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
        }
    }

    /**
     * 执行查询并返回单个整数值（如COUNT、MAX等聚合查询）
     *
     * @param sql    查询SQL
     * @param params 参数值
     * @return 查询到的整数值，未查到返回0
     */
    protected int querySingleInt(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            System.err.println("[DB错误] " + e.getMessage());
            return 0;
        } finally {
            close(rs, pstmt, conn);
        }
    }

    /**
     * 执行查询并返回单个字符串值
     *
     * @param sql    查询SQL
     * @param params 参数值
     * @return 查询到的字符串值，未查到返回null
     */
    protected String querySingleString(String sql, Object... params) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            setParams(pstmt, params);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("[DB错误] " + e.getMessage());
            return null;
        } finally {
            close(rs, pstmt, conn);
        }
    }
}
