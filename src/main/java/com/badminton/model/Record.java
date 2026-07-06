package com.badminton.model;

/**
 * 纪录实体类 — 对应数据库 record 表
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class Record {

    private int id;           // 纪录ID（主键，自增）
    private String recordType;// 纪录类型（最高得分/最快获胜/最长连胜等）
    private String recordValue;// 纪录值
    private int playerId;     // 创造者ID
    private int matchId;      // 所在比赛ID
    private String recordDate;// 创造日期
    private int isBroken;     // 是否已被打破（0=当前纪录，1=已破）

    /* 扩展字段（非数据库字段，用于展示） */
    private String playerName; // 选手姓名

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public Record() {}

    /**
     * 全参构造
     *
     * @param id         纪录ID
     * @param recordType 纪录类型
     * @param recordValue 纪录值
     * @param playerId   创造者ID
     * @param matchId    所在比赛ID
     * @param recordDate 创造日期
     * @param isBroken   是否已被打破
     */
    public Record(int id, String recordType, String recordValue, int playerId,
                  int matchId, String recordDate, int isBroken) {
        this.id = id;
        this.recordType = recordType;
        this.recordValue = recordValue;
        this.playerId = playerId;
        this.matchId = matchId;
        this.recordDate = recordDate;
        this.isBroken = isBroken;
    }

    /**
     * 不含ID的构造（用于新增）
     *
     * @param recordType  纪录类型
     * @param recordValue 纪录值
     * @param playerId    创造者ID
     * @param matchId     所在比赛ID
     */
    public Record(String recordType, String recordValue, int playerId, int matchId) {
        this.recordType = recordType;
        this.recordValue = recordValue;
        this.playerId = playerId;
        this.matchId = matchId;
        this.isBroken = 0;
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getRecordValue() { return recordValue; }
    public void setRecordValue(String recordValue) { this.recordValue = recordValue; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }

    public int getIsBroken() { return isBroken; }
    public void setIsBroken(int isBroken) { this.isBroken = isBroken; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    /**
     * 获取纪录状态的中文描述
     *
     * @return "当前纪录" 或 "已被打破"
     */
    public String getStatusText() {
        return isBroken == 0 ? "当前纪录" : "已被打破";
    }

    @Override
    public String toString() {
        return String.format("纪录[ID=%d, 类型=%s, 值=%s, 创造者=%s, 日期=%s, 状态=%s]",
                id, recordType, recordValue,
                playerName == null ? "选手" + playerId : playerName,
                recordDate, getStatusText());
    }
}
