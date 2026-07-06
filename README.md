# 羽毛球运动协会管理系统 — 设计文档

## 一、项目概述

### 1.1 项目名称
大黑山羽毛球运动协会管理系统

### 1.2 技术选型
| 项目 | 选型 | 说明 |
|------|------|------|
| 开发语言 | Java (JDK 8+) | 控制台程序 |
| 数据库 | SQLite | 轻量级文件数据库，无需安装服务端，适合控制台程序 |
| JDBC | sqlite-jdbc | SQLite 的 JDBC 驱动 |
| 架构模式 | 分层架构 | 表现层 → 业务层 → 数据访问层 → 模型层 |
| 构建工具 | 手动编译 / Maven | 两种方式均可，推荐 Maven 管理依赖 |

### 1.3 项目结构 (包划分)
```
com.badminton
├── model          // 模型层：实体类 (POJO)
│   ├── Court.java
│   ├── Player.java
│   ├── Match.java
│   ├── Booking.java
│   ├── Record.java
│   └── MatchPlayer.java
├── dao            // 数据访问层：数据库 CRUD 操作
│   ├── BaseDao.java
│   ├── CourtDao.java
│   ├── PlayerDao.java
│   ├── MatchDao.java
│   ├── BookingDao.java
│   ├── RecordDao.java
│   └── MatchPlayerDao.java
├── service        // 业务逻辑层：核心业务处理
│   ├── CourtService.java
│   ├── PlayerService.java
│   ├── MatchService.java
│   ├── BookingService.java
│   └── StatisticsService.java
├── ui             // 表现层：控制台菜单与用户交互
│   ├── MainMenu.java
│   ├── CourtMenu.java
│   ├── PlayerMenu.java
│   ├── MatchMenu.java
│   ├── BookingMenu.java
│   └── StatisticsMenu.java
└── util           // 工具类
    ├── DBUtil.java
    └── InputUtil.java
```

---

## 二、数据库设计

### 2.1 数据库选择：SQLite
**选择理由：**
- 零配置，无需安装数据库服务器
- 单文件存储（.db 文件），方便拷贝和提交作业
- 支持标准 SQL，满足本项目所有需求
- JDBC 驱动成熟稳定

### 2.2 数据表设计

#### 表1：court（场地表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 场地ID |
| name | VARCHAR(50) | NOT NULL | 场地名称（如"东1号"） |
| area | VARCHAR(20) | NOT NULL | 所属区域（东面/南面/西面/北面） |
| status | INTEGER | DEFAULT 1 | 状态：1=可用，0=维护中 |

**初始化数据（9片场地）：**
- 东面：东1号、东2号、东3号（共3片，属于"东南区"）
- 南面：南1号（实际也是东南区，与东面合计3片）
- 西面：西1号、西2号、西3号（共3片，属于"西北区"）
- 北面：北1号、北2号、北3号（共3片，属于"西北区"）

> **说明：** 按照需求"东面和南面有3片，西面和北面有6片"，实际分配为：东面2片 + 南面1片 = 东南区3片；西面3片 + 北面3片 = 西北区6片。

#### 表2：player（选手表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 选手ID |
| name | VARCHAR(50) | NOT NULL | 姓名 |
| gender | VARCHAR(4) | NOT NULL | 性别（男/女） |
| level | VARCHAR(20) | NOT NULL | 打球级别（初级/中级/高级/专业） |
| phone | VARCHAR(20) | | 联系电话 |
| register_date | DATE | DEFAULT (date('now')) | 注册日期 |

#### 表3：match（比赛表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 比赛ID |
| name | VARCHAR(100) | NOT NULL | 比赛名称 |
| match_type | VARCHAR(20) | NOT NULL | 类型（男单/女单/男双/女双/混双） |
| match_date | DATE | NOT NULL | 比赛日期 |
| start_time | TIME | NOT NULL | 开始时间 |
| end_time | TIME | NOT NULL | 结束时间 |
| court_id | INTEGER | FOREIGN KEY → court.id | 使用场地 |
| status | VARCHAR(20) | DEFAULT '待开始' | 状态（待开始/进行中/已结束/已取消） |

#### 表4：match_player（比赛-选手关联表，多对多）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 关联ID |
| match_id | INTEGER | FOREIGN KEY → match.id | 比赛ID |
| player_id | INTEGER | FOREIGN KEY → player.id | 选手ID |
| score | INTEGER | DEFAULT 0 | 得分 |
| rank | INTEGER | | 最终名次（1=冠军，2=亚军，3=季军...） |

#### 表5：booking（场地预定表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 预定ID |
| court_id | INTEGER | FOREIGN KEY → court.id | 场地ID |
| player_id | INTEGER | FOREIGN KEY → player.id | 预定人ID |
| book_date | DATE | NOT NULL | 预定日期 |
| start_time | TIME | NOT NULL | 开始时间 |
| end_time | TIME | NOT NULL | 结束时间 |
| purpose | VARCHAR(200) | | 用途说明 |

#### 表6：record（纪录表）
| 字段名 | 类型 | 约束 | 说明 |
|--------|------|------|------|
| id | INTEGER | PRIMARY KEY AUTOINCREMENT | 纪录ID |
| record_type | VARCHAR(50) | NOT NULL | 纪录类型（最高分/最快获胜/最长连胜等） |
| record_value | VARCHAR(200) | NOT NULL | 纪录值 |
| player_id | INTEGER | FOREIGN KEY → player.id | 创造者ID |
| match_id | INTEGER | FOREIGN KEY → match.id | 所在比赛ID |
| record_date | DATE | DEFAULT (date('now')) | 创造日期 |
| is_broken | INTEGER | DEFAULT 0 | 是否已被打破（0=当前纪录，1=已破） |

---

## 三、分层架构详细设计

### 3.1 模型层（model 包）

每个实体类包含：
- 私有属性（对应数据表字段）
- 无参构造 + 全参构造
- getter/setter 方法
- toString() 方法

| 类名 | 对应表 | 功能 |
|------|--------|------|
| Court.java | court | 场地实体 |
| Player.java | player | 选手实体 |
| Match.java | match | 比赛实体 |
| MatchPlayer.java | match_player | 比赛-选手关联实体 |
| Booking.java | booking | 场地预定实体 |
| Record.java | record | 纪录实体 |

### 3.2 数据访问层（dao 包）

采用 JDBC 原生方式操作 SQLite，每个 DAO 类封装对应表的 CRUD 操作。

| 类名 | 功能 |
|------|------|
| BaseDao.java | 基础 DAO：获取连接、关闭资源、通用查询方法 |
| CourtDao.java | 场地增删改查、按区域查询、状态更新 |
| PlayerDao.java | 选手增删改查、按级别/性别筛选 |
| MatchDao.java | 比赛增删改查、按日期/类型/状态筛选 |
| MatchPlayerDao.java | 选手报名参赛、录入成绩名次、查询比赛选手 |
| BookingDao.java | 场地预定、取消预定、时间冲突检测、查询预定 |
| RecordDao.java | 纪录增删改查、检测是否打破纪录 |

### 3.3 业务逻辑层（service 包）

核心业务规则在此层实现，调用 DAO 层完成数据操作。

| 类名 | 核心方法 | 业务规则 |
|------|----------|----------|
| CourtService.java | `listAllCourts()` `listByArea()` `setMaintenance()` | 场地状态管理 |
| PlayerService.java | `addPlayer()` `updatePlayer()` `deletePlayer()` `listAll()` `listByLevel()` `listByGender()` | 初始50名选手数据预置 |
| MatchService.java | `createMatch()` `cancelMatch()` `arrangeMatch()` `addPlayerToMatch()` `recordResult()` `listByStatus()` | **冲突检测：** 同一选手不能在同一时间段参加两个比赛；同一场地同一时间段不能有两场比赛；比赛时间不能交叉 |
| BookingService.java | `bookCourt()` `cancelBooking()` `listBookings()` `checkTimeConflict()` | **冲突检测：** 场地同一时间段不能重复预定；同一选手同一时间只能预定一个场地 |
| StatisticsService.java | `getMatchRankings()` `getPlayerRecords()` `checkRecordBreak()` `getPlayerStats()` `getCourtUsageStats()` | 统计名次、成绩、破纪录情况、场地使用率 |

### 3.4 表现层（ui 包）

纯控制台菜单，通过 `Scanner` 读取用户输入，数字选择菜单项。

| 类名 | 功能 |
|------|------|
| MainMenu.java | 主菜单（程序入口），提供各子模块入口 |
| CourtMenu.java | 场地管理菜单：查看场地、维护设置 |
| PlayerMenu.java | 选手管理菜单：增删改查选手 |
| MatchMenu.java | 比赛管理菜单：创建比赛、安排赛程、录入成绩 |
| BookingMenu.java | 场地预定菜单：预定/取消/查看 |
| StatisticsMenu.java | 统计查询菜单：名次、成绩、破纪录 |

**菜单交互设计（示例）：**
```
========== 大黑山羽毛球运动协会管理系统 ==========
1. 场地管理
2. 选手管理
3. 比赛管理
4. 场地预定
5. 统计查询
0. 退出系统
请选择：
```

### 3.5 工具类（util 包）

| 类名 | 功能 |
|------|------|
| DBUtil.java | SQLite 数据库连接管理（JDBC URL、获取连接、建表初始化） |
| InputUtil.java | 控制台输入工具（读取字符串、整数、日期，输入校验） |

---

## 四、核心业务规则

### 4.1 场地预定冲突检测
```
判断逻辑：同一场地(new) 与 已有预定(old) 时间冲突 ⇔
  new.start_time < old.end_time AND new.end_time > old.start_time
  AND new.book_date = old.book_date
```

### 4.2 比赛时间冲突检测（选手维度）
```
判断逻辑：选手A在新比赛(new)的时间段内，是否已经报名了其他比赛(old) ⇔
  查询 match_player 中选手A的所有比赛
  对每场比赛判断：new.match_date = old.match_date AND 时间重叠
```

### 4.3 比赛时间冲突检测（场地维度）
```
判断逻辑：场地C在新比赛(new)的时间段内，是否已被其他比赛占用 ⇔
  查询 match 表中同场地、同日期、时间重叠的比赛
```

### 4.4 破纪录检测
```
每次录入比赛成绩后：
1. 查询当前该纪录类型的历史最佳值
2. 与本次成绩比较
3. 如超越，在 record 表中插入新纪录（is_broken=0），旧纪录标记 is_broken=1
```

### 4.5 统计功能
- **比赛名次统计：** 按 match_id 查询 match_player，按 rank 排序
- **选手成绩统计：** 某选手所有参赛记录，包括得分、名次
- **破纪录统计：** 查询 record 表，按时间或类型展示
- **场地使用率：** 统计某时间段内各场地的预定/比赛占用情况

---

## 五、程序启动流程

```
1. 程序启动 → MainMenu.main()
2. DBUtil 初始化数据库连接
3. 检查数据库是否存在，不存在则自动建表 + 初始化基础数据
   - 初始化9片场地数据
   - 初始化50名选手数据（可预置或提示用户导入）
4. 显示主菜单，进入循环等待用户选择
5. 用户选择子菜单，进入对应 Menu 类
6. 各 Menu 类调用 Service 层完成业务操作
7. 用户选择退出时，关闭数据库连接，程序结束
```

---

## 六、开发顺序建议

```
第一阶段：基础设施
  1. 创建项目结构（包目录）
  2. DBUtil.java — 数据库连接 + 自动建表
  3. model 包所有实体类
  4. BaseDao.java — 通用数据库操作

第二阶段：数据访问层
  5. CourtDao.java
  6. PlayerDao.java
  7. MatchDao.java
  8. MatchPlayerDao.java
  9. BookingDao.java
  10. RecordDao.java

第三阶段：业务逻辑层
  11. CourtService.java
  12. PlayerService.java（含50名选手初始化）
  13. MatchService.java（含冲突检测）
  14. BookingService.java（含冲突检测）
  15. StatisticsService.java

第四阶段：表现层
  16. InputUtil.java
  17. MainMenu.java
  18. CourtMenu.java
  19. PlayerMenu.java
  20. MatchMenu.java
  21. BookingMenu.java
  22. StatisticsMenu.java

第五阶段：测试与完善
  23. 功能测试
  24. 边界条件处理
  25. 输入异常处理
```

---

## 七、依赖配置

### Maven 依赖 (pom.xml)
```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.42.0.0</version>
</dependency>
```

### 手动编译
下载 `sqlite-jdbc-3.42.0.0.jar`，放在项目 `lib/` 目录下，编译时加入 classpath。

---

## 八、类注释规范

每个类的 Javadoc 注释格式：

```java
/**
 * 类功能描述
 *
 * @author （编写者，留空待填）
 * @version 1.0
 * @since 2026-07-06
 */
```

每个方法的注释格式：

```java
/**
 * 方法功能描述
 *
 * @param paramName 参数说明
 * @return 返回值说明
 */
```

---

## 九、待确认事项

1. ~~场地分配：东面+南面3片，西面+北面6片 → 按2+1+3+3分配~~
2. ~~50名选手：是否需要预置数据，还是通过程序录入？→ 预置50条示例数据~~
3. 纪录类型：是否需要支持自定义纪录类型？→ 预设几种（最高得分、最快获胜、最长连胜），支持扩展
4. 比赛类型：男单、女单、男双、女双、混双，是否需要支持团体赛？→ 暂不支持，可扩展
