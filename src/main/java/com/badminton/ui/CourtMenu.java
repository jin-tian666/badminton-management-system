package com.badminton.ui;

import com.badminton.model.Court;
import com.badminton.service.CourtService;
import com.badminton.util.InputUtil;

import java.util.List;

/**
 * 场地管理菜单 — 查看场地、维护设置
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class CourtMenu {

    private final CourtService courtService = new CourtService();

    /**
     * 显示场地管理子菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n========== 场地管理 ==========");
            System.out.println("  1. 查看所有场地");
            System.out.println("  2. 按区域查看场地");
            System.out.println("  3. 查看可用场地");
            System.out.println("  4. 设置场地维护");
            System.out.println("  5. 恢复场地可用");
            System.out.println("  0. 返回主菜单");
            System.out.println("==============================");

            int choice = InputUtil.readInt("请选择：");
            switch (choice) {
                case 1: listAll(); break;
                case 2: listByArea(); break;
                case 3: listAvailable(); break;
                case 4: setMaintenance(); break;
                case 5: setAvailable(); break;
                case 0: return;
                default: System.out.println("[错误] 无效选项！");
            }
        }
    }

    /**
     * 查看所有场地
     */
    private void listAll() {
        List<Court> courts = courtService.listAll();
        System.out.println("\n---------- 全部场地 ----------");
        if (courts.isEmpty()) {
            System.out.println("暂无场地数据。");
            return;
        }
        System.out.printf("%-4s  %-8s  %-6s  %-6s\n", "ID", "名称", "区域", "状态");
        System.out.println("--------------------------------");
        for (Court c : courts) {
            System.out.printf("%-4d  %-8s  %-6s  %-6s\n",
                c.getId(), c.getName(), c.getArea(), c.getStatusText());
        }
        System.out.println("--------------------------------");
        System.out.println("共 " + courts.size() + " 片场地。");
        InputUtil.pressEnter();
    }

    /**
     * 按区域查看场地
     */
    private void listByArea() {
        System.out.println("\n区域：1.东面  2.南面  3.西面  4.北面");
        int areaChoice = InputUtil.readInt("请选择区域：");
        String area;
        switch (areaChoice) {
            case 1: area = "东面"; break;
            case 2: area = "南面"; break;
            case 3: area = "西面"; break;
            case 4: area = "北面"; break;
            default:
                System.out.println("[错误] 无效区域！");
                return;
        }
        List<Court> courts = courtService.listByArea(area);
        System.out.println("\n---------- " + area + "场地 ----------");
        if (courts.isEmpty()) {
            System.out.println("该区域暂无场地。");
            return;
        }
        System.out.printf("%-4s  %-8s  %-6s\n", "ID", "名称", "状态");
        System.out.println("----------------------");
        for (Court c : courts) {
            System.out.printf("%-4d  %-8s  %-6s\n", c.getId(), c.getName(), c.getStatusText());
        }
        InputUtil.pressEnter();
    }

    /**
     * 查看可用场地
     */
    private void listAvailable() {
        List<Court> courts = courtService.listAvailable();
        System.out.println("\n---------- 可用场地 ----------");
        if (courts.isEmpty()) {
            System.out.println("当前无可用场地。");
            return;
        }
        System.out.printf("%-4s  %-8s  %-6s\n", "ID", "名称", "区域");
        System.out.println("------------------------------");
        for (Court c : courts) {
            System.out.printf("%-4d  %-8s  %-6s\n", c.getId(), c.getName(), c.getArea());
        }
        System.out.println("------------------------------");
        System.out.println("共 " + courts.size() + " 片可用场地。");
        InputUtil.pressEnter();
    }

    /**
     * 设置场地维护
     */
    private void setMaintenance() {
        int id = InputUtil.readInt("请输入场地ID：");
        String result = courtService.setMaintenance(id);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 恢复场地可用
     */
    private void setAvailable() {
        int id = InputUtil.readInt("请输入场地ID：");
        String result = courtService.setAvailable(id);
        System.out.println(result);
        InputUtil.pressEnter();
    }
}
