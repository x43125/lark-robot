package com.wx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wx.robot.entity.QuestionInfo;
import com.wx.robot.entity.RichBody;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangxiang
 * @date 2022/8/13 15:01
 * @description
 */
public class RobotTest {
    @Test
    public void testCalendar() {
        Calendar instance = Calendar.getInstance();
        int i = instance.get(Calendar.MONTH);
        System.out.println(i);
    }

    @Test
    public void testSendMsg() {
        String larkRobotUrl = "https://open.feishu.cn/open-apis/bot/v2/hook/dd70a5a9-716f-428a-b6b1-c2503e423cc7";
        String requestMethod = "POST";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String prompt = "卷王之王卷穿肠提醒您每日一卷已送达，今天你卷了吗？";

        HttpURLConnection con;
        BufferedReader buffer;
        StringBuffer resultBuffer;

        try {
            URL url = new URL(larkRobotUrl);
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();
            //设置请求类型
            con.setRequestMethod(requestMethod);
            //设置请求需要返回的数据类型和字符集类型
            con.setRequestProperty("Content-Type", "application/json;charset=GBK");
            //允许写出
            con.setDoOutput(true);
            //允许读入
            con.setDoInput(true);
            //不使用缓存
            con.setUseCaches(false);

            OutputStream os = con.getOutputStream();

            String sendContent = buildRichContent(prompt);
//            String sendContent = buildPictureContent(prompt);

            os.write(sendContent.getBytes());

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
                System.out.println(dateFormat.format(new Date()) + " === " + resultBuffer.toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildSimpleContent(String prompt) {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> content = new HashMap<>();

        // 普通模式
        content.put("text", prompt);
        map.put("msg_type", "text");
        map.put("content", content);
        return JSON.toJSONString(map);
    }

    private String buildRichContent(String prompt) {

        Map<String, Object> map = new HashMap<>();
        Map<String, Object> content = new HashMap<>();

        map.put("msg_type", "post");
        map.put("content", content);

        Map<String, Object> post = new HashMap<>();
        Map<String, Object> zhCN = new HashMap<>();

        content.put("post", post);
        post.put("zh-CN", zhCN);
        zhCN.put("title", "leetcode " + prompt);

        com.wx.robot.entity.RichBody[][] richBodies = new com.wx.robot.entity.RichBody[1][2];
        com.wx.robot.entity.RichBody richBodyPrompt = new com.wx.robot.entity.RichBody();
        com.wx.robot.entity.RichBody richBodyContent = new com.wx.robot.entity.RichBody();
        richBodyPrompt.setTag("text");
        richBodyPrompt.setText("leetcode robot上线");

        richBodyContent.setTag("a");
        richBodyContent.setText("每日一卷");
        richBodyContent.setHref("https://sspai.com/post/68578");

        richBodies[0][0] = richBodyPrompt;
        richBodies[0][1] = richBodyContent;

        zhCN.put("content", richBodies);

        return JSON.toJSONString(map);
    }

    private String buildPictureContent(String prompt) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> card = new HashMap<>();
        Map<String, Object> config = new HashMap<>();
        Map<String, Object> header = new HashMap<>();
        Map<String, Object> title = new HashMap<>();
        Element[] elements = new Element[1];
        Element element = new Element();
        Text text = new Text();
        text.setTag("plain_text");
        text.setContent("test content!!!");
        element.setTag("div");
        element.setText(text);

        map.put("msg_type", "interactive");
        map.put("card", card);
        card.put("config", config);
        card.put("header", header);
        card.put("elements", elements);
        config.put("wide_screen_mode", true);
        header.put("title", title);
        header.put("template", "red");
        title.put("tag", "plain_text");
        title.put("content", "this is header");

        return JSON.toJSONString(map);
    }

    static class Element {
        private String tag;
        private Text text;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public Text getText() {
            return text;
        }

        public void setText(Text text) {
            this.text = text;
        }
    }

    static class Text {
        private String tag;
        private String content;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Test
    public void jsonStr2Object() {
//        InputStream is = RobotTest.class.getClassLoader().getResourceAsStream("json.txt");

        String jsonStr = "{\"data\":{\"question\":{\"questionId\":\"779\",\"translatedTitle\":\"最多能完成排序的块 II\",\"translatedContent\":\"这个问题和$最多能完成排序的块$相似，但给定数组中的元素可以重复，输入数组最大长度为2000，其中的元素最大为10**8。\\n\\narr是一个可能包含重复元素的整数数组，我们将这个数组分割成几个$块$，并将这些块分别进行排序。之后再连接起来，使得连接的结果和按升序排序后的原数组相同。\\n\\n我们最多能将数组分成多少块？\\n\\n示例 1:\\n\\n\\n输入: arr = [5,4,3,2,1]\\n输出: 1\\n解释:\\n将数组分成2块或者更多块，都无法得到所需的结果。\\n例如，分成 [5, 4], [3, 2, 1] 的结果是 [4, 5, 1, 2, 3]，这不是有序的数组。 \\n\\n\\n示例 2:\\n\\n\\n输入: arr = [2,1,3,4,4]\\n输出: 4\\n解释:\\n我们可以把它分成两块，例如 [2, 1], [3, 4, 4]。\\n然而，分成 [2, 1], [3], [4], [4] 可以得到最多的块数。 \\n\\n\\n注意:\\n\\n\\n\\tarr的长度在[1, 2000]之间。\\n\\tarr[i]的大小在[0, 10**8]之间。\\n\\n\",\"difficulty\":\"Hard\"}}}";
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        JSONObject translatedTitle = jsonObject.getJSONObject("translatedTitle");
        QuestionInfo questionInfo = JSONObject.parseObject(jsonStr, QuestionInfo.class);
        System.out.println(questionInfo);
    }

    @Test
    public void richText() {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> content = new HashMap<>();
        Map<String, Object> post = new HashMap<>();
        Map<String, Object> zhCN = new HashMap<>();

        RichBody[][] arr = new RichBody[2][2];
        RichBody richBody = new RichBody();
        richBody.setTag("text");
        richBody.setHref("https://sspai.com/u/100gle/updates");
        richBody.setText("点击查看");
        arr[0][0] = richBody;

        zhCN.put("title", "富文本消息测试！");
        zhCN.put("content", arr);


        post.put("zh-CN", zhCN);
        content.put("post", post);

        map.put("msg_type", "text");
        map.put("content", content);

        System.out.println();

        String s = JSONObject.toJSONString(map);
        System.out.println(s);
    }

    static class RichBody {
        private String tag;
        private String text;
        private String href;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }
    }

    @Test
    public void testCalcTimeDuration() {
        Instant inst1 = Instant.now();  //当前的时间
        Instant tomorrow = inst1.plus(Duration.ofDays(1));


//        System.out.println("Inst1：" + inst1);
//        Instant inst2 = inst1.plus(Duration.ofSeconds(10));     //当前时间+10秒后的时间
//        System.out.println("Inst2：" + inst2);
//        Instant inst3 = inst1.plus(Duration.ofDays(125));       //当前时间+125天后的时间
//        System.out.println("inst3：" + inst3);

//        System.out.println("以毫秒计的时间差：" + Duration.between(inst1, inst2).toMillis());
//        System.out.println("以秒计的时间差：" + Duration.between(inst1, inst3).getSeconds());

//        Instant targetInst = Instant.parse("2022-08-17T01:00:00Z");
//        System.out.println("Duration.between(inst1, targetInst).getSeconds() = " + Duration.between(inst1, targetInst).getSeconds());
//
//
//        LocalDateTime ldt = LocalDateTime.ofInstant(inst1, ZoneId.systemDefault());
//        System.out.printf("%s %d %d at %d:%d:%d%n", ldt.getMonth(), ldt.getDayOfMonth(),
//                ldt.getYear(), ldt.getHour(), ldt.getMinute(), ldt.getSecond());

        System.out.println("tomorrow.getNano() = " + tomorrow.getNano());
        System.out.println("inst1.getEpochSecond() = " + inst1.getEpochSecond());
        System.out.println("inst1.getNano() = " + inst1.getNano());

        long seconds = Duration.between(inst1, tomorrow).getSeconds();
        System.out.println("s = " + seconds);

    }
}

/*
{
  "msg_type": "text", // 指定消息类型
  "content": {  // 消息内容主体
    "text": "your-message"
  }
}
 */