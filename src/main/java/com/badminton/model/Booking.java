package com.badminton.model;

/**
 * 场地预定实体类 — 对应数据库 booking 表
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class Booking {

    private int id;          // 预定ID（主键，自增）
    private int courtId;     // 场地ID
    private int playerId;    // 预定人ID
    private String bookDate; // 预定日期
    private String startTime;// 开始时间
    private String endTime;  // 结束时间
    private String purpose;  // 用途说明

    /* 扩展字段（非数据库字段，用于展示） */
    private String courtName;  // 场地名称（JOIN查询时使用）
    private String playerName; // 选手姓名（JOIN查询时使用）

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public Booking() {}

    /**
     * 全参构造
     *
     * @param id        预定ID
     * @param courtId   场地ID
     * @param playerId  预定人ID
     * @param bookDate  预定日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param purpose   用途说明
     */
    public Booking(int id, int courtId, int playerId, String bookDate,
                   String startTime, String endTime, String purpose) {
        this.id = id;
        this.courtId = courtId;
        this.playerId = playerId;
        this.bookDate = bookDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    /**
     * 不含ID的构造（用于新增）
     *
     * @param courtId   场地ID
     * @param playerId  预定人ID
     * @param bookDate  预定日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param purpose   用途说明
     */
    public Booking(int courtId, int playerId, String bookDate,
                   String startTime, String endTime, String purpose) {
        this.courtId = courtId;
        this.playerId = playerId;
        this.bookDate = bookDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.purpose = purpose;
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCourtId() { return courtId; }
    public void setCourtId(int courtId) { this.courtId = courtId; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public String getBookDate() { return bookDate; }
    public void setBookDate(String bookDate) { this.bookDate = bookDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    @Override
    public String toString() {
        return String.format("预定[ID=%d, 场地=%s, 预定人=%s, 日期=%s, 时间=%s-%s, 用途=%s]",
                id, courtName == null ? "场地" + courtId : courtName,
                playerName == null ? "选手" + playerId : playerName,
                bookDate, startTime, endTime,
                purpose == null || purpose.isEmpty() ? "无" : purpose);
    }
}
