package tools.mongoDB.utils;


import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MongoDBUtils {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;

    public MongoDBUtils() {
        // 连接 MongoDB 数据库
        mongoClient = new MongoClient("localhost", 27017);
        database = mongoClient.getDatabase("mydb");
        collection = database.getCollection("user_dialogues");
    }

    public void saveDialogue(String userId, String sessionId, String dialogue) {
        // 构建复合主键
        Document id = new Document("user_id", userId).append("session_id", sessionId);

        // 获取指定文档
        Document query = new Document("_id", id);
        Document doc = collection.find(query).first();

        if (doc == null) {
            // 如果文档不存在，则创建一个新文档
            doc = new Document("_id", id).append("dialogues", new ArrayList<String>());
        }

        // 将对话信息添加到对话历史列表中
        List<String> dialogues = (List<String>) doc.get("dialogues");
        dialogues.add(dialogue);

        // 更新文档中的对话历史字段
        collection.replaceOne(query, doc, new UpdateOptions().upsert(true));
    }

    public List<String> getDialogues(String userId, String sessionId) {
        // 构建复合主键
        Document id = new Document("user_id", userId).append("session_id", sessionId);

        // 查询指定文档的对话历史
        Document query = new Document("_id", id);
        Document doc = collection.find(query).first();

        if (doc == null) {
            return new ArrayList<String>();
        } else {
// 返回对话历史列表
            return (List<String>) doc.get("dialogues");
        }
    }
    public static void main(String[] args) {
        MongoDBUtils storage = new MongoDBUtils();

        // 示例用法：保存对话历史记录
        String userId = "123";
        String sessionId = "456";
        String dialogue1 = "Hello, how are you?";
        String dialogue2 = "I'm fine, thanks for asking.";
        storage.saveDialogue(userId, sessionId, dialogue1);
        storage.saveDialogue(userId, sessionId, dialogue2);

        // 示例用法：获取对话历史记录
        List<String> dialogues = storage.getDialogues(userId, sessionId);
        for (String dialogue : dialogues) {
            System.out.println(dialogue);
        }
    }
}
