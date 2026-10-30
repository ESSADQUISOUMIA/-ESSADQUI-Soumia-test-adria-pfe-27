package ma.adria.transactionservice.config;
import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
@Configuration public class SecurityConfig {
@Bean SecurityFilterChain security(HttpSecurity http) throws Exception {
return http.csrf(c->c.disable()).sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
.authorizeHttpRequests(a->a.requestMatchers("/actuator/health/**").permitAll().anyRequest().authenticated())
.oauth2ResourceServer(o->o.jwt(j->{})).build(); }
}
