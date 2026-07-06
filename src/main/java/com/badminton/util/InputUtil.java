package com.badminton.util;

import java.util.Scanner;

/**
 * 控制台输入工具类 — 提供各类输入的读取与校验
 *
 * @author （编写者）
 * @version 1.0
 * @since 2026-07-06
 */
public class InputUtil {

    private static final Scanner scanner = new Scanner(System.in);

    /**
     * 读取整数值（带提示信息）
     *
     * @param prompt 提示信息
     * @return 用户输入的整数
     */
    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[错误] 请输入有效的整数！");
            }
        }
    }

    /**
     * 读取整数值（无提示）
     *
     * @return 用户输入的整数
     */
    public static int readInt() {
        return readInt("");
    }

    /**
     * 读取字符串（带提示信息）
     *
     * @param prompt 提示信息
     * @return 用户输入的字符串
     */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * 读取日期（格式：YYYY-MM-DD）
     *
     * @param prompt 提示信息
     * @return 用户输入的日期字符串
     */
    public static String readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
                return input;
            }
            System.out.println("[错误] 日期格式错误，请输入 YYYY-MM-DD（如：2026-07-06）！");
        }
    }

    /**
     * 读取时间（格式：HH:MM）
     *
     * @param prompt 提示信息
     * @return 用户输入的时间字符串
     */
    public static String readTime(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches("\\d{2}:\\d{2}")) {
                return input;
            }
            System.out.println("[错误] 时间格式错误，请输入 HH:MM（如：09:00）！");
        }
    }

    /**
     * 按任意键继续
     */
    public static void pressEnter() {
        System.out.print("\n按回车键继续...");
        scanner.nextLine();
    }
}
