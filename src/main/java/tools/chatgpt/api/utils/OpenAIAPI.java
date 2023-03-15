package tools.chatgpt.api.utils;

import cn.hutool.core.convert.ConvertException;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.context.annotation.Configuration;
import tools.mongoDB.utils.MongoDBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OpenAIAPI {

    private MongoDBUtils mongoDBUtils = MongoDBUtils.getInstance();

    /**
     * 聊天端点
     */
    String chatEndpoint = "https://api.openai.com/v1/chat/completions";
    /**
     * api密匙
     */
    String apiKey = "Bearer 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";

    /**
     * 发送消息
     *
     * @param txt 内容 {@link String} userId 用户id {@link String} sessionId 会话id  {@link String}
     * @return {@link String} 返回消息
     */
    public String chat(String userId, String sessionId, String txt) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("model", "gpt-3.5-turbo");
        List<Map<String, String>> dataList = new ArrayList<>();
        List<Map<String, String>> dialogues = getDialogues(userId, sessionId);
        dataList.addAll(dialogues);
        dataList.add(new HashMap<String, String>(){{
            put("role", "user");
            put("content", txt);
        }});
        paramMap.put("messages", dataList);
        JSONObject message = null;
        try {
            System.setProperty("https.proxyHost", "127.0.0.1");
            System.setProperty("https.proxyPort", "7890");
            String body = HttpRequest.post(chatEndpoint)
                .header("Authorization", apiKey)
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(paramMap))
                .execute()
                .body();
            JSONObject jsonObject = JSONUtil.parseObj(body);
            System.out.println(jsonObject);
            JSONArray choices = jsonObject.getJSONArray("choices");
            JSONObject result = choices.get(0, JSONObject.class, Boolean.TRUE);
            message = result.getJSONObject("message");
            //        将对话信息添加到对话历史列表中
            mongoDBUtils.saveDialogue(userId, sessionId, txt);
            mongoDBUtils.saveDialogue(userId, sessionId,  message.getStr("content"));
        } catch (HttpException e) {
            return "出现了异常";
        } catch (ConvertException e) {
            return "出现了异常";
        }
        return message.getStr("content");
    }

//从mongoDB中获取对话历史,然后将list按照一个user一个assistant的顺序，塞入 List<Map<String, String>> dataList中
    public List<Map<String, String>> getDialogues(String userId, String sessionId) {
        List<String> dialogues = mongoDBUtils.getDialogues(userId, sessionId);
        List<Map<String, String>> dataList = new ArrayList<>();
        for (int i = 0; i < dialogues.size(); i++) {
            if (i % 2 == 0) {
                int finalI = i;
                dataList.add(new HashMap<String, String>() {{
                    put("role", "user");
                    put("content", dialogues.get(finalI));
                }});
            } else {
                int finalI1 = i;
                dataList.add(new HashMap<String, String>() {{
                    put("role", "assistant");
                    put("content", dialogues.get(finalI1));
                }});
            }
        }
        return dataList;
    }

//    调用MongoDB删除对话历史
    public void deleteDialogues(String userId, String sessionId) {
        mongoDBUtils.deleteDialoguesByKey(userId, sessionId);
    }

}
