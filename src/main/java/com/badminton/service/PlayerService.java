package com.badminton.service;

import com.badminton.dao.PlayerDao;
import com.badminton.model.Player;

import java.util.List;

/**
 * 选手业务逻辑类 — 选手的增删改查与筛选
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class PlayerService {

    private final PlayerDao playerDao = new PlayerDao();

    /**
     * 获取所有选手
     *
     * @return 选手列表
     */
    public List<Player> listAll() {
        return playerDao.findAll();
    }

    /**
     * 根据ID获取选手
     *
     * @param id 选手ID
     * @return 选手对象，未找到返回null
     */
    public Player getById(int id) {
        return playerDao.findById(id);
    }

    /**
     * 新增选手
     *
     * @param player 选手对象
     * @return 操作结果描述
     */
    public String addPlayer(Player player) {
        if (player.getName() == null || player.getName().trim().isEmpty()) {
            return "选手姓名不能为空！";
        }
        if (player.getGender() == null || (!player.getGender().equals("男") && !player.getGender().equals("女"))) {
            return "性别必须为'男'或'女'！";
        }
        String[] validLevels = {"初级", "中级", "高级", "专业"};
        boolean valid = false;
        for (String lv : validLevels) {
            if (lv.equals(player.getLevel())) { valid = true; break; }
        }
        if (!valid) {
            return "级别必须为：初级、中级、高级、专业！";
        }
        // 电话号码校验：可为空，非空时须为1开头的11位数字
        String phone = player.getPhone();
        if (phone != null && !phone.isEmpty()) {
            if (!phone.matches("1\\d{10}")) {
                return "电话号码须以1开头的11位数字！";
            }
        }
        int id = playerDao.insert(player);
        return id > 0 ? "选手添加成功！ID=" + id : "添加失败！";
    }

    /**
     * 更新选手信息
     *
     * @param player 选手对象（含ID）
     * @return 操作结果描述
     */
    public String updatePlayer(Player player) {
        if (playerDao.findById(player.getId()) == null) {
            return "选手不存在！";
        }
        boolean ok = playerDao.update(player);
        return ok ? "选手信息更新成功！" : "更新失败！";
    }

    /**
     * 删除选手
     *
     * @param id 选手ID
     * @return 操作结果描述
     */
    public String deletePlayer(int id) {
        Player player = playerDao.findById(id);
        if (player == null) {
            return "选手不存在！";
        }
        boolean ok = playerDao.delete(id);
        return ok ? "选手[" + player.getName() + "]已删除（含相关参赛和预定记录）。" : "删除失败！";
    }

    /**
     * 按级别筛选选手
     *
     * @param level 打球级别
     * @return 符合级别的选手列表
     */
    public List<Player> listByLevel(String level) {
        return playerDao.findByLevel(level);
    }

    /**
     * 按性别筛选选手
     *
     * @param gender 性别
     * @return 符合性别的选手列表
     */
    public List<Player> listByGender(String gender) {
        return playerDao.findByGender(gender);
    }
}
