package proyecto.web.serviceguideBackend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import proyecto.web.serviceguideBackend.services.EmailService;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    EmailService emailService;

    @GetMapping("/send")
    public ResponseEntity<?> sendEmail() {
        emailService.sendEmail();
        return ResponseEntity.ok("Correo enviado");
    }

    @GetMapping("/send-html")
    public ResponseEntity<?> sendEmailTemplate() {
        emailService.sendEmailTemplate();
        return ResponseEntity.ok("Correo con plantilla enviado");
    }
}
