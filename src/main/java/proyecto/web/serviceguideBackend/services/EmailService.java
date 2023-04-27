package proyecto.web.serviceguideBackend.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    TemplateEngine templateEngine;

    public void sendEmail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("serviceguide23@gmail.com");
        message.setTo("serviceguide23@gmail.com");
        message.setSubject("Prueba envio email");
        message.setText("Esto es el email");

        javaMailSender.send(message);
    }

    public void sendEmailTemplate() {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            Context context = new Context();
            String htmlText = templateEngine.process("email-template", context);
            helper.setFrom("serviceguide23@gmail.com");
            helper.setTo("serviceguide23@gmail.com");
            helper.setSubject("Prueba envio email");
            helper.setText(htmlText, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
