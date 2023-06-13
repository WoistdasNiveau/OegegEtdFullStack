package at.oegeg.etd.Configs;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@TestConfiguration
public class TestConfig
{
    @Bean
    public JavaMailSender javaMailSender()
    {
        return new JavaMailSenderImpl();
    }

    @Bean
    public PasswordEncoder CustomPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
}
