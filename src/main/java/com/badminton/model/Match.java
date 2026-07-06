package com.badminton.model;

/**
 * 比赛实体类 — 对应数据库 match_info 表
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class Match {

    private int id;           // 比赛ID（主键，自增）
    private String name;      // 比赛名称
    private String matchType; // 比赛类型（男单/女单/男双/女双/混双）
    private String matchDate; // 比赛日期
    private String startTime; // 开始时间
    private String endTime;   // 结束时间
    private int courtId;      // 使用场地ID
    private String status;    // 状态（待开始/进行中/已结束/已取消）

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public Match() {}

    /**
     * 全参构造
     *
     * @param id        比赛ID
     * @param name      比赛名称
     * @param matchType 比赛类型
     * @param matchDate 比赛日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param courtId   使用场地ID
     * @param status    状态
     */
    public Match(int id, String name, String matchType, String matchDate,
                 String startTime, String endTime, int courtId, String status) {
        this.id = id;
        this.name = name;
        this.matchType = matchType;
        this.matchDate = matchDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courtId = courtId;
        this.status = status;
    }

    /**
     * 不含ID的构造（用于新增，ID由数据库自增）
     *
     * @param name      比赛名称
     * @param matchType 比赛类型
     * @param matchDate 比赛日期
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param courtId   使用场地ID
     */
    public Match(String name, String matchType, String matchDate,
                 String startTime, String endTime, int courtId) {
        this.name = name;
        this.matchType = matchType;
        this.matchDate = matchDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.courtId = courtId;
        this.status = "待开始";
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }

    public String getMatchDate() { return matchDate; }
    public void setMatchDate(String matchDate) { this.matchDate = matchDate; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public int getCourtId() { return courtId; }
    public void setCourtId(int courtId) { this.courtId = courtId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("比赛[ID=%d, 名称=%s, 类型=%s, 日期=%s, 时间=%s-%s, 场地ID=%d, 状态=%s]",
                id, name, matchType, matchDate, startTime, endTime, courtId, status);
    }
}
