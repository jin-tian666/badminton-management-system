package com.badminton.dao;

import com.badminton.model.Match;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 比赛数据访问类 — 负责 match_info 表的 CRUD 操作
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MatchDao extends BaseDao {

    /**
     * 查询所有比赛
     *
     * @return 比赛列表
     */
    public List<Match> findAll() {
        List<Match> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, match_type, match_date, start_time, end_time, court_id, status " +
                         "FROM match_info ORDER BY match_date DESC, start_time";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToMatch(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 查询所有比赛失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 根据ID查询比赛
     *
     * @param id 比赛ID
     * @return 比赛对象，未找到返回null
     */
    public Match findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, match_type, match_date, start_time, end_time, court_id, status " +
                         "FROM match_info WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rowToMatch(rs);
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 查询比赛失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return null;
    }

    /**
     * 新增比赛
     *
     * @param match 比赛对象
     * @return 新比赛ID，失败返回-1
     */
    public int insert(Match match) {
        String sql = "INSERT INTO match_info (name, match_type, match_date, start_time, end_time, court_id, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, match.getName(), match.getMatchType(),
                match.getMatchDate(), match.getStartTime(), match.getEndTime(),
                match.getCourtId(), match.getStatus());
    }

    /**
     * 更新比赛状态
     *
     * @param id     比赛ID
     * @param status 状态（待开始/进行中/已结束/已取消）
     * @return 是否更新成功
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE match_info SET status = ? WHERE id = ?";
        return executeUpdate(sql, status, id) > 0;
    }

    /**
     * 按日期查询比赛
     *
     * @param date 日期（格式：YYYY-MM-DD）
     * @return 该日期的比赛列表
     */
    public List<Match> findByDate(String date) {
        List<Match> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, match_type, match_date, start_time, end_time, court_id, status " +
                         "FROM match_info WHERE match_date = ? ORDER BY start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToMatch(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 按日期查询失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 按状态查询比赛
     *
     * @param status 状态（待开始/进行中/已结束/已取消）
     * @return 该状态的比赛列表
     */
    public List<Match> findByStatus(String status) {
        List<Match> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, match_type, match_date, start_time, end_time, court_id, status " +
                         "FROM match_info WHERE status = ? ORDER BY match_date, start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToMatch(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 按状态查询失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询某选手已报名的所有比赛（用于冲突检测）
     *
     * @param playerId 选手ID
     * @return 该选手的比赛列表
     */
    public List<Match> findByPlayerId(int playerId) {
        List<Match> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT m.id, m.name, m.match_type, m.match_date, m.start_time, m.end_time, " +
                         "m.court_id, m.status " +
                         "FROM match_info m INNER JOIN match_player mp ON m.id = mp.match_id " +
                         "WHERE mp.player_id = ? AND m.status IN ('待开始', '进行中') " +
                         "ORDER BY m.match_date, m.start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToMatch(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 查询选手比赛失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询与指定时间段冲突的比赛（场地维度冲突检测）
     *
     * @param courtId   场地ID
     * @param date      日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param excludeId 排除的比赛ID（更新时排除自身）
     * @return 冲突的比赛列表
     */
    public List<Match> findConflicting(int courtId, String date, String startTime,
                                        String endTime, int excludeId) {
        List<Match> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, match_type, match_date, start_time, end_time, court_id, status " +
                         "FROM match_info " +
                         "WHERE court_id = ? AND match_date = ? " +
                         "  AND status IN ('待开始', '进行中') " +
                         "  AND id != ? " +
                         "  AND start_time < ? AND end_time > ? " +
                         "ORDER BY start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, courtId);
            pstmt.setString(2, date);
            pstmt.setInt(3, excludeId);
            pstmt.setString(4, endTime);
            pstmt.setString(5, startTime);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToMatch(rs));
            }
        } catch (SQLException e) {
            System.err.println("[MatchDao] 冲突检测失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 将ResultSet当前行转换为Match对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return Match对象
     * @throws SQLException 读取数据失败时抛出
     */
    private Match rowToMatch(ResultSet rs) throws SQLException {
        return new Match(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("match_type"),
            rs.getString("match_date"),
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getInt("court_id"),
            rs.getString("status")
        );
    }
}
