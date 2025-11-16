package cm.bogne_stanley.money_flow.common.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public void sendActivationCode(String email, String code){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("noreply@moneyflow.cm");
        message.setSubject("Votre code d'activation");
        message.setText("Bonjour,\n\nVotre code d'activation est : " + code + "\n\nMerci d'utiliser notre service.");
        mailSender.send(message);
    }
}
