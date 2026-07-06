package com.badminton.dao;

import com.badminton.model.MatchPlayer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 比赛-选手关联数据访问类 — 负责 match_player 表的 CRUD 操作
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MatchPlayerDao extends BaseDao {

    /**
     * 选手报名参赛
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @return 新关联ID，失败返回-1
     */
    public int insert(int matchId, int playerId) {
        String sql = "INSERT INTO match_player (match_id, player_id) VALUES (?, ?)";
        return executeInsert(sql, matchId, playerId);
    }

    /**
     * 删除参赛记录（取消报名）
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @return 是否删除成功
     */
    public boolean delete(int matchId, int playerId) {
        String sql = "DELETE FROM match_player WHERE match_id = ? AND player_id = ?";
        return executeUpdate(sql, matchId, playerId) > 0;
    }

    /**
     * 查询某比赛的所有参赛选手
     *
     * @param matchId 比赛ID
     * @return 参赛记录列表（含选手姓名）
     */
    public List<MatchPlayer> findByMatchId(int matchId) {
        List<MatchPlayer> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT mp.id, mp.match_id, mp.player_id, mp.score, mp.rank, p.name AS player_name " +
                         "FROM match_player mp INNER JOIN player p ON mp.player_id = p.id " +
                         "WHERE mp.match_id = ? ORDER BY mp.rank ASC, mp.score DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, matchId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MatchPlayer mp = rowToMatchPlayer(rs);
                mp.setPlayerName(rs.getString("player_name"));
                list.add(mp);
            }
        } catch (SQLException e) {
            System.err.println("[MatchPlayerDao] 查询比赛选手失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 查询某选手参加的所有比赛记录
     *
     * @param playerId 选手ID
     * @return 参赛记录列表（含比赛名称）
     */
    public List<MatchPlayer> findByPlayerId(int playerId) {
        List<MatchPlayer> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT mp.id, mp.match_id, mp.player_id, mp.score, mp.rank, m.name AS match_name " +
                         "FROM match_player mp INNER JOIN match_info m ON mp.match_id = m.id " +
                         "WHERE mp.player_id = ? ORDER BY m.match_date DESC";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                MatchPlayer mp = rowToMatchPlayer(rs);
                mp.setMatchName(rs.getString("match_name"));
                list.add(mp);
            }
        } catch (SQLException e) {
            System.err.println("[MatchPlayerDao] 查询选手比赛失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 录入选手成绩和名次
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @param score    得分
     * @param rank     名次
     * @return 是否更新成功
     */
    public boolean updateResult(int matchId, int playerId, int score, int rank) {
        String sql = "UPDATE match_player SET score = ?, rank = ? WHERE match_id = ? AND player_id = ?";
        return executeUpdate(sql, score, rank, matchId, playerId) > 0;
    }

    /**
     * 检查选手是否已报名某比赛
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @return true=已报名，false=未报名
     */
    public boolean isPlayerInMatch(int matchId, int playerId) {
        String sql = "SELECT COUNT(*) FROM match_player WHERE match_id = ? AND player_id = ?";
        return querySingleInt(sql, matchId, playerId) > 0;
    }

    /**
     * 获取某比赛已报名人数
     *
     * @param matchId 比赛ID
     * @return 报名人数
     */
    public int countByMatchId(int matchId) {
        String sql = "SELECT COUNT(*) FROM match_player WHERE match_id = ?";
        return querySingleInt(sql, matchId);
    }

    /**
     * 将ResultSet当前行转换为MatchPlayer对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return MatchPlayer对象
     * @throws SQLException 读取数据失败时抛出
     */
    private MatchPlayer rowToMatchPlayer(ResultSet rs) throws SQLException {
        return new MatchPlayer(
            rs.getInt("id"),
            rs.getInt("match_id"),
            rs.getInt("player_id"),
            rs.getInt("score"),
            rs.getInt("rank")
        );
    }
}
