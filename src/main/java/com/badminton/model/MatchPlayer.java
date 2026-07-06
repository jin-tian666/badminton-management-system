package com.badminton.model;

/**
 * 比赛-选手关联实体类 — 对应数据库 match_player 表（多对多关系）
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MatchPlayer {

    private int id;        // 关联ID（主键，自增）
    private int matchId;   // 比赛ID
    private int playerId;  // 选手ID
    private int score;     // 得分
    private int rank;      // 最终名次

    /* 扩展字段（非数据库字段，用于展示） */
    private String playerName;  // 选手姓名（JOIN查询时使用）
    private String matchName;   // 比赛名称（JOIN查询时使用）

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public MatchPlayer() {}

    /**
     * 全参构造
     *
     * @param id       关联ID
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @param score    得分
     * @param rank     名次
     */
    public MatchPlayer(int id, int matchId, int playerId, int score, int rank) {
        this.id = id;
        this.matchId = matchId;
        this.playerId = playerId;
        this.score = score;
        this.rank = rank;
    }

    /**
     * 不含ID的构造（用于新增）
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     */
    public MatchPlayer(int matchId, int playerId) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.score = 0;
        this.rank = 0;
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getMatchId() { return matchId; }
    public void setMatchId(int matchId) { this.matchId = matchId; }

    public int getPlayerId() { return playerId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }

    public String getMatchName() { return matchName; }
    public void setMatchName(String matchName) { this.matchName = matchName; }

    /**
     * 获取名次中文描述
     *
     * @return 名次描述（冠军/亚军/季军/第N名）
     */
    public String getRankText() {
        if (rank == 0) return "未排名";
        if (rank == 1) return "冠军";
        if (rank == 2) return "亚军";
        if (rank == 3) return "季军";
        return "第" + rank + "名";
    }

    @Override
    public String toString() {
        return String.format("参赛记录[ID=%d, 比赛ID=%d, 选手ID=%d, 得分=%d, 名次=%s]",
                id, matchId, playerId, score, getRankText());
    }
}
