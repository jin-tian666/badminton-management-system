package com.badminton.ui;

import com.badminton.model.Player;
import com.badminton.service.PlayerService;
import com.badminton.util.InputUtil;

import java.util.List;

/**
 * 选手管理菜单 — 选手的增删改查
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class PlayerMenu {

    private final PlayerService playerService = new PlayerService();

    /**
     * 显示选手管理子菜单
     */
    public void show() {
        while (true) {
            System.out.println("\n========== 选手管理 ==========");
            System.out.println("  1. 查看所有选手");
            System.out.println("  2. 按级别查看选手");
            System.out.println("  3. 按性别查看选手");
            System.out.println("  4. 添加选手");
            System.out.println("  5. 修改选手信息");
            System.out.println("  6. 删除选手");
            System.out.println("  0. 返回主菜单");
            System.out.println("==============================");

            int choice = InputUtil.readInt("请选择：");
            switch (choice) {
                case 1: listAll(); break;
                case 2: listByLevel(); break;
                case 3: listByGender(); break;
                case 4: addPlayer(); break;
                case 5: updatePlayer(); break;
                case 6: deletePlayer(); break;
                case 0: return;
                default: System.out.println("[错误] 无效选项！");
            }
        }
    }

    /**
     * 查看所有选手
     */
    private void listAll() {
        List<Player> players = playerService.listAll();
        printPlayerList(players, "全部选手");
    }

    /**
     * 按级别查看选手
     */
    private void listByLevel() {
        System.out.println("\n级别：1.初级  2.中级  3.高级  4.专业");
        int lv = InputUtil.readInt("请选择级别：");
        String level;
        switch (lv) {
            case 1: level = "初级"; break;
            case 2: level = "中级"; break;
            case 3: level = "高级"; break;
            case 4: level = "专业"; break;
            default:
                System.out.println("[错误] 无效级别！");
                return;
        }
        List<Player> players = playerService.listByLevel(level);
        printPlayerList(players, level + "选手");
    }

    /**
     * 按性别查看选手
     */
    private void listByGender() {
        System.out.println("\n性别：1.男  2.女");
        int g = InputUtil.readInt("请选择性别：");
        String gender;
        switch (g) {
            case 1: gender = "男"; break;
            case 2: gender = "女"; break;
            default:
                System.out.println("[错误] 无效性别！");
                return;
        }
        List<Player> players = playerService.listByGender(gender);
        printPlayerList(players, gender + "选手");
    }

    /**
     * 添加选手
     */
    private void addPlayer() {
        System.out.println("\n---------- 添加选手 ----------");
        String name = InputUtil.readString("姓名：");

        // 性别 — 数字选择避免编码问题
        String gender;
        while (true) {
            int g = InputUtil.readInt("性别（1.男  2.女）：");
            if (g == 1) { gender = "男"; break; }
            if (g == 2) { gender = "女"; break; }
            System.out.println("[错误] 请输入1（男）或2（女）！");
        }

        // 级别 — 数字选择避免编码问题
        String level;
        while (true) {
            System.out.println("级别：1.初级  2.中级  3.高级  4.专业");
            int lv = InputUtil.readInt("请选择级别：");
            if (lv == 1) { level = "初级"; break; }
            if (lv == 2) { level = "中级"; break; }
            if (lv == 3) { level = "高级"; break; }
            if (lv == 4) { level = "专业"; break; }
            System.out.println("[错误] 请输入1-4之间的数字！");
        }

        // 电话号码（可为空，非空时须为1开头的11位数字）
        String phone;
        while (true) {
            phone = InputUtil.readString("联系电话（可选，1开头的11位数字）：");
            if (phone.isEmpty()) {
                break;
            }
            if (phone.matches("1\\d{10}")) {
                break;
            }
            System.out.println("[错误] 电话号码须以1开头的11位数字，或留空！");
        }

        System.out.printf("确认添加 — 姓名：%s  性别：%s  级别：%s  电话：%s\n",
                name, gender, level, phone.isEmpty() ? "无" : phone);
        String confirm = InputUtil.readString("确认？(y/n)：");
        if (!"y".equalsIgnoreCase(confirm) && !"yes".equalsIgnoreCase(confirm)) {
            System.out.println("已取消。");
            InputUtil.pressEnter();
            return;
        }

        Player player = new Player(name, gender, level, phone);
        String result = playerService.addPlayer(player);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 修改选手信息
     */
    private void updatePlayer() {
        System.out.println("\n---------- 修改选手 ----------");
        int id = InputUtil.readInt("请输入要修改的选手ID：");
        Player old = playerService.getById(id);
        if (old == null) {
            System.out.println("选手不存在！");
            InputUtil.pressEnter();
            return;
        }
        System.out.println("当前信息：" + old);
        System.out.println("（留空/输入0表示不修改该项）");

        String name = InputUtil.readString("新姓名[" + old.getName() + "]：");

        // 性别 — 数字选择
        String gender = null;
        while (true) {
            String prompt = String.format("新性别（1.男  2.女  0.不修改）[当前=%s]：", old.getGender());
            int g = InputUtil.readInt(prompt);
            if (g == 0) break;
            if (g == 1) { gender = "男"; break; }
            if (g == 2) { gender = "女"; break; }
            System.out.println("[错误] 请输入1（男）、2（女）或0（跳过）！");
        }

        // 级别 — 数字选择
        String level = null;
        while (true) {
            String prompt = String.format("新级别（1.初级 2.中级 3.高级 4.专业  0.不修改）[当前=%s]：", old.getLevel());
            int lv = InputUtil.readInt(prompt);
            if (lv == 0) break;
            if (lv == 1) { level = "初级"; break; }
            if (lv == 2) { level = "中级"; break; }
            if (lv == 3) { level = "高级"; break; }
            if (lv == 4) { level = "专业"; break; }
            System.out.println("[错误] 请输入1-4之间的数字，或0跳过！");
        }

        // 电话
        String phone = null;
        while (true) {
            String oldPhone = old.getPhone() != null ? old.getPhone() : "无";
            phone = InputUtil.readString("新电话（1开头的11位数字，留空不修改）[" + oldPhone + "]：");
            if (phone.isEmpty()) {
                phone = null;
                break;
            }
            if (phone.matches("1\\d{10}")) {
                break;
            }
            System.out.println("[错误] 电话号码须以1开头的11位数字，或留空！");
        }

        if (!name.isEmpty()) old.setName(name);
        if (gender != null) old.setGender(gender);
        if (level != null) old.setLevel(level);
        if (phone != null) old.setPhone(phone);

        String result = playerService.updatePlayer(old);
        System.out.println(result);
        InputUtil.pressEnter();
    }

    /**
     * 删除选手
     */
    private void deletePlayer() {
        System.out.println("\n---------- 删除选手 ----------");
        int id = InputUtil.readInt("请输入要删除的选手ID：");
        System.out.print("确认删除？(y/n)：");
        String confirm = InputUtil.readString("");
        if ("y".equalsIgnoreCase(confirm) || "yes".equalsIgnoreCase(confirm)) {
            String result = playerService.deletePlayer(id);
            System.out.println(result);
        } else {
            System.out.println("已取消。");
        }
        InputUtil.pressEnter();
    }

    /**
     * 打印选手列表（通用方法）
     *
     * @param players 选手列表
     * @param title   标题
     */
    private void printPlayerList(List<Player> players, String title) {
        System.out.println("\n---------- " + title + " ----------");
        if (players.isEmpty()) {
            System.out.println("暂无选手。");
            InputUtil.pressEnter();
            return;
        }
        System.out.printf("%-4s  %-6s  %-4s  %-6s  %-12s  %-12s\n",
            "ID", "姓名", "性别", "级别", "电话", "注册日期");
        System.out.println("------------------------------------------------------------");
        for (Player p : players) {
            System.out.printf("%-4d  %-6s  %-4s  %-6s  %-12s  %-12s\n",
                p.getId(), p.getName(), p.getGender(), p.getLevel(),
                p.getPhone() != null ? p.getPhone() : "-",
                p.getRegisterDate());
        }
        System.out.println("------------------------------------------------------------");
        System.out.println("共 " + players.size() + " 名选手。");
        InputUtil.pressEnter();
    }
}
