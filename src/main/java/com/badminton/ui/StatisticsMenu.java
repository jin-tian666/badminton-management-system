package com.badminton.ui;

import com.badminton.service.StatisticsService;
import com.badminton.util.InputUtil;

/**
 * 统计查询菜单 — 名次统计、成绩统计、破纪录统计、场地使用率
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class StatisticsMenu {

    private final StatisticsService statisticsService = new StatisticsService();

    /**
     * 显示统计查询子菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n========== 统计查询 ==========");
            System.out.println("  1. 比赛名次统计");
            System.out.println("  2. 选手成绩统计");
            System.out.println("  3. 当前纪录榜");
            System.out.println("  4. 破纪录历史");
            System.out.println("  5. 场地使用统计");
            System.out.println("  0. 返回主菜单");
            System.out.println("==============================");

            int choice = InputUtil.readInt("请选择：");
            switch (choice) {
                case 1: matchRankings(); break;
                case 2: playerStats(); break;
                case 3: currentRecords(); break;
                case 4: brokenRecords(); break;
                case 5: courtUsage(); break;
                case 0: return;
                default: System.out.println("[错误] 无效选项！");
            }
        }
    }

    /**
     * 比赛名次统计
     */
    private void matchRankings() {
        System.out.println("\n---------- 比赛名次统计 ----------");
        int matchId = InputUtil.readInt("请输入比赛ID：");
        String result = statisticsService.getMatchRankings(matchId);
        System.out.println("\n" + result);
        InputUtil.pressEnter();
    }

    /**
     * 选手成绩统计
     */
    private void playerStats() {
        System.out.println("\n---------- 选手成绩统计 ----------");
        int playerId = InputUtil.readInt("请输入选手ID：");
        String result = statisticsService.getPlayerStats(playerId);
        System.out.println("\n" + result);
        InputUtil.pressEnter();
    }

    /**
     * 当前纪录榜
     */
    private void currentRecords() {
        String result = statisticsService.getCurrentRecords();
        System.out.println("\n" + result);
        InputUtil.pressEnter();
    }

    /**
     * 破纪录历史
     */
    private void brokenRecords() {
        String result = statisticsService.getBrokenRecords();
        System.out.println("\n" + result);
        InputUtil.pressEnter();
    }

    /**
     * 场地使用统计
     */
    private void courtUsage() {
        String result = statisticsService.getCourtUsageStats();
        System.out.println("\n" + result);
        InputUtil.pressEnter();
    }
}
