package at.oegeg.etd.Services.Implementations;

import at.oegeg.etd.DataTransferObjects.DisplayModels.UserDisplay;
import at.oegeg.etd.Entities.UserEntity;
import at.oegeg.etd.views.UserDetailsView;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static at.oegeg.etd.Constants.Constants.SENDERMAIL;


@Service
@RequiredArgsConstructor
public class EmailSenderService
{
    private final JavaMailSender _javaMailSender;

    public void SendSetPasswortMail(String email, String token, String name)
    {
        SimpleMailMessage message = new SimpleMailMessage();
        String body = "Dear " + name + "!" + System.lineSeparator() + System.lineSeparator() +
                "Your Group Leader has successfully created your OegegEtd Account. Please use following link to choose a " +
                "password and activate your account." + System.lineSeparator() + System.lineSeparator() +
                "http://localhost:8081/init/" + token;

        message.setFrom(SENDERMAIL);
        message.setTo(email);
        message.setSubject("Account Creation | OegegEtd");
        message.setText(body);

        _javaMailSender.send(message);
    }

    public void ChangePasswordMail(String email, String token, String name)
    {
        SimpleMailMessage message = new SimpleMailMessage();

        String body = "Dear " + name + "!" + System.lineSeparator() + System.lineSeparator() +
        "If you have requested to change your password, please follow the link below. If you did not request to change your password," +
                " ignore this email!" + System.lineSeparator() + System.lineSeparator() +
                "http://localhost:8081/init/" + token;

        message.setFrom(SENDERMAIL);
        message.setTo(email);
        message.setSubject("Password Reset | OegegEtd");
        message.setText(body);

        _javaMailSender.send(message);
    }
}
















