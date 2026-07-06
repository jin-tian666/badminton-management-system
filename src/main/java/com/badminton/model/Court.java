package com.badminton.model;

/**
 * 场地实体类 — 对应数据库 court 表
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class Court {

    private int id;           // 场地ID（主键，自增）
    private String name;      // 场地名称（如"东1号"）
    private String area;      // 所属区域（东面/南面/西面/北面）
    private int status;       // 状态：1=可用，0=维护中

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public Court() {}

    /**
     * 全参构造
     *
     * @param id     场地ID
     * @param name   场地名称
     * @param area   所属区域
     * @param status 状态（1可用 / 0维护）
     */
    public Court(int id, String name, String area, int status) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.status = status;
    }

    /**
     * 不含ID的构造（用于新增，ID由数据库自增）
     *
     * @param name   场地名称
     * @param area   所属区域
     * @param status 状态
     */
    public Court(String name, String area, int status) {
        this.name = name;
        this.area = area;
        this.status = status;
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    /**
     * 获取状态的中文描述
     *
     * @return "可用" 或 "维护中"
     */
    public String getStatusText() {
        return status == 1 ? "可用" : "维护中";
    }

    @Override
    public String toString() {
        return String.format("场地[ID=%d, 名称=%s, 区域=%s, 状态=%s]",
                id, name, area, getStatusText());
    }
}
