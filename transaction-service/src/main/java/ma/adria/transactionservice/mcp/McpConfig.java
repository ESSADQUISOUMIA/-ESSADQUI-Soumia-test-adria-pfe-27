package ma.adria.transactionservice.mcp;
import org.springframework.context.annotation.*;import org.springframework.ai.tool.ToolCallbackProvider;import org.springframework.ai.tool.method.MethodToolCallbackProvider;
@Configuration public class McpConfig {@Bean ToolCallbackProvider tools(BankTools tools){return MethodToolCallbackProvider.builder().toolObjects(tools).build();}}
