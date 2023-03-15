package chatGPT.Controller;


import org.springframework.web.bind.annotation.*;
import tools.chatgpt.api.utils.OpenAIAPI;

@RestController
public class ChatController {

    OpenAIAPI openAIAPI = new OpenAIAPI();

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/chat")
    public String chatGet(@RequestParam String userId, @RequestParam String sessionId, @RequestParam String message) {
        return openAIAPI.chat(userId, sessionId, message);
    }

    @PostMapping("/chat")
    public String chatPost(@RequestBody String userId, @RequestBody String sessionId, @RequestBody String message) {
        return openAIAPI.chat(userId, sessionId, message);
    }

}
