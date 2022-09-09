package com.wx.robot;

import com.alibaba.fastjson.JSON;
import com.wx.robot.entity.QuestionInfo;
import com.wx.robot.entity.RichBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangxiang
 * @date 2022/8/19 11:30
 * @description
 */
public class LarkRobot {


    /**
     * 向飞书发送每日一题
     *
     * @param robotUrl
     * @param requestType POST, GET
     * @return
     */
    public String sendRequest(String robotUrl, String requestType, String content) throws IOException {
        System.out.println("开始发送请求...");
        HttpURLConnection con;
        BufferedReader buffer;
        StringBuffer resultBuffer;

        URL url = new URL(robotUrl);
        //得到连接对象
        con = (HttpURLConnection) url.openConnection();
        //设置请求类型
        con.setRequestMethod(requestType);
        //设置请求需要返回的数据类型和字符集类型
        con.setRequestProperty("Content-Type", "application/json;charset=GBK");
        //允许写出
        con.setDoOutput(true);
        //允许读入
        con.setDoInput(true);
        //不使用缓存
        con.setUseCaches(false);

        OutputStream os = con.getOutputStream();
        os.write(content.getBytes());

        //得到响应码
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            //得到响应流
            InputStream inputStream = con.getInputStream();
            //将响应流转换成字符串
            resultBuffer = new StringBuffer();
            String line;
            buffer = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
            while ((line = buffer.readLine()) != null) {
                resultBuffer.append(line);
            }
            return resultBuffer.toString();
        } else {
            throw new IOException(responseCode + " : " + con.getResponseMessage());
        }
    }

    public String buildRichContent(QuestionInfo questionInfo) {
        System.out.println("开始转换成富文本类型内容...");
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> content = new HashMap<>();

        map.put("msg_type", "post");
        map.put("content", content);

        Map<String, Object> post = new HashMap<>();
        Map<String, Object> zhCN = new HashMap<>();

        content.put("post", post);
        post.put("zh-CN", zhCN);
        zhCN.put("title", questionInfo.getPrompt());

        RichBody[][] richBodies = buildRichBody(questionInfo);
        zhCN.put("content", richBodies);
        return JSON.toJSONString(map);
    }

    /**
     * 装载内容
     *
     * @param questionInfo
     * @return
     */
    public RichBody[][] buildRichBody(QuestionInfo questionInfo) {
        String content = questionInfo.getContent();

        content = content.replace("\t", "    ");
        String[] split = content.split("\n");

        RichBody[][] richBodies = new RichBody[2 + split.length][2];
        RichBody richBodyPrompt = new RichBody();
        RichBody richBodyContent = new RichBody();
        richBodyPrompt.setTag("text");
        richBodyPrompt.setText(questionInfo.getFrontId() + "." + questionInfo.getZhTitle() + "(" + questionInfo.getDifficulty() + "):");
        richBodies[0][0] = richBodyPrompt;

        richBodyContent.setTag("a");
        richBodyContent.setText(questionInfo.getAddressDescription());
        richBodyContent.setHref(questionInfo.getAddress());
        richBodies[0][1] = richBodyContent;

        RichBody richBodySplit = new RichBody();
        richBodySplit.setTag("text");
        richBodySplit.setText("######################################################################################");
        richBodies[1][0] = richBodySplit;

        RichBody richBodyNull = new RichBody();
        richBodyNull.setTag("text");
        richBodyNull.setText("");
        richBodies[1][1] = richBodyNull;

        for (int i = 0; i < split.length; i++) {
            RichBody richBody = new RichBody();
            richBody.setTag("text");
            richBody.setText(split[i]);
            richBodies[i + 2][0] = richBody;
            RichBody tmpRichBodyNull = new RichBody();
            tmpRichBodyNull.setTag("text");
            tmpRichBodyNull.setText("");
            richBodies[i + 2][1] = tmpRichBodyNull;
        }

        return richBodies;
    }

    /**
     * 根据内容自动选择相应的格式装配
     *
     * @param questionInfo
     * @return
     */
    public String buildPrintContent(QuestionInfo questionInfo) {
        System.out.println("开始将 questionInfo 转换成可直接发送的字符串内容...");
        String richContent = buildRichContent(questionInfo);
        System.out.println("questionInfo 转成富文本类型内容成功");
        return richContent;
//        return buildSimplePrintContent(questionInfo);
    }


    /**
     * 将 QuestionInfo 装配成适合发送向飞书的格式
     * 1. 普通模式
     *
     * @param questionInfo
     * @return
     */
    private static String buildSimplePrintContent(QuestionInfo questionInfo) {
        System.out.println("开始转换成普通文本类型...");
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> content = new HashMap<>();

        map.put("content", content);
        map.put("msg_type", "text");
        content.put("text", questionInfo.getPrompt() + "\n" +
                questionInfo.getTitle() + "(" +
                questionInfo.getTitle() + ")\n难度: " +
                questionInfo.getDifficulty() + "\n" +
                questionInfo.getAddress());

        return JSON.toJSONString(map);
    }
}
