package com.badminton.service;

import com.badminton.dao.CourtDao;
import com.badminton.model.Court;

import java.util.List;

/**
 * 场地业务逻辑类 — 场地查询与状态管理
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class CourtService {

    private final CourtDao courtDao = new CourtDao();

    /**
     * 获取所有场地列表
     *
     * @return 场地列表
     */
    public List<Court> listAll() {
        return courtDao.findAll();
    }

    /**
     * 获取所有可用场地
     *
     * @return 可用场地列表
     */
    public List<Court> listAvailable() {
        return courtDao.findAvailable();
    }

    /**
     * 按区域获取场地
     *
     * @param area 区域名称（东面/南面/西面/北面）
     * @return 该区域的场地列表
     */
    public List<Court> listByArea(String area) {
        return courtDao.findByArea(area);
    }

    /**
     * 根据ID获取场地
     *
     * @param id 场地ID
     * @return 场地对象，未找到返回null
     */
    public Court getById(int id) {
        return courtDao.findById(id);
    }

    /**
     * 设置场地为维护状态
     *
     * @param courtId 场地ID
     * @return 操作结果描述
     */
    public String setMaintenance(int courtId) {
        Court court = courtDao.findById(courtId);
        if (court == null) {
            return "场地不存在！";
        }
        if (court.getStatus() == 0) {
            return "场地已在维护中！";
        }
        boolean ok = courtDao.updateStatus(courtId, 0);
        return ok ? "场地[" + court.getName() + "]已设为维护状态。" : "操作失败！";
    }

    /**
     * 恢复场地为可用状态
     *
     * @param courtId 场地ID
     * @return 操作结果描述
     */
    public String setAvailable(int courtId) {
        Court court = courtDao.findById(courtId);
        if (court == null) {
            return "场地不存在！";
        }
        if (court.getStatus() == 1) {
            return "场地已是可用状态！";
        }
        boolean ok = courtDao.updateStatus(courtId, 1);
        return ok ? "场地[" + court.getName() + "]已恢复为可用。" : "操作失败！";
    }

    /**
     * 按区域统计场地数量
     *
     * @param area 区域名称
     * @return 该区域场地数量
     */
    public int countByArea(String area) {
        return courtDao.findByArea(area).size();
    }
}
