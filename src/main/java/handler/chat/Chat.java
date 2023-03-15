package handler.chat;

import org.junit.Test;
import tools.chatgpt.api.utils.OpenAIAPI;
import tools.deepl.api.utils.DeepLAPI;
import tools.mongoDB.utils.MongoDBUtils;

import java.io.IOException;
import java.util.Scanner;

public class Chat {
    private OpenAIAPI openAIAPI = new OpenAIAPI();

    private String chat(String userId, String sessionId, String message) {
        Chat chat = new Chat();
        return chat.openAIAPI.chat(userId, sessionId, message);
    }
    public static void main(String[] args) {
        String userId = "pp";
        String sessionId = "gpt";
        String message = "哪一年还发生了什么大事情吗";
        Chat chat = new Chat();
        System.out.println(chat.chat(userId, sessionId, message));
    }


// 实现控制台输入，每次输入后回车，调用chat方法，然后继续输入。
// 你可以在chat方法中加入一些逻辑，比如判断输入的内容是否是“退出”，如果是就退出循环。
    @Test
    public void chatTest() throws IOException {
        String userId = "pp";
        String sessionId = "gpt";
        Chat chat = new Chat();
        String txt = "";
        Scanner scanner = new Scanner(System.in);
        while(!txt.equals("-1")){
            txt = scanner.nextLine();
            System.out.println(chat.chat(userId, sessionId, txt));
        }
    }
}
