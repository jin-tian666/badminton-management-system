package com.badminton.ui;

import com.badminton.model.Match;
import com.badminton.model.MatchPlayer;
import com.badminton.service.CourtService;
import com.badminton.service.MatchService;
import com.badminton.util.InputUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 比赛管理菜单 — 比赛创建、赛程安排、选手报名、成绩录入
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MatchMenu {

    private final MatchService matchService = new MatchService();
    private final CourtService courtService = new CourtService();

    /**
     * 显示比赛管理子菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n========== 比赛管理 ==========");
            System.out.println("  1. 查看所有比赛");
            System.out.println("  2. 创建新比赛");
            System.out.println("  3. 选手报名参赛");
            System.out.println("  4. 查看比赛参赛选手");
            System.out.println("  5. 录入比赛成绩");
            System.out.println("  6. 取消比赛");
            System.out.println("  7. 按状态查看比赛");
            System.out.println("  0. 返回主菜单");
            System.out.println("==============================");

            int choice = InputUtil.readInt("请选择：");
            switch (choice) {
                case 1: listAll(); break;
                case 2: createMatch(); break;
                case 3: signUpPlayer(); break;
                case 4: viewPlayers(); break;
                case 5: recordResult(); break;
                case 6: cancelMatch(); break;
                case 7: listByStatus(); break;
                case 0: return;
                default: System.out.println("[错误] 无效选项！");
            }
        }
    }

    /**
     * 查看所有比赛
     */
    private void listAll() {
        List<Match> matches = matchService.listAll();
        printMatchList(matches, "全部比赛");
    }

    /**
     * 按状态查看比赛
     */
    private void listByStatus() {
        System.out.println("\n状态：1.待开始  2.进行中  3.已结束  4.已取消");
        int s = InputUtil.readInt("请选择状态：");
        String status;
        switch (s) {
            case 1: status = "待开始"; break;
            case 2: status = "进行中"; break;
            case 3: status = "已结束"; break;
            case 4: status = "已取消"; break;
            default:
                System.out.println("[错误] 无效状态！");
                return;
        }
        List<Match> matches = matchService.listByStatus(status);
        printMatchList(matches, status + "比赛");
    }

    /**
     * 创建新比赛
     */
    private void createMatch() {
        System.out.println("\n---------- 创建比赛 ----------");

        // 显示可用场地
        System.out.println("可用场地：");
        courtService.listAvailable().forEach(c ->
            System.out.printf("  ID=%d  %s(%s)\n", c.getId(), c.getName(), c.getArea())
        );

        String name = InputUtil.readString("比赛名称：");

        System.out.println("比赛类型：1.男单  2.女单  3.男双  4.女双  5.混双");
        int typeChoice = InputUtil.readInt("请选择类型：");
        String[] types = {"男单", "女单", "男双", "女双", "混双"};
        if (typeChoice < 1 || typeChoice > 5) {
            System.out.println("[错误] 无效类型！");
            return;
        }
        String matchType = types[typeChoice - 1];

        String date = InputUtil.readDate("比赛日期(YYYY-MM-DD)：");
        String startTime = InputUtil.readTime("开始时间(HH:MM)：");
        String endTime = InputUtil.readTime("结束时间(HH:MM)：");
        int courtId = InputUtil.readInt("场地ID：");

        Match match = new Match(name, matchType, date, startTime, endTime, courtId);
        String result = matchService.createMatch(match);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 选手报名（单打1人，双打2人一对）
     */
    private void signUpPlayer() {
        System.out.println("\n---------- 选手报名 ----------");
        int matchId = InputUtil.readInt("比赛ID：");

        Match match = matchService.getById(matchId);
        if (match == null) {
            System.out.println("比赛不存在！");
            InputUtil.pressEnter();
            return;
        }
        System.out.println("比赛：" + match.getName() + " (" + match.getMatchDate() + " " + match.getStartTime() + "-" + match.getEndTime() + ")");
        System.out.println("类型：" + match.getMatchType());

        boolean isSingles = match.getMatchType().contains("单");
        boolean isDoubles = match.getMatchType().contains("双");
        int currentCount = matchService.getPlayerCount(matchId);
        System.out.println("当前已报名人数：" + currentCount);

        List<Integer> playerIds;
        if (isSingles) {
            int playerId = InputUtil.readInt("选手ID：");
            playerIds = Collections.singletonList(playerId);
        } else if (isDoubles) {
            int playerId1 = InputUtil.readInt("搭档选手1 ID：");
            int playerId2 = InputUtil.readInt("搭档选手2 ID：");
            playerIds = Arrays.asList(playerId1, playerId2);
        } else {
            int playerId = InputUtil.readInt("选手ID：");
            playerIds = Collections.singletonList(playerId);
        }

        String result = matchService.addPlayersToMatch(matchId, playerIds);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 查看比赛参赛选手
     */
    private void viewPlayers() {
        System.out.println("\n---------- 比赛选手 ----------");
        int matchId = InputUtil.readInt("比赛ID：");

        Match match = matchService.getById(matchId);
        if (match == null) {
            System.out.println("比赛不存在！");
            InputUtil.pressEnter();
            return;
        }
        System.out.println("比赛：" + match.getName() + " | 状态：" + match.getStatus());

        List<MatchPlayer> list = matchService.getMatchPlayers(matchId);
        if (list.isEmpty()) {
            System.out.println("暂无选手报名。");
        } else {
            System.out.printf("%-4s  %-6s  %-6s  %-6s\n", "序号", "姓名", "得分", "名次");
            System.out.println("----------------------------------");
            int i = 1;
            for (MatchPlayer mp : list) {
                System.out.printf("%-4d  %-6s  %-6d  %-6s\n",
                    i++, mp.getPlayerName(), mp.getScore(), mp.getRankText());
            }
        }
        InputUtil.pressEnter();
    }

    /**
     * 录入成绩
     */
    private void recordResult() {
        System.out.println("\n---------- 录入成绩 ----------");
        int matchId = InputUtil.readInt("比赛ID：");

        Match match = matchService.getById(matchId);
        if (match == null) {
            System.out.println("比赛不存在！");
            InputUtil.pressEnter();
            return;
        }

        System.out.println("当前参赛选手：");
        List<MatchPlayer> participants = matchService.getMatchPlayers(matchId);
        if (participants.isEmpty()) {
            System.out.println("暂无选手报名，无法录入成绩。");
            InputUtil.pressEnter();
            return;
        }
        for (MatchPlayer mp : participants) {
            System.out.printf("  选手ID=%d  %s  当前得分=%d  当前名次=%s\n",
                mp.getPlayerId(), mp.getPlayerName(), mp.getScore(), mp.getRankText());
        }

        int playerId = InputUtil.readInt("选手ID：");
        int score = InputUtil.readInt("得分：");
        int rank = InputUtil.readInt("名次（1=冠军，2=亚军，3=季军...）：");

        String result = matchService.recordResult(matchId, playerId, score, rank);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 取消比赛
     */
    private void cancelMatch() {
        System.out.println("\n---------- 取消比赛 ----------");
        int matchId = InputUtil.readInt("比赛ID：");
        String result = matchService.cancelMatch(matchId);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 打印比赛列表（通用方法）
     *
     * @param matches 比赛列表
     * @param title   标题
     */
    private void printMatchList(List<Match> matches, String title) {
        System.out.println("\n---------- " + title + " ----------");
        if (matches.isEmpty()) {
            System.out.println("暂无比赛。");
            InputUtil.pressEnter();
            return;
        }
        System.out.printf("%-4s  %-12s  %-6s  %-12s  %-12s  %-4s  %-8s\n",
            "ID", "名称", "类型", "日期", "时间", "场地", "状态");
        System.out.println("------------------------------------------------------------------");
        for (Match m : matches) {
            String courtName = "";
            com.badminton.model.Court c = courtService.getById(m.getCourtId());
            if (c != null) courtName = c.getName();
            System.out.printf("%-4d  %-12s  %-6s  %-12s  %-12s  %-4s  %-8s\n",
                m.getId(), m.getName(), m.getMatchType(), m.getMatchDate(),
                m.getStartTime() + "-" + m.getEndTime(), courtName, m.getStatus());
        }
        System.out.println("------------------------------------------------------------------");
        System.out.println("共 " + matches.size() + " 场比赛。");
        InputUtil.pressEnter();
    }
}
