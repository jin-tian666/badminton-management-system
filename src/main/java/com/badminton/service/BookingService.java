package com.badminton.service;

import com.badminton.dao.BookingDao;
import com.badminton.dao.CourtDao;
import com.badminton.dao.PlayerDao;
import com.badminton.model.Booking;
import com.badminton.model.Court;
import com.badminton.model.Player;

import java.util.List;

/**
 * 场地预定业务逻辑类 — 预定/取消/查询，含时间冲突检测
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class BookingService {

    private final BookingDao bookingDao = new BookingDao();
    private final CourtDao courtDao = new CourtDao();
    private final PlayerDao playerDao = new PlayerDao();

    /**
     * 预定场地（含冲突检测）
     *
     * @param booking 预定对象
     * @return 操作结果描述
     */
    public String bookCourt(Booking booking) {
        // 1. 基本校验
        if (booking.getBookDate() == null || booking.getBookDate().trim().isEmpty()) {
            return "预定日期不能为空！";
        }
        if (booking.getStartTime().compareTo(booking.getEndTime()) >= 0) {
            return "开始时间必须早于结束时间！";
        }

        // 2. 检查场地是否存在且可用
        Court court = courtDao.findById(booking.getCourtId());
        if (court == null) {
            return "场地不存在！";
        }
        if (court.getStatus() == 0) {
            return "场地[" + court.getName() + "]正在维护中，无法预定！";
        }

        // 3. 检查选手是否存在
        Player player = playerDao.findById(booking.getPlayerId());
        if (player == null) {
            return "选手不存在！";
        }

        // 4. 冲突检测（场地冲突 + 选手时间冲突）
        List<Booking> conflicts = bookingDao.findConflicting(
            booking.getCourtId(), booking.getPlayerId(),
            booking.getBookDate(), booking.getStartTime(), booking.getEndTime(), 0
        );
        if (!conflicts.isEmpty()) {
            Booking conflict = conflicts.get(0);
            if (conflict.getCourtId() == booking.getCourtId()) {
                return "场地冲突！场地[" + court.getName() + "]在" + booking.getBookDate() + " "
                     + booking.getStartTime() + "-" + booking.getEndTime()
                     + " 已被[" + conflict.getPlayerName() + "]预定。";
            } else {
                return "时间冲突！您在该时段已有其他预定（场地["
                     + conflict.getCourtName() + "]，"
                     + conflict.getStartTime() + "-" + conflict.getEndTime() + "）。";
            }
        }

        // 5. 创建预定
        int id = bookingDao.insert(booking);
        return id > 0 ? "预定成功！ID=" + id + "，场地：" + court.getName()
                      + "，时间：" + booking.getBookDate() + " " + booking.getStartTime() + "-" + booking.getEndTime()
                      : "预定失败！";
    }

    /**
     * 取消预定
     *
     * @param bookingId 预定ID
     * @return 操作结果描述
     */
    public String cancelBooking(int bookingId) {
        Booking booking = bookingDao.findById(bookingId);
        if (booking == null) {
            return "预定记录不存在！";
        }
        boolean ok = bookingDao.delete(bookingId);
        return ok ? "预定已取消（场地：" + booking.getCourtName()
                    + "，" + booking.getBookDate() + " " + booking.getStartTime() + "-" + booking.getEndTime() + "）。"
                  : "取消失败！";
    }

    /**
     * 获取所有预定记录
     *
     * @return 预定列表
     */
    public List<Booking> listAll() {
        return bookingDao.findAll();
    }

    /**
     * 获取某选手的预定记录
     *
     * @param playerId 选手ID
     * @return 预定列表
     */
    public List<Booking> listByPlayer(int playerId) {
        return bookingDao.findByPlayerId(playerId);
    }

    /**
     * 根据ID获取预定
     *
     * @param id 预定ID
     * @return 预定对象
     */
    public Booking getById(int id) {
        return bookingDao.findById(id);
    }
}
