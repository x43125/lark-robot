package com.wx.robot;


import com.wx.robot.entity.QuestionInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangxiang
 * @date 2022/8/11 11:10
 * @description 卷王之王机器人
 */
public class LeetCodeDailyQuestionRobot {

    private LarkRobot larkRobot;
    private LeetGetHttpClient leetGetHttpClient;

    public LeetCodeDailyQuestionRobot(LarkRobot larkRobot, LeetGetHttpClient leetGetHttpClient) {
        this.larkRobot = larkRobot;
        this.leetGetHttpClient = leetGetHttpClient;
    }


    /**
     * @param execTime 每天几点执行定时任务   24小时制时间 例: 08:00:00  20:00:00
     * @param task     定时任务执行的具体任务
     */
    private static void timing(String nowTime, String execTime, TimerTask task) {
        // todo 计算运行到这的时候的时间和目标时间的差值
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println("当前时间: " + df.format(new Date()) + " " + nowTime + "\n待运行时间: " + execTime);
        long delayTime = calcGapTime(nowTime, execTime);
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        // 推迟gapTime后执行一次，之后每隔1天执行一次
        if (delayTime != 0) {
            System.out.println("首次发送将在" + delayTime + "秒后执行");
        }
        executor.scheduleAtFixedRate(task, delayTime, 24 * 60 * 60, TimeUnit.SECONDS);
    }

    /**
     * 计算首次运行代码到这的时候的时间与目标时间相差多少秒
     *
     * @param nowTime  14:36:24
     * @param execTime 10:00:00
     * @return
     */
    private static long calcGapTime(String nowTime, String execTime) {
        if (nowTime.equals(execTime)) {
            return 0;
        }

        long delayTime;

        String[] nowArr = nowTime.split(":");
        String[] execArr = execTime.split(":");

        int[] nowTimeArr = new int[nowArr.length];
        int[] execTimeArr = new int[execArr.length];

        for (int i = 0; i < nowArr.length; i++) {
            nowTimeArr[i] = Integer.parseInt(nowArr[i]);
            execTimeArr[i] = Integer.parseInt(execArr[i]);
        }

        int now = nowTimeArr[0] * 60 * 60 + nowTimeArr[1] * 60 + nowTimeArr[2];
        int exec = execTimeArr[0] * 60 * 60 + execTimeArr[1] * 60 + execTimeArr[2];

        if (now <= exec) {
            delayTime = exec - now;
        } else {
            delayTime = 24 * 60 * 60 - now + exec;
        }

        return delayTime;
    }

    private void start(String[] args, String larkRobotUrl, String prompt, String successLog, String failLog) {
        System.out.println("开卷！！！\n");

        String requestMethod = "POST";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        // 如果不输入执行开始时间的话，就按照当前开始算
        String nowTime = new SimpleDateFormat("HH:mm:ss").format(new Date());
        // 校验参数
        verifyArgs(args);
        String executeTime = args.length != 0 ? args[0] : nowTime;

        timing(nowTime, executeTime, new TimerTask() {
            @Override
            public void run() {
                String printQuestionInfo = failLog;
                try {
                    // 获取题目
                    QuestionInfo questionInfo = leetGetHttpClient.getQuestion(prompt, new Date());
                    // 将questionInfo转成字符串准备发送
                    printQuestionInfo = larkRobot.buildPrintContent(questionInfo);
                    System.out.println(successLog);
                } catch (IOException e) {
                    System.out.println(failLog + ": " + e.getMessage());
                }

                try {
                    String result = larkRobot.sendRequest(larkRobotUrl, requestMethod, printQuestionInfo);
                    System.out.println(dateFormat.format(new Date()) + " ===== 发送完成 ==== 返回值 ====" + result);
                } catch (IOException e) {
                    System.out.println("发送失败: " + e.getMessage());
                }
            }
        });
    }


    /**
     * 校验参数格式，不对则直接报错停止项目
     *
     * @param args
     */
    private static void verifyArgs(String[] args) {
        System.out.print("輸入的参数值: ");
        for (String arg : args) {
            System.out.print(arg + " ");
        }
        System.out.println();
    }


    /**
     * @param args 目前参数只有一个为 目标执行时间：每天发送的时间 24小时制时间 例: 08:00:00  20:00:00
     *             如果为空则为当前时间，且会立刻发送一条
     */
    public static void main(String[] args) {
        System.out.println("卷王之王启动！！！");
        String larkRobotUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/dd70a5a9-716f-428a-b6b1-c2503e423cc7";
        String prompt = "卷王之王卷穿肠提醒您每日一卷已送达，今天你卷了吗？";
        String successLog = "获取每日一题成功";
        String failLog = "获取每日一题失败";

        LarkRobot larkRobot = new LarkRobot();
        LeetGetHttpClient leetGetHttpClient = new LeetGetHttpClient();

        LeetCodeDailyQuestionRobot leetCodeDailyQuestionRobot = new LeetCodeDailyQuestionRobot(larkRobot, leetGetHttpClient);
        leetCodeDailyQuestionRobot.start(args, larkRobotUrl, prompt, successLog, failLog);
    }
}