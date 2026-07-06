package com.badminton.ui;

import com.badminton.util.DBUtil;
import com.badminton.util.InputUtil;

/**
 * 主菜单 — 程序入口，提供各子模块的导航
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class MainMenu {

    private final CourtMenu courtMenu = new CourtMenu();
    private final PlayerMenu playerMenu = new PlayerMenu();
    private final MatchMenu matchMenu = new MatchMenu();
    private final BookingMenu bookingMenu = new BookingMenu();
    private final StatisticsMenu statisticsMenu = new StatisticsMenu();

    /**
     * 程序入口
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  大黑山羽毛球运动协会管理系统  v1.0");
        System.out.println("========================================");

        // 初始化数据库（建表 + 基础数据）
        DBUtil.initDatabase();

        // 启动主菜单
        new MainMenu().run();
    }

    /**
     * 运行主菜单循环
     */
    public void run() {
        while (true) {
            printBanner();
            int choice = InputUtil.readInt("请选择：");

            switch (choice) {
                case 1:
                    courtMenu.show();
                    break;
                case 2:
                    playerMenu.show();
                    break;
                case 3:
                    matchMenu.show();
                    break;
                case 4:
                    bookingMenu.show();
                    break;
                case 5:
                    statisticsMenu.show();
                    break;
                case 0:
                    System.out.println("\n感谢使用，再见！");
                    return;
                default:
                    System.out.println("[错误] 无效选项，请重新选择！");
            }
        }
    }

    /**
     * 打印主菜单界面
     */
    private void printBanner() {
        System.out.println("\n========== 主菜单 ==========");
        System.out.println("  1. 场地管理");
        System.out.println("  2. 选手管理");
        System.out.println("  3. 比赛管理");
        System.out.println("  4. 场地预定");
        System.out.println("  5. 统计查询");
        System.out.println("  0. 退出系统");
        System.out.println("============================");
    }
}
