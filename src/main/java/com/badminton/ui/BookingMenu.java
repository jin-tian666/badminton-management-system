package com.badminton.ui;

import com.badminton.model.Booking;
import com.badminton.service.BookingService;
import com.badminton.service.CourtService;
import com.badminton.util.InputUtil;

import java.util.List;

/**
 * 场地预定菜单 — 预定/取消/查看场地预定
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class BookingMenu {

    private final BookingService bookingService = new BookingService();
    private final CourtService courtService = new CourtService();

    /**
     * 显示场地预定子菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n========== 场地预定 ==========");
            System.out.println("  1. 查看所有预定");
            System.out.println("  2. 预定场地");
            System.out.println("  3. 取消预定");
            System.out.println("  4. 按选手查看预定");
            System.out.println("  0. 返回主菜单");
            System.out.println("==============================");

            int choice = InputUtil.readInt("请选择：");
            switch (choice) {
                case 1: listAll(); break;
                case 2: bookCourt(); break;
                case 3: cancelBooking(); break;
                case 4: listByPlayer(); break;
                case 0: return;
                default: System.out.println("[错误] 无效选项！");
            }
        }
    }

    /**
     * 查看所有预定
     */
    private void listAll() {
        List<Booking> bookings = bookingService.listAll();
        printBookingList(bookings, "全部预定");
    }

    /**
     * 预定场地
     */
    private void bookCourt() {
        System.out.println("\n---------- 预定场地 ----------");

        // 显示可用场地
        System.out.println("可用场地：");
        courtService.listAvailable().forEach(c ->
            System.out.printf("  ID=%d  %s(%s)\n", c.getId(), c.getName(), c.getArea())
        );

        int courtId = InputUtil.readInt("场地ID：");
        int playerId = InputUtil.readInt("预定人ID：");
        String date = InputUtil.readDate("预定日期(YYYY-MM-DD)：");
        String startTime = InputUtil.readTime("开始时间(HH:MM)：");
        String endTime = InputUtil.readTime("结束时间(HH:MM)：");
        String purpose = InputUtil.readString("用途说明（可选）：");

        Booking booking = new Booking(courtId, playerId, date, startTime, endTime, purpose);
        String result = bookingService.bookCourt(booking);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 取消预定
     */
    private void cancelBooking() {
        System.out.println("\n---------- 取消预定 ----------");
        int bookingId = InputUtil.readInt("预定ID：");
        System.out.print("确认取消？(y/n)：");
        String confirm = InputUtil.readString("");
        if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
            String result = bookingService.cancelBooking(bookingId);
            System.out.println(result);
        } else {
            System.out.println("已取消。");
        }
        InputUtil.pressEnter();
    }

    /**
     * 按选手查看预定
     */
    private void listByPlayer() {
        int playerId = InputUtil.readInt("选手ID：");
        List<Booking> bookings = bookingService.listByPlayer(playerId);
        printBookingList(bookings, "选手" + playerId + "的预定");
    }

    /**
     * 打印预定列表（通用方法）
     *
     * @param bookings 预定列表
     * @param title    标题
     */
    private void printBookingList(List<Booking> bookings, String title) {
        System.out.println("\n---------- " + title + " ----------");
        if (bookings.isEmpty()) {
            System.out.println("暂无预定记录。");
            InputUtil.pressEnter();
            return;
        }
        System.out.printf("%-4s  %-8s  %-6s  %-12s  %-16s  %-10s\n",
            "ID", "场地", "预定人", "日期", "时间", "用途");
        System.out.println("--------------------------------------------------------------");
        for (Booking b : bookings) {
            System.out.printf("%-4d  %-8s  %-6s  %-12s  %-16s  %-10s\n",
                b.getId(), b.getCourtName(), b.getPlayerName(),
                b.getBookDate(), b.getStartTime() + "-" + b.getEndTime(),
                b.getPurpose() != null && !b.getPurpose().isEmpty() ? b.getPurpose() : "-");
        }
        System.out.println("--------------------------------------------------------------");
        System.out.println("共 " + bookings.size() + " 条预定。");
        InputUtil.pressEnter();
    }
}
