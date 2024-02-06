package at.oegeg.etd.Config;

import at.oegeg.etd.Security.Services.JwtAuthenticationFilter;
import at.oegeg.etd.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class MultiSecurityConfig
{
    @Configuration
    @Order(1)
    @RequiredArgsConstructor
    public static class RestApiSecurityConfig
    {
        private final JwtAuthenticationFilter _jwtAuthenticationFilter;
        private final AuthenticationProvider _authenticationProvider;
        private final RestAuthenticationEntryPoint _restAuthenticationEntryPoint;
        @Autowired
        private ApplicationContext applicationContext;


        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
        {
            return http
                    .cors().and().csrf().disable()
                    .securityMatcher("/api/**")
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/api/v1/login").permitAll()
                            .anyRequest()
                            .authenticated())
                    .httpBasic()
                    .authenticationEntryPoint(_restAuthenticationEntryPoint)
                    .and().build();
                    //.csrf(AbstractHttpConfigurer::disable)
                    //.authorizeHttpRequests(auth -> auth
                    //        .requestMatchers("/**").permitAll()
                    //        .requestMatchers("/api/v1/login").permitAll()
                    //        .requestMatchers("/api/**").authenticated())
                    ////.httpBasic(hbc -> hbc.authenticationEntryPoint(_restAuthenticationEntryPoint))
                    //.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    //.authenticationProvider(_authenticationProvider)
                    //.addFilterBefore(_jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                    //.exceptionHandling()
                    //.defaultAuthenticationEntryPointFor(_restAuthenticationEntryPoint, new AntPathRequestMatcher("/api/**"))
                    //.accessDeniedPage("/403")
                    //.and()
                    //.csrf().disable()
                    //.build();
        }
    }

    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class SecurityConfig extends VaadinWebSecurity
    {
        private final AuthenticationProvider authenticationProvider;

        @Override
        protected void configure(HttpSecurity http) throws Exception
        {
            super.configure(http);

            setLoginView(http, LoginView.class);

        }

        @Override
        protected void configure(WebSecurity web) throws Exception
        {
            super.configure(web);
        }
    }
}
