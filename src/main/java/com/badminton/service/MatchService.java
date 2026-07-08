package com.badminton.service;

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

import java.util.ArrayList;
import java.util.List;

/**
 * 比赛业务逻辑类 — 比赛的创建、安排、选手报名、成绩录入
 * 核心规则：
 * 1. 同一选手不能在同一时间段参加两个比赛
 * 2. 同一场地同一时间段不能有两场比赛
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MatchService {

    private final MatchDao matchDao = new MatchDao();
    private final MatchPlayerDao matchPlayerDao = new MatchPlayerDao();
    private final CourtDao courtDao = new CourtDao();
    private final PlayerDao playerDao = new PlayerDao();

    /**
     * 创建新比赛（含冲突检测）
     *
     * @param match 比赛对象
     * @return 操作结果描述
     */
    public String createMatch(Match match) {
        // 1. 基本校验
        if (match.getName() == null || match.getName().trim().isEmpty()) {
            return "比赛名称不能为空！";
        }
        if (match.getMatchDate() == null || match.getMatchDate().trim().isEmpty()) {
            return "比赛日期不能为空！";
        }
        if (match.getStartTime().compareTo(match.getEndTime()) >= 0) {
            return "开始时间必须早于结束时间！";
        }

        // 1.5 日期范围校验（不得早于2000-01-01，不得晚于2030-01-01）
        if (match.getMatchDate().compareTo("2000-01-01") < 0
            || match.getMatchDate().compareTo("2030-01-01") > 0) {
            return "比赛日期必须在 2000-01-01 至 2030-01-01 之间！";
        }

        // 2. 检查场地是否存在且可用
        Court court = courtDao.findById(match.getCourtId());
        if (court == null) {
            return "场地不存在！";
        }
        if (court.getStatus() == 0) {
            return "场地[" + court.getName() + "]正在维护中，无法安排比赛！";
        }

        // 3. 场地时间冲突检测
        List<Match> conflicts = matchDao.findConflicting(
            match.getCourtId(), match.getMatchDate(),
            match.getStartTime(), match.getEndTime(), 0
        );
        if (!conflicts.isEmpty()) {
            return "场地冲突！场地[" + court.getName() + "]在" + match.getMatchDate() + " "
                 + match.getStartTime() + "-" + match.getEndTime() + " 已被比赛["
                 + conflicts.get(0).getName() + "]占用。";
        }

        // 4. 创建比赛
        int id = matchDao.insert(match);
        return id > 0 ? "比赛创建成功！ID=" + id + "，场地：" + court.getName() : "创建失败！";
    }

    /**
     * 取消比赛
     *
     * @param matchId 比赛ID
     * @return 操作结果描述
     */
    public String cancelMatch(int matchId) {
        Match match = matchDao.findById(matchId);
        if (match == null) {
            return "比赛不存在！";
        }
        if ("已结束".equals(match.getStatus())) {
            return "已结束的比赛无法取消！";
        }
        boolean ok = matchDao.updateStatus(matchId, "已取消");
        return ok ? "比赛[" + match.getName() + "]已取消。" : "操作失败！";
    }

    /**
     * 选手报名比赛（含选手时间冲突检测、性别校验）
     * 单打每次报1人，双打每次报2人（一对搭档），无总人数上限
     *
     * @param matchId   比赛ID
     * @param playerIds 选手ID列表（单打1个，双打2个）
     * @return 操作结果描述
     */
    public String addPlayersToMatch(int matchId, List<Integer> playerIds) {
        // 1. 检查比赛是否存在
        Match match = matchDao.findById(matchId);
        if (match == null) {
            return "比赛不存在！";
        }
        if (!"待开始".equals(match.getStatus())) {
            return "只有[待开始]状态的比赛才能报名！";
        }

        String matchType = match.getMatchType();

        // 2. 校验单次报名人数：单打1人，双打2人
        boolean isSingles = matchType.contains("单");
        boolean isDoubles = matchType.contains("双");
        if (isSingles && playerIds.size() != 1) {
            return "单打比赛每次只能报名1人！";
        }
        if (isDoubles && playerIds.size() != 2) {
            return "双打比赛每次必须报名2人（一对搭档）！";
        }

        // 3. 逐个校验选手（存在性、性别、重复报名、时间冲突）
        for (int pid : playerIds) {
            Player player = playerDao.findById(pid);
            if (player == null) {
                return "选手ID=" + pid + " 不存在！";
            }

            // 性别匹配校验
            String playerGender = player.getGender();
            if (("男单".equals(matchType) || "男双".equals(matchType)) && !"男".equals(playerGender)) {
                return matchType + "比赛仅限男性选手报名，选手[" + player.getName() + "]性别不符！";
            }
            if (("女单".equals(matchType) || "女双".equals(matchType)) && !"女".equals(playerGender)) {
                return matchType + "比赛仅限女性选手报名，选手[" + player.getName() + "]性别不符！";
            }

            // 检查是否重复报名
            if (matchPlayerDao.isPlayerInMatch(matchId, pid)) {
                return "选手[" + player.getName() + "]已报名该比赛！";
            }

            // 选手时间冲突检测
            List<Match> playerMatches = matchDao.findByPlayerId(pid);
            for (Match m : playerMatches) {
                if (m.getMatchDate().equals(match.getMatchDate())) {
                    if (match.getStartTime().compareTo(m.getEndTime()) < 0
                        && match.getEndTime().compareTo(m.getStartTime()) > 0) {
                        return "选手[" + player.getName() + "]在同一时间段已报名比赛["
                             + m.getName() + "]（" + m.getStartTime() + "-" + m.getEndTime() + "）！";
                    }
                }
            }
        }

        // 4. 全部校验通过，逐个插入
        List<String> names = new ArrayList<>();
        for (int pid : playerIds) {
            matchPlayerDao.insert(matchId, pid);
            Player player = playerDao.findById(pid);
            names.add(player.getName());
        }

        String namesStr = String.join("、", names);
        return "报名成功！选手：" + namesStr;
    }

    /**
     * 录入比赛成绩和名次
     *
     * @param matchId  比赛ID
     * @param playerId 选手ID
     * @param score    得分
     * @param rank     名次
     * @return 操作结果描述（含破纪录信息）
     */
    public String recordResult(int matchId, int playerId, int score, int rank) {
        // 1. 检查参赛记录
        if (!matchPlayerDao.isPlayerInMatch(matchId, playerId)) {
            return "该选手未报名此比赛！";
        }

        // 2. 更新成绩
        boolean ok = matchPlayerDao.updateResult(matchId, playerId, score, rank);
        if (!ok) {
            return "成绩录入失败！";
        }

        // 3. 更新比赛状态为"已结束"
        matchDao.updateStatus(matchId, "已结束");

        // 4. 破纪录检测
        RecordDao recordDao = new RecordDao();
        String recordMsg = checkAndRecord(matchId, playerId, score, recordDao);

        Player player = playerDao.findById(playerId);
        String rankText = rank == 1 ? "冠军" : rank == 2 ? "亚军" : rank == 3 ? "季军" : "第" + rank + "名";
        return "成绩录入成功！" + player.getName() + " 获得" + rankText + "，得分" + score + "。" + recordMsg;
    }

    /**
     * 检测是否打破纪录，如果打破则自动记录
     *
     * @param matchId   比赛ID
     * @param playerId  选手ID
     * @param score     得分
     * @param recordDao 纪录DAO
     * @return 破纪录提示信息
     */
    private String checkAndRecord(int matchId, int playerId, int score, RecordDao recordDao) {
        StringBuilder sb = new StringBuilder();
        String recordType = "最高得分";

        // 查询当前最高得分纪录
        String bestStr = recordDao.findBestByType(recordType);
        int currentBest = 0;
        if (bestStr != null && !bestStr.isEmpty()) {
            try {
                currentBest = Integer.parseInt(bestStr);
            } catch (NumberFormatException ignored) {}
        }

        if (score > currentBest) {
            // 打破纪录：标记旧纪录，插入新纪录
            recordDao.markBroken(recordType);
            Record newRecord = new Record(recordType, String.valueOf(score), playerId, matchId);
            recordDao.insert(newRecord);
            sb.append(" 【新纪录！打破了").append(recordType).append("：").append(score).append("分】");
        }

        return sb.toString();
    }

    /**
     * 获取所有比赛
     *
     * @return 比赛列表
     */
    public List<Match> listAll() {
        return matchDao.findAll();
    }

    /**
     * 根据ID获取比赛
     *
     * @param id 比赛ID
     * @return 比赛对象
     */
    public Match getById(int id) {
        return matchDao.findById(id);
    }

    /**
     * 按状态获取比赛
     *
     * @param status 状态
     * @return 比赛列表
     */
    public List<Match> listByStatus(String status) {
        return matchDao.findByStatus(status);
    }

    /**
     * 按日期获取比赛
     *
     * @param date 日期
     * @return 比赛列表
     */
    public List<Match> listByDate(String date) {
        return matchDao.findByDate(date);
    }

    /**
     * 获取某比赛的参赛选手及成绩
     *
     * @param matchId 比赛ID
     * @return 参赛记录列表
     */
    public List<MatchPlayer> getMatchPlayers(int matchId) {
        return matchPlayerDao.findByMatchId(matchId);
    }

    /**
     * 获取某选手的所有参赛记录
     *
     * @param playerId 选手ID
     * @return 参赛记录列表
     */
    public List<MatchPlayer> getPlayerMatches(int playerId) {
        return matchPlayerDao.findByPlayerId(playerId);
    }

    /**
     * 获取比赛报名人数
     *
     * @param matchId 比赛ID
     * @return 报名人数
     */
    public int getPlayerCount(int matchId) {
        return matchPlayerDao.countByMatchId(matchId);
    }
}
