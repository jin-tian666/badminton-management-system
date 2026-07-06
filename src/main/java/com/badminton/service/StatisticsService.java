package com.badminton.service;

import com.badminton.dao.BookingDao;
import com.badminton.dao.CourtDao;
import com.badminton.dao.MatchDao;
import com.badminton.dao.MatchPlayerDao;
import com.badminton.dao.PlayerDao;
import com.badminton.dao.RecordDao;
import com.badminton.model.Court;
import com.badminton.model.Match;
import com.badminton.model.MatchPlayer;
import com.badminton.model.Player;
import com.badminton.model.Record;

import java.util.List;

/**
 * 统计查询业务类 — 名次统计、成绩统计、破纪录统计、场地使用率
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class StatisticsService {

    private final MatchPlayerDao matchPlayerDao = new MatchPlayerDao();
    private final RecordDao recordDao = new RecordDao();
    private final MatchDao matchDao = new MatchDao();
    private final PlayerDao playerDao = new PlayerDao();
    private final CourtDao courtDao = new CourtDao();

    /**
     * 获取某比赛的名次和成绩统计（按名次排序）
     *
     * @param matchId 比赛ID
     * @return 统计结果字符串
     */
    public String getMatchRankings(int matchId) {
        Match match = matchDao.findById(matchId);
        if (match == null) {
            return "比赛不存在！";
        }

        List<MatchPlayer> list = matchPlayerDao.findByMatchId(matchId);
        if (list.isEmpty()) {
            return "比赛[" + match.getName() + "]暂无参赛选手。";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========== 比赛名次统计 ==========\n");
        sb.append("比赛：").append(match.getName()).append("\n");
        sb.append("类型：").append(match.getMatchType())
          .append("  |  日期：").append(match.getMatchDate())
          .append("  |  状态：").append(match.getStatus()).append("\n");
        sb.append("----------------------------------\n");
        sb.append(String.format("%-6s  %-8s  %-6s  %-6s\n", "名次", "姓名", "得分", "称号"));
        sb.append("----------------------------------\n");

        for (MatchPlayer mp : list) {
            sb.append(String.format("%-6s  %-8s  %-6d  %-6s\n",
                mp.getRankText(),
                mp.getPlayerName() != null ? mp.getPlayerName() : "选手" + mp.getPlayerId(),
                mp.getScore(),
                mp.getRank() <= 3 ? (mp.getRank() == 1 ? "🏆" : mp.getRank() == 2 ? "🥈" : "🥉") : ""
            ));
        }
        sb.append("----------------------------------\n");
        sb.append("共 ").append(list.size()).append(" 名选手参赛。");
        return sb.toString();
    }

    /**
     * 获取某选手的所有参赛成绩统计
     *
     * @param playerId 选手ID
     * @return 统计结果字符串
     */
    public String getPlayerStats(int playerId) {
        Player player = playerDao.findById(playerId);
        if (player == null) {
            return "选手不存在！";
        }

        List<MatchPlayer> records = matchPlayerDao.findByPlayerId(playerId);
        StringBuilder sb = new StringBuilder();
        sb.append("========== 选手成绩统计 ==========\n");
        sb.append("选手：").append(player.getName())
          .append("  |  性别：").append(player.getGender())
          .append("  |  级别：").append(player.getLevel()).append("\n");
        sb.append("----------------------------------\n");

        if (records.isEmpty()) {
            sb.append("暂无参赛记录。\n");
        } else {
            sb.append(String.format("%-4s  %-12s  %-6s  %-6s\n", "序号", "比赛名称", "得分", "名次"));
            sb.append("----------------------------------\n");
            int totalScore = 0;
            int championCount = 0;
            for (int i = 0; i < records.size(); i++) {
                MatchPlayer mp = records.get(i);
                sb.append(String.format("%-4d  %-12s  %-6d  %-6s\n",
                    i + 1,
                    mp.getMatchName() != null ? mp.getMatchName() : "比赛" + mp.getMatchId(),
                    mp.getScore(),
                    mp.getRankText()
                ));
                totalScore += mp.getScore();
                if (mp.getRank() == 1) championCount++;
            }
            sb.append("----------------------------------\n");
            sb.append("总得分：").append(totalScore)
              .append("  |  参赛次数：").append(records.size())
              .append("  |  冠军次数：").append(championCount).append("\n");
            if (!records.isEmpty()) {
                sb.append("平均得分：").append(String.format("%.1f", (double) totalScore / records.size()));
            }
        }
        return sb.toString();
    }

    /**
     * 查看当前有效纪录
     *
     * @return 统计结果字符串
     */
    public String getCurrentRecords() {
        List<Record> records = recordDao.findCurrent();
        StringBuilder sb = new StringBuilder();
        sb.append("========== 当前纪录榜 ==========\n");

        if (records.isEmpty()) {
            sb.append("暂无纪录。\n");
        } else {
            sb.append(String.format("%-12s  %-10s  %-8s  %-12s\n", "纪录类型", "纪录值", "创造者", "创造日期"));
            sb.append("----------------------------------\n");
            for (Record r : records) {
                sb.append(String.format("%-12s  %-10s  %-8s  %-12s\n",
                    r.getRecordType(),
                    r.getRecordValue(),
                    r.getPlayerName() != null ? r.getPlayerName() : "选手" + r.getPlayerId(),
                    r.getRecordDate()
                ));
            }
        }
        return sb.toString();
    }

    /**
     * 查看破纪录历史
     *
     * @return 统计结果字符串
     */
    public String getBrokenRecords() {
        List<Record> records = recordDao.findBroken();
        StringBuilder sb = new StringBuilder();
        sb.append("========== 破纪录历史 ==========\n");

        if (records.isEmpty()) {
            sb.append("暂无历史纪录被打破。\n");
        } else {
            sb.append(String.format("%-12s  %-10s  %-8s  %-12s\n", "纪录类型", "旧纪录值", "原创造者", "创造日期"));
            sb.append("----------------------------------\n");
            for (Record r : records) {
                sb.append(String.format("%-12s  %-10s  %-8s  %-12s\n",
                    r.getRecordType(),
                    r.getRecordValue(),
                    r.getPlayerName() != null ? r.getPlayerName() : "选手" + r.getPlayerId(),
                    r.getRecordDate()
                ));
            }
        }
        return sb.toString();
    }

    /**
     * 统计场地使用情况
     *
     * @return 统计结果字符串
     */
    public String getCourtUsageStats() {
        List<Court> courts = courtDao.findAll();
        List<Match> allMatches = matchDao.findAll();
        List<com.badminton.model.Booking> allBookings = new BookingDao().findAll();

        StringBuilder sb = new StringBuilder();
        sb.append("========== 场地使用统计 ==========\n");
        sb.append("场地总数：").append(courts.size()).append(" 片\n");
        sb.append("区域分布：东面/南面3片，西面/北面6片\n");
        sb.append("----------------------------------\n");

        // 按区域统计
        String[] areas = {"东面", "南面", "西面", "北面"};
        for (String area : areas) {
            List<Court> areaCourts = courtDao.findByArea(area);
            int matchCount = 0;
            for (Court c : areaCourts) {
                for (Match m : allMatches) {
                    if (m.getCourtId() == c.getId()) matchCount++;
                }
            }
            sb.append(String.format("%s：%d 片场地，累计 %d 场比赛\n",
                area, areaCourts.size(), matchCount));
        }

        sb.append("----------------------------------\n");
        sb.append("比赛总数：").append(allMatches.size()).append(" 场\n");
        sb.append("预定总数：").append(allBookings.size()).append(" 次\n");

        // 计算当前可用场地
        int available = 0;
        for (Court c : courts) {
            if (c.getStatus() == 1) available++;
        }
        sb.append("当前可用场地：").append(available).append("/").append(courts.size());

        return sb.toString();
    }
}
