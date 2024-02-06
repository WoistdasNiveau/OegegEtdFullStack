package at.oegeg.etd.Config;

import at.oegeg.etd.Repositories.IUserEntityRepository;
import at.oegeg.etd.views.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

//@EnableWebSecurity
//@Configuration
//@RequiredArgsConstructor
//public class SecurityConfig extends VaadinWebSecurity
//{
//    private final AuthenticationProvider authenticationProvider;
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception
//    {
//        http.authorizeHttpRequests(auth -> auth.requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated());
//        http.csrf().ignoringRequestMatchers("/api/**");
//
//        super.configure(http);
//
//        setLoginView(http, LoginView.class);
//
//    }
//
//    @Override
//    protected void configure(WebSecurity web) throws Exception
//    {
//        super.configure(web);
//    }
//
//}
