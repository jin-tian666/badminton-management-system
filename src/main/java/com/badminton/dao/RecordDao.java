package com.badminton.dao;

import com.badminton.model.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 纪录数据访问类 — 负责 record 表的 CRUD 操作与破纪录检测
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class RecordDao extends BaseDao {

    /**
     * 新增纪录
     *
     * @param record 纪录对象
     * @return 新纪录ID，失败返回-1
     */
    public int insert(Record record) {
        String sql = "INSERT INTO record (record_type, record_value, player_id, match_id, is_broken) " +
                     "VALUES (?, ?, ?, ?, ?)";
        return executeInsert(sql, record.getRecordType(), record.getRecordValue(),
                record.getPlayerId(), record.getMatchId(), record.getIsBroken());
    }

    /**
     * 查询所有纪录
     *
     * @return 纪录列表（含选手名）
     */
    public List<Record> findAll() {
        List<Record> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT r.id, r.record_type, r.record_value, r.player_id, r.match_id, " +
                         "r.record_date, r.is_broken, p.name AS player_name " +
                         "FROM record r INNER JOIN player p ON r.player_id = p.id " +
                         "ORDER BY r.record_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RecordDao] 查询所有纪录失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询当前有效纪录（is_broken = 0）
     *
     * @return 有效纪录列表
     */
    public List<Record> findCurrent() {
        List<Record> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT r.id, r.record_type, r.record_value, r.player_id, r.match_id, " +
                         "r.record_date, r.is_broken, p.name AS player_name " +
                         "FROM record r INNER JOIN player p ON r.player_id = p.id " +
                         "WHERE r.is_broken = 0 ORDER BY r.record_type";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RecordDao] 查询当前纪录失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询某选手的纪录
     *
     * @param playerId 选手ID
     * @return 该选手的纪录列表
     */
    public List<Record> findByPlayerId(int playerId) {
        List<Record> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT r.id, r.record_type, r.record_value, r.player_id, r.match_id, " +
                         "r.record_date, r.is_broken, p.name AS player_name " +
                         "FROM record r INNER JOIN player p ON r.player_id = p.id " +
                         "WHERE r.player_id = ? ORDER BY r.record_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RecordDao] 查询选手纪录失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 按类型查询某纪录的最高值
     *
     * @param recordType 纪录类型
     * @return 该类型当前最高纪录值（字符串），无纪录返回null
     */
    public String findBestByType(String recordType) {
        String sql = "SELECT record_value FROM record WHERE record_type = ? AND is_broken = 0 " +
                     "ORDER BY CAST(record_value AS INTEGER) DESC LIMIT 1";
        return querySingleString(sql, recordType);
    }

    /**
     * 将某类型旧纪录标记为已打破
     *
     * @param recordType 纪录类型
     * @return 更新的记录数
     */
    public int markBroken(String recordType) {
        String sql = "UPDATE record SET is_broken = 1 WHERE record_type = ? AND is_broken = 0";
        return executeUpdate(sql, recordType);
    }

    /**
     * 按类型查询被打破的纪录（历史纪录）
     *
     * @return 已打破的纪录列表
     */
    public List<Record> findBroken() {
        List<Record> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT r.id, r.record_type, r.record_value, r.player_id, r.match_id, " +
                         "r.record_date, r.is_broken, p.name AS player_name " +
                         "FROM record r INNER JOIN player p ON r.player_id = p.id " +
                         "WHERE r.is_broken = 1 ORDER BY r.record_date DESC";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToRecord(rs));
            }
        } catch (SQLException e) {
            System.err.println("[RecordDao] 查询历史纪录失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 将ResultSet当前行转换为Record对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return Record对象（含选手名）
     * @throws SQLException 读取数据失败时抛出
     */
    private Record rowToRecord(ResultSet rs) throws SQLException {
        Record record = new Record(
            rs.getInt("id"),
            rs.getString("record_type"),
            rs.getString("record_value"),
            rs.getInt("player_id"),
            rs.getInt("match_id"),
            rs.getString("record_date"),
            rs.getInt("is_broken")
        );
        record.setPlayerName(rs.getString("player_name"));
        return record;
    }
}
