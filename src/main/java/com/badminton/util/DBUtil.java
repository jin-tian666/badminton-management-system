package com.badminton.util;

import java.sql.*;

/**
 * 数据库工具类 — 负责SQLite数据库的连接管理、建表与初始化数据
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class DBUtil {

    /** SQLite 数据库文件路径（项目根目录下的 badminton.db） */
    private static final String DB_URL = "jdbc:sqlite:badminton.db";

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException 连接失败时抛出
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    /**
     * 安全关闭数据库资源
     *
     * @param rs   ResultSet（可为null）
     * @param stmt Statement / PreparedStatement（可为null）
     * @param conn Connection（可为null）
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }

    /**
     * 初始化数据库：建表 + 插入基础数据（场地、选手）
     * 仅在首次运行时执行（表不存在时创建）
     */
    public static void initDatabase() {
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            stmt = conn.createStatement();

            // ======================== 建表 ========================

            /* 场地表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS court (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name VARCHAR(50) NOT NULL," +
                "  area VARCHAR(20) NOT NULL," +
                "  status INTEGER DEFAULT 1" +
                ")"
            );

            /* 选手表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS player (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name VARCHAR(50) NOT NULL," +
                "  gender VARCHAR(4) NOT NULL," +
                "  level VARCHAR(20) NOT NULL," +
                "  phone VARCHAR(20)," +
                "  register_date DATE DEFAULT (date('now'))" +
                ")"
            );

            /* 比赛表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS match_info (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  name VARCHAR(100) NOT NULL," +
                "  match_type VARCHAR(20) NOT NULL," +
                "  match_date DATE NOT NULL," +
                "  start_time TIME NOT NULL," +
                "  end_time TIME NOT NULL," +
                "  court_id INTEGER REFERENCES court(id)," +
                "  status VARCHAR(20) DEFAULT '待开始'" +
                ")"
            );

            /* 比赛-选手关联表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS match_player (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  match_id INTEGER REFERENCES match_info(id)," +
                "  player_id INTEGER REFERENCES player(id)," +
                "  score INTEGER DEFAULT 0," +
                "  rank INTEGER" +
                ")"
            );

            /* 场地预定表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS booking (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  court_id INTEGER REFERENCES court(id)," +
                "  player_id INTEGER REFERENCES player(id)," +
                "  book_date DATE NOT NULL," +
                "  start_time TIME NOT NULL," +
                "  end_time TIME NOT NULL," +
                "  purpose VARCHAR(200)" +
                ")"
            );

            /* 纪录表 */
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS record (" +
                "  id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "  record_type VARCHAR(50) NOT NULL," +
                "  record_value VARCHAR(200) NOT NULL," +
                "  player_id INTEGER REFERENCES player(id)," +
                "  match_id INTEGER REFERENCES match_info(id)," +
                "  record_date DATE DEFAULT (date('now'))," +
                "  is_broken INTEGER DEFAULT 0" +
                ")"
            );

            // ======================== 初始化场地数据 ========================
            // 检查是否已初始化（避免重复插入）
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM court");
            if (rs.next() && rs.getInt(1) == 0) {
                // 东面2片 + 南面1片 = 东南区3片；西面3片 + 北面3片 = 西北区6片
                String[][] courts = {
                    {"东1号", "东面"}, {"东2号", "东面"},
                    {"南1号", "南面"},
                    {"西1号", "西面"}, {"西2号", "西面"}, {"西3号", "西面"},
                    {"北1号", "北面"}, {"北2号", "北面"}, {"北3号", "北面"}
                };
                for (String[] c : courts) {
                    stmt.executeUpdate(
                        "INSERT INTO court (name, area) VALUES ('" + c[0] + "', '" + c[1] + "')"
                    );
                }
                System.out.println("[系统] 已初始化9片场地数据。");
            }
            rs.close();

            // ======================== 初始化选手数据（50名） ========================
            rs = stmt.executeQuery("SELECT COUNT(*) FROM player");
            if (rs.next() && rs.getInt(1) == 0) {
                initPlayers(stmt);
                System.out.println("[系统] 已初始化50名选手数据。");
            }
            rs.close();

            System.out.println("[系统] 数据库初始化完成。");
        } catch (SQLException e) {
            System.err.println("[错误] 数据库初始化失败：" + e.getMessage());
        } finally {
            close(null, stmt, conn);
        }
    }

    /**
     * 插入50名预置选手数据
     *
     * @param stmt Statement对象
     * @throws SQLException 插入失败时抛出
     */
    private static void initPlayers(Statement stmt) throws SQLException {
        String[][] players = {
            {"张伟", "男", "高级"}, {"李娜", "女", "专业"}, {"王强", "男", "中级"},
            {"赵敏", "女", "高级"}, {"刘洋", "男", "初级"}, {"陈静", "女", "中级"},
            {"杨帆", "男", "高级"}, {"周颖", "女", "专业"}, {"吴昊", "男", "中级"},
            {"郑爽", "女", "初级"}, {"钱进", "男", "专业"}, {"孙雨", "女", "高级"},
            {"马超", "男", "中级"}, {"朱红", "女", "高级"}, {"胡歌", "男", "初级"},
            {"林黛", "女", "专业"}, {"何冰", "男", "高级"}, {"郭靖", "男", "中级"},
            {"黄蓉", "女", "高级"}, {"梁辰", "男", "初级"}, {"宋雅", "女", "中级"},
            {"唐龙", "男", "专业"}, {"韩梅", "女", "初级"}, {"冯雷", "男", "高级"},
            {"曹操", "男", "中级"}, {"刘备", "男", "高级"}, {"关羽", "男", "专业"},
            {"张飞", "男", "初级"}, {"赵云", "男", "高级"}, {"诸葛", "男", "专业"},
            {"吕布", "男", "专业"}, {"貂蝉", "女", "高级"}, {"甄姬", "女", "中级"},
            {"大乔", "女", "初级"}, {"小乔", "女", "中级"}, {"孙策", "男", "高级"},
            {"周瑜", "男", "专业"}, {"鲁肃", "男", "中级"}, {"吕蒙", "男", "初级"},
            {"陆逊", "男", "高级"}, {"甘宁", "男", "中级"}, {"黄盖", "男", "初级"},
            {"许褚", "男", "高级"}, {"典韦", "男", "专业"}, {"张辽", "男", "中级"},
            {"徐晃", "男", "初级"}, {"夏侯", "男", "高级"}, {"司马", "男", "专业"},
            {"邓艾", "男", "中级"}, {"姜维", "男", "高级"}
        };
        for (String[] p : players) {
            stmt.executeUpdate(
                "INSERT INTO player (name, gender, level) VALUES ('"
                + p[0] + "', '" + p[1] + "', '" + p[2] + "')"
            );
        }
    }
}
