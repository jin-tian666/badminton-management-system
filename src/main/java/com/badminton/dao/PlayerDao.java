package com.badminton.dao;

import com.badminton.model.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 选手数据访问类 — 负责 player 表的 CRUD 操作
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class PlayerDao extends BaseDao {

    /**
     * 查询所有选手
     *
     * @return 选手列表
     */
    public List<Player> findAll() {
        List<Player> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, gender, level, phone, register_date FROM player ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToPlayer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PlayerDao] 查询所有选手失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 根据ID查询选手
     *
     * @param id 选手ID
     * @return 选手对象，未找到返回null
     */
    public Player findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, gender, level, phone, register_date FROM player WHERE id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rowToPlayer(rs);
            }
        } catch (SQLException e) {
            System.err.println("[PlayerDao] 查询选手失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return null;
    }

    /**
     * 新增选手
     *
     * @param player 选手对象（不含id）
     * @return 新选手的ID，失败返回-1
     */
    public int insert(Player player) {
        String sql = "INSERT INTO player (name, gender, level, phone) VALUES (?, ?, ?, ?)";
        return executeInsert(sql, player.getName(), player.getGender(),
                player.getLevel(), player.getPhone());
    }

    /**
     * 更新选手信息
     *
     * @param player 选手对象
     * @return 是否更新成功
     */
    public boolean update(Player player) {
        String sql = "UPDATE player SET name = ?, gender = ?, level = ?, phone = ? WHERE id = ?";
        return executeUpdate(sql, player.getName(), player.getGender(),
                player.getLevel(), player.getPhone(), player.getId()) > 0;
    }

    /**
     * 删除选手
     *
     * @param id 选手ID
     * @return 是否删除成功
     */
    public boolean delete(int id) {
        // 先删除该选手的参赛记录和预定记录
        executeUpdate("DELETE FROM match_player WHERE player_id = ?", id);
        executeUpdate("DELETE FROM booking WHERE player_id = ?", id);
        executeUpdate("DELETE FROM record WHERE player_id = ?", id);
        return executeUpdate("DELETE FROM player WHERE id = ?", id) > 0;
    }

    /**
     * 按级别筛选选手
     *
     * @param level 打球级别（初级/中级/高级/专业）
     * @return 符合级别的选手列表
     */
    public List<Player> findByLevel(String level) {
        List<Player> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, gender, level, phone, register_date FROM player WHERE level = ? ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, level);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToPlayer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PlayerDao] 按级别查询失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 按性别筛选选手
     *
     * @param gender 性别（男/女）
     * @return 符合性别的选手列表
     */
    public List<Player> findByGender(String gender) {
        List<Player> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT id, name, gender, level, phone, register_date FROM player WHERE gender = ? ORDER BY id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, gender);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToPlayer(rs));
            }
        } catch (SQLException e) {
            System.err.println("[PlayerDao] 按性别查询失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 将ResultSet当前行转换为Player对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return Player对象
     * @throws SQLException 读取数据失败时抛出
     */
    private Player rowToPlayer(ResultSet rs) throws SQLException {
        return new Player(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("gender"),
            rs.getString("level"),
            rs.getString("phone"),
            rs.getString("register_date")
        );
    }
}
