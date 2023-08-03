package me.utku.emailhoneypot.service;

import com.sun.mail.imap.IMAPFolder;
import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;

import jakarta.mail.search.FlagTerm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.emailhoneypot.dto.EmailSetupRequest;
import me.utku.emailhoneypot.enums.EmailListenerStatus;
import me.utku.emailhoneypot.dto.EmailContent;
import me.utku.emailhoneypot.model.EmailListener;
import me.utku.emailhoneypot.repository.EmailListenerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class EmailListenerService extends MessageCountAdapter {
    private final RestService restService;
    private final JWTService jwtService;
    private final EmailListenerRepository emailListenerRepository;

    @Value("${be.firmId}")
    private String firmId;

    public List<EmailListener> getAllByFirmRef(HttpServletRequest httpServletRequest) {
        List<EmailListener> emailListeners = new ArrayList<>();
        String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
        try {
            if (authToken != null && jwtService.validateJWT(authToken)) {
                emailListeners = emailListenerRepository.findAllByFirmRef(firmId);
                emailListeners.stream().forEach(emailListener -> emailListener
                    .setPassword(null)
                    .setFirmRef(null)
                );
            }
        } catch (Exception error) {
            log.error("EmailListener service get all exception: {}", error.getMessage());
        }
        return emailListeners;
    }

    public EmailListener getById(String id) {
        try {
            return emailListenerRepository.findById(id).orElse(null);
        } catch (Exception error) {
            log.error("EmailListener service get by id exception: {}", error.getMessage());
            return null;
        }
    }

    public EmailListener create(EmailSetupRequest emailSetupRequest, HttpServletRequest httpServletRequest) {
        EmailListener emailListener = new EmailListener();
        String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
        if (authToken != null && jwtService.validateJWT(authToken)) {
            emailListener.setEmail(emailSetupRequest.getEmail());
            emailListener.setPassword(KeyService.encrypt(emailSetupRequest.getPassword()));
            emailListener.setStatus(EmailListenerStatus.LISTEN);
            emailListener.setFirmRef(firmId);
            if(emailListenerRepository.existsByEmail(emailListener.getEmail())){
                return emailListenerRepository.findByEmail(emailListener.getEmail());
            }
            emailListener.setId(UUID.randomUUID().toString());
            emailListener = emailListenerRepository.save(emailListener);
        }
        return emailListener;
    }

    @Scheduled(fixedRate = 1000 * 10 * 10)
    public void checkEmails() {
        for (EmailListener emailListener : emailListenerRepository.findAllByFirmRef(firmId)) {
            System.out.println("Email listener: " + emailListener);
            if (emailListener != null && emailListener.getStatus().equals(EmailListenerStatus.LISTEN)) {
                try {
                    Properties props = new Properties();
                    props.setProperty("mail.store.protocol", "imaps");
                    props.setProperty("mail.imaps.host", "imap.gmail.com");
                    props.setProperty("mail.imaps.port", "993");
                    Session session = Session.getInstance(props);

                    Store store = session.getStore("imaps");
                    store.connect(emailListener.getEmail(), KeyService.decrypt(emailListener.getPassword()));

                    IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
                    inbox.open(Folder.READ_WRITE);
                    Flags seen = new Flags(Flags.Flag.SEEN);
                    FlagTerm unseenFlagTerm = new FlagTerm(seen, false);
                    Message[] messages = inbox.search(unseenFlagTerm);
                    Arrays.stream(messages).forEach(message -> {
                        try {
                            EmailContent emailContent = new EmailContent(message.getFrom()[0].toString(), message.getSentDate(), message.getSubject());
                            restService.postSuspiciousActivity(emailContent);
                            message.setFlag(Flags.Flag.SEEN, true);
                        } catch (Exception e) {
                            log.error("EmailListener service checkMails messages stream exception: {}", e.getMessage());
                        }
                    });
                    inbox.close(true);
                    store.close();
                } catch (MessagingException error) {
                    log.error("EmailListener service checkMails exception: {}", error.getMessage());
                }
            }
        }
    }

    public EmailListener update(String id, EmailListener emailListener, HttpServletRequest httpServletRequest) {
        EmailListener existListener = new EmailListener();
        try {
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if (authToken != null && jwtService.validateJWT(authToken)) {
                existListener = emailListenerRepository.findById(id).orElse(null);
                if (existListener == null) throw new Exception("EmailListener not found");
                if(emailListener.getStatus() != null) existListener.setStatus(emailListener.getStatus());
                existListener = emailListenerRepository.save(existListener);
            }
        } catch (Exception error) {
            log.error("EmailListener service update exception: {}", error.getMessage());
        }
        return emailListener;
    }

    public Boolean delete(String id, HttpServletRequest httpServletRequest) {
        try {
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if (authToken != null && jwtService.validateJWT(authToken)) {
                emailListenerRepository.deleteById(id);
                return true;
            }
        } catch (Exception error) {
            log.error("EmailListener service delete exception: {}", error.getMessage());
        }
        return false;
    }
}
