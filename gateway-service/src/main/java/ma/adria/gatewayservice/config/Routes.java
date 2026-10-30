package ma.adria.gatewayservice.config;
import org.springframework.context.annotation.*;import org.springframework.cloud.gateway.route.*;
@Configuration public class Routes {@Bean RouteLocator routes(RouteLocatorBuilder b){return b.routes().route("accounts",r->r.path("/api/accounts/**").uri("lb://account-service")).route("transactions",r->r.path("/api/transactions/**").uri("lb://transaction-service")).route("assistant",r->r.path("/api/chat/**").uri("lb://assistant-service")).build();}}
