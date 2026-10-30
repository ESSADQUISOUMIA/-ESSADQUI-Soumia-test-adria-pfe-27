package ma.adria.assistantservice.web.rest;
import org.springframework.web.bind.annotation.*;import ma.adria.assistantservice.service.AssistantService;import java.util.Map;
@RestController @RequestMapping("/api/chat") public class ChatController {private final AssistantService s;public ChatController(AssistantService s){this.s=s;}public record Request(String message){}@PostMapping public Map<String,String> ask(@RequestBody Request q){return Map.of("answer",s.ask(q.message()));}}
