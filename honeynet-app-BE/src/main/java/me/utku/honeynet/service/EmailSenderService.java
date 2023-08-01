package me.utku.honeynet.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import jakarta.mail.*;

import java.util.Date;
import java.util.Properties;

@Slf4j
@Service
public class EmailSenderService {
    private Session session;
    private String from = "fakemployeebeam@gmail.com";

    public void init (){
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Authenticator auth = new Authenticator() {
            @Override
            protected  PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(from,"axchjcpceerkvxcl");
            }
        };
         session = Session.getInstance(props, auth);
    }

    public void sendEmail(String to, String subject){
        try{
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(from, "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse(from, false));

            msg.setSubject(subject, "UTF-8");

            msg.setContent("This is a test", "text/html; charset=utf-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            Transport.send(msg);
        }catch (Exception error){
            log.error("EmailSenderService sendEmail exception {}", error.getMessage());
        }
    }
}
