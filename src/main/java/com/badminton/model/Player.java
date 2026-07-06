package com.badminton.model;

/**
 * 选手实体类 — 对应数据库 player 表
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class Player {

    private int id;              // 选手ID（主键，自增）
    private String name;         // 姓名
    private String gender;       // 性别（男/女）
    private String level;        // 打球级别（初级/中级/高级/专业）
    private String phone;        // 联系电话
    private String registerDate; // 注册日期

    /* ========== 构造方法 ========== */

    /** 无参构造 */
    public Player() {}

    /**
     * 全参构造
     *
     * @param id           选手ID
     * @param name         姓名
     * @param gender       性别
     * @param level        打球级别
     * @param phone        联系电话
     * @param registerDate 注册日期
     */
    public Player(int id, String name, String gender, String level, String phone, String registerDate) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.level = level;
        this.phone = phone;
        this.registerDate = registerDate;
    }

    /**
     * 不含ID的构造（用于新增，ID由数据库自增）
     *
     * @param name   姓名
     * @param gender 性别
     * @param level  打球级别
     * @param phone  联系电话
     */
    public Player(String name, String gender, String level, String phone) {
        this.name = name;
        this.gender = gender;
        this.level = level;
        this.phone = phone;
    }

    /* ========== Getter / Setter ========== */

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getRegisterDate() { return registerDate; }
    public void setRegisterDate(String registerDate) { this.registerDate = registerDate; }

    @Override
    public String toString() {
        return String.format("选手[ID=%d, 姓名=%s, 性别=%s, 级别=%s, 电话=%s, 注册日期=%s]",
                id, name, gender, level, phone == null ? "无" : phone, registerDate);
    }
}
