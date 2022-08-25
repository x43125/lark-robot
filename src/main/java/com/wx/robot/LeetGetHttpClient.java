package com.wx.robot;

import com.alibaba.fastjson.JSONObject;
import com.wx.robot.entity.content.Question;
import com.wx.robot.entity.content.ContentBody;
import com.wx.robot.entity.QuestionInfo;
import com.wx.robot.utils.UnicodeUtil;
import okhttp3.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangxiang
 * @date 2022/8/19 10:27
 * @description
 */
public class LeetGetHttpClient {
    private static final String QUESTION_URL = "https://leetcode-cn.com/problems/two-sum/description/";
    private static final String GRAPHQL_URL = "https://leetcode-cn.com/graphql";
    private static final String REPLACE_HTML_SIGNAL_REGEX = "<.*?>";
    private static final String REPLACE_HTML_TRANSFER_REGEX = "&.*?;";
    private static final String ADDRESS_DESCRIPTION = "点击开卷!!!";


    private static final Map<String, String> HTML_SIGNAL_MAP;

    static {
        HTML_SIGNAL_MAP = new HashMap<>();
        HTML_SIGNAL_MAP.put("&quot;", "\"");
        HTML_SIGNAL_MAP.put("&amp;", "&");
        HTML_SIGNAL_MAP.put("&lt;", "<");
        HTML_SIGNAL_MAP.put("&gt;", ">");
        HTML_SIGNAL_MAP.put("&nbsp;", " ");
    }


    /**
     * 获取题目
     * todo: 根据时间来获取某日一题
     *
     * @param date 要获取题目的日期
     * @return
     */
    public void buildQuestionUrlAddress(Date date, QuestionInfo questionInfo) throws IOException {
        String title = "";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String target = df.format(date);
        String today = df.format(new Date());

        if (target.equals(today)) {
            buildDailyQuestionTitleInfo(questionInfo);
            title = questionInfo.getTitle();
        } else {
            //todo 根据日期获得题目
        }
        questionInfo.setAddress("https://leetcode.cn/problems/" + title);
    }

    public String buildQuestionUrlAddress(String problemName) {
        return "https://leetcode.cn/problems/" + problemName;
    }


    /**
     * 获取每日一题的题目内容(英文),用来构建完整的请求API
     *
     * @return
     */
    public void buildDailyQuestionTitleInfo(QuestionInfo questionInfo) throws IOException {
        Connection.Response response;
        try {
            response = Jsoup.connect(QUESTION_URL)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            System.out.println("connect to questionUrl failed: " + QUESTION_URL);
            throw new IOException("connect to questionUrl failed: " + QUESTION_URL);
        }

        String csrftoken = response.cookie("aliyungf_tc");
        String cfduid = response.cookie("__cfduid");
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        // 获取LeetCode题目标题时的查询字符串
        String postBody = "query questionOfToday { todayRecord { question { questionFrontendId questionTitleSlug " +
                "__typename } lastSubmission { id __typename } date userStatus __typename }}";

        assert csrftoken != null;
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/graphql")
                .addHeader("Referer", QUESTION_URL)
                .addHeader("Cookie", "__cfduid=" + cfduid + ";" + "csrftoken=" + csrftoken)
                .addHeader("x-csrftoken", csrftoken)
                .url(GRAPHQL_URL)
                .post(RequestBody.create(MediaType.parse("application/graphql; charset=utf-8"), postBody))
                .build();

        String titleInfo;
        try {
            Response response1 = client.newCall(request).execute();
            titleInfo = UnicodeUtil.unicode2String(response1.body().string());
        } catch (IOException e) {
            System.out.println("connect to graphqlUrl failed: " + GRAPHQL_URL);
            throw new IOException("connect to graphqlUrl failed: " + GRAPHQL_URL);
        }

        //将title和id解析出来
        JSONObject jsonObject = JSONObject.parseObject(titleInfo);
        String title = jsonObject.getJSONObject("data")
                .getJSONArray("todayRecord")
                .getJSONObject(0)
                .getJSONObject("question")
                .getString("questionTitleSlug");
        String frontId = jsonObject.getJSONObject("data")
                .getJSONArray("todayRecord")
                .getJSONObject(0)
                .getJSONObject("question")
                .getString("questionFrontendId");

        questionInfo.setTitle(title);
        questionInfo.setFrontId(frontId);
    }

    /**
     * 获取题目内容
     *
     * @param title
     * @return
     * @throws IOException
     */
    private String getContent(String title) throws IOException {
        Connection.Response response;
        try {
            response = Jsoup.connect(QUESTION_URL)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (IOException e) {
            System.out.println("connect to QUESTION_URL failed: " + QUESTION_URL);
            throw new IOException("connect to QUESTION_URL failed: " + QUESTION_URL);
        }

        String csrftoken = response.cookie("aliyungf_tc");
        String cfduid = response.cookie("__cfduid");
        OkHttpClient client = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .build();

        String query = "query{   question(titleSlug:\"%s\") {  questionId   translatedTitle    translatedContent    difficulty   }   }";
        String postBody = String.format(query, title);
        assert csrftoken != null;
        Request request = new Request.Builder()
                .addHeader("Content-Type", "application/graphql")
                .addHeader("Referer", QUESTION_URL)
                .addHeader("Cookie", "__cfduid=" + cfduid + ";" + "csrftoken=" + csrftoken)
                .addHeader("x-csrftoken", csrftoken)
                .url(GRAPHQL_URL)
                .post(RequestBody.create(MediaType.parse("application/graphql; charset=utf-8"), postBody))
                .build();

        try {
            Response response1 = client.newCall(request).execute();
            //由于json的原因，返回的数据中文变成了Unicode码，需要另外解码
            return UnicodeUtil.unicode2String(response1.body().string());
        } catch (IOException e) {
            System.out.println("request failed: " + e.getMessage());
            throw new IOException("request failed: " + e.getMessage());
        }
    }

    /**
     * 格式化收到的request body内容，将转义字符、html字符等去掉
     *
     * @param content
     * @return
     */
    public String formatContent(String content) {
        String replaceHtmlSignalStr = content.replaceAll(REPLACE_HTML_SIGNAL_REGEX, "");
        return replaceHtmlSignal(REPLACE_HTML_TRANSFER_REGEX, replaceHtmlSignalStr);
    }

    private static String replaceHtmlSignal(String regex, String input) {
        if (regex == null || regex.length() == 0) {
            regex = REPLACE_HTML_SIGNAL_REGEX;
        }

        Pattern p = Pattern.compile(regex);
        // 获取 matcher 对象
        Matcher m = p.matcher(input);
        String tempInput = input;
        while (m.find()) {
            int start = m.start();
            int end = m.end();
            String matchSignal = tempInput.substring(start, end);
            if (HTML_SIGNAL_MAP.containsKey(matchSignal)) {
                tempInput = tempInput.replace(matchSignal, HTML_SIGNAL_MAP.get(matchSignal));
            } else {
                HTML_SIGNAL_MAP.put(matchSignal, "$");
                tempInput = tempInput.replace(matchSignal, "$");
            }
            m = p.matcher(tempInput);
        }

        return tempInput;
    }


    public QuestionInfo getQuestion(String prompt, Date date) throws IOException {
        QuestionInfo questionInfo = new QuestionInfo();
        questionInfo.setPrompt(prompt);
        questionInfo.setAddressDescription(ADDRESS_DESCRIPTION);

        // 获取题目 url
        buildQuestionUrlAddress(date, questionInfo);
        System.out.println("题目url: " + questionInfo.getAddress() + " 获取成功");
        // 获取每日一题标题
        buildDailyQuestionTitleInfo(questionInfo);
        System.out.println("题目标题: " + questionInfo.getTitle() + " 获取成功");
        // 获取内容
        String requestBody = getContent(questionInfo.getTitle());
        System.out.println("题目内容 获取成功");
        // 格式化得到的内容
        String formatContent = formatContent(requestBody);
        System.out.println("题目内容 格式化完成");
        // 将格式化后的内容转成 QuestionInfo
        System.out.println("开始将内容封装成 questionInfo...");
        ContentBody body = JSONObject.parseObject(formatContent, ContentBody.class);
        Question question = body.getData().getQuestion();

        questionInfo.setZhTitle(question.getTranslatedTitle());
        questionInfo.setDifficulty(question.getDifficulty());
        questionInfo.setContent(question.getTranslatedContent());
        questionInfo.setId(question.getQuestionId());

        System.out.println("内容封装成 questionInfo 完成");
        return questionInfo;
    }

    /**
     * 获取到题目主干内容
     *
     * @param requestBody
     * @return
     */
    private static String parseBody(String requestBody) {
        return requestBody.substring(requestBody.indexOf("\"translatedContent\":\"") + "\"translatedContent\":\"".length(), requestBody.indexOf("\\n\",\"difficulty\""));
    }
}
