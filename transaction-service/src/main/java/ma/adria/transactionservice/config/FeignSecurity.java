package ma.adria.transactionservice.config;
import feign.RequestInterceptor;import org.springframework.context.annotation.*;import org.springframework.security.core.context.SecurityContextHolder;import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
@Configuration public class FeignSecurity {@Bean RequestInterceptor bearer(){return template->{var a=SecurityContextHolder.getContext().getAuthentication();if(a instanceof JwtAuthenticationToken jwt)template.header("Authorization","Bearer "+jwt.getToken().getTokenValue());};}}
