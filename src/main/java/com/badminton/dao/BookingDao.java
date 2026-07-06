package com.badminton.dao;

import com.badminton.model.Booking;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 场地预定数据访问类 — 负责 booking 表的 CRUD 操作与时间冲突检测
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class BookingDao extends BaseDao {

    /**
     * 新增预定记录
     *
     * @param booking 预定对象
     * @return 新预定ID，失败返回-1
     */
    public int insert(Booking booking) {
        String sql = "INSERT INTO booking (court_id, player_id, book_date, start_time, end_time, purpose) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        return executeInsert(sql, booking.getCourtId(), booking.getPlayerId(),
                booking.getBookDate(), booking.getStartTime(), booking.getEndTime(),
                booking.getPurpose());
    }

    /**
     * 取消预定（删除记录）
     *
     * @param id 预定ID
     * @return 是否删除成功
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM booking WHERE id = ?";
        return executeUpdate(sql, id) > 0;
    }

    /**
     * 根据ID查询预定
     *
     * @param id 预定ID
     * @return 预定对象，未找到返回null
     */
    public Booking findById(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT b.id, b.court_id, b.player_id, b.book_date, b.start_time, b.end_time, " +
                         "b.purpose, c.name AS court_name, p.name AS player_name " +
                         "FROM booking b " +
                         "INNER JOIN court c ON b.court_id = c.id " +
                         "INNER JOIN player p ON b.player_id = p.id " +
                         "WHERE b.id = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rowToBooking(rs);
            }
        } catch (SQLException e) {
            System.err.println("[BookingDao] 查询预定失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return null;
    }

    /**
     * 查询所有预定记录
     *
     * @return 预定列表（含场地名和选手名）
     */
    public List<Booking> findAll() {
        List<Booking> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT b.id, b.court_id, b.player_id, b.book_date, b.start_time, b.end_time, " +
                         "b.purpose, c.name AS court_name, p.name AS player_name " +
                         "FROM booking b " +
                         "INNER JOIN court c ON b.court_id = c.id " +
                         "INNER JOIN player p ON b.player_id = p.id " +
                         "ORDER BY b.book_date DESC, b.start_time";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDao] 查询所有预定失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 按选手ID查询预定
     *
     * @param playerId 选手ID
     * @return 该选手的预定列表
     */
    public List<Booking> findByPlayerId(int playerId) {
        List<Booking> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT b.id, b.court_id, b.player_id, b.book_date, b.start_time, b.end_time, " +
                         "b.purpose, c.name AS court_name, p.name AS player_name " +
                         "FROM booking b " +
                         "INNER JOIN court c ON b.court_id = c.id " +
                         "INNER JOIN player p ON b.player_id = p.id " +
                         "WHERE b.player_id = ? " +
                         "ORDER BY b.book_date DESC, b.start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDao] 按选手查询预定失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 检测场地时间冲突（同一场地同一时间段不能重复预定 / 同一选手同一时间只能预定一个）
     *
     * @param courtId   场地ID（传入-1表示只检测选手冲突）
     * @param playerId  选手ID
     * @param date      预定日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param excludeId 排除的预定ID（更新时排除自身），传入0表示不排除
     * @return 冲突的预定列表
     */
    public List<Booking> findConflicting(int courtId, int playerId, String date,
                                          String startTime, String endTime, int excludeId) {
        List<Booking> list = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            // 场地冲突 或 选手冲突（同一选手同一时间不能预定多个场地）
            String sql = "SELECT b.id, b.court_id, b.player_id, b.book_date, b.start_time, b.end_time, " +
                         "b.purpose, c.name AS court_name, p.name AS player_name " +
                         "FROM booking b " +
                         "INNER JOIN court c ON b.court_id = c.id " +
                         "INNER JOIN player p ON b.player_id = p.id " +
                         "WHERE b.book_date = ? AND b.id != ? " +
                         "  AND b.start_time < ? AND b.end_time > ? " +
                         "  AND (b.court_id = ? OR b.player_id = ?) " +
                         "ORDER BY b.start_time";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, date);
            pstmt.setInt(2, excludeId);
            pstmt.setString(3, endTime);
            pstmt.setString(4, startTime);
            pstmt.setInt(5, courtId);
            pstmt.setInt(6, playerId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rowToBooking(rs));
            }
        } catch (SQLException e) {
            System.err.println("[BookingDao] 冲突检测失败：" + e.getMessage());
        } finally {
            close(rs, pstmt, conn);
        }
        return list;
    }

    /**
     * 将ResultSet当前行转换为Booking对象
     *
     * @param rs ResultSet（已定位到某行）
     * @return Booking对象（含扩展字段）
     * @throws SQLException 读取数据失败时抛出
     */
    private Booking rowToBooking(ResultSet rs) throws SQLException {
        Booking booking = new Booking(
            rs.getInt("id"),
            rs.getInt("court_id"),
            rs.getInt("player_id"),
            rs.getString("book_date"),
            rs.getString("start_time"),
            rs.getString("end_time"),
            rs.getString("purpose")
        );
        booking.setCourtName(rs.getString("court_name"));
        booking.setPlayerName(rs.getString("player_name"));
        return booking;
    }
}
