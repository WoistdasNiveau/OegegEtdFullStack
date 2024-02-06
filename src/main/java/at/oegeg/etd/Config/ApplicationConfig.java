package at.oegeg.etd.Config;

import at.oegeg.etd.Repositories.IUserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig
{
    private final IUserEntityRepository _userRepository;

    @Bean
    public UserDetailsService userDetailsService()
    {
        return username ->
                _userRepository.findByEmailOrTelephoneNumberOrNameOrIdentifier(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                //_userRepository.findByIdentifier(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvier = new DaoAuthenticationProvider();
        authProvier.setUserDetailsService(userDetailsService());
        authProvier.setPasswordEncoder(CustomPasswordEncoder());
        return authProvier;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder CustomPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
