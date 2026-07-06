package com.badminton.dao;

import com.badminton.model.Court;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 场地数据访问类 — 负责 court 表的 CRUD 操作
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class CourtDao extends BaseDao {

    /**
     * 查询所有场地
     *
     * @return 场地列表
     */
    public List<Court> findAll() {
        List<Court> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, area, status FROM court ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToCourt(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CourtDao] 查询所有场地失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 根据ID查询场地
     *
     * @param id 场地ID
     * @return 场地对象，未找到返回null
     */
    public Court findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, area, status FROM court WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rowToCourt(rs);
            }
        } catch (SQLException e) {
            System.err.println("[CourtDao] 查询场地失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return null;
    }

    /**
     * 按区域查询场地
     *
     * @param area 区域名称（东面/南面/西面/北面）
     * @return 该区域的场地列表
     */
    public List<Court> findByArea(String area) {
        List<Court> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, area, status FROM court WHERE area = ? ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, area);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToCourt(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CourtDao] 按区域查询失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询所有可用场地
     *
     * @return 状态为"可用"的场地列表
     */
    public List<Court> findAvailable() {
        List<Court> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, area, status FROM court WHERE status = 1 ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToCourt(rs));
            }
        } catch (SQLException e) {
            System.err.println("[CourtDao] 查询可用场地失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 更新场地状态
     *
     * @param id     场地ID
     * @param status 状态（1=可用，0=维护中）
     * @return 是否更新成功
     */
    public boolean updateStatus(int id, int status) {
        String sql = "UPDATE court SET status = ? WHERE id = ?";
        return executeUpdate(sql, status, id) > 0;
    }

    /**
     * 将ResultSet当前行转换为Court对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return Court对象
     * @throws SQLException 读取数据失败时抛出
     */
    private Court rowToCourt(ResultSet rs) throws SQLException {
        return new Court(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("area"),
            rs.getInt("status")
        );
    }
}
