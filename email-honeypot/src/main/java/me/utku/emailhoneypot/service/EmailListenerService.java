package me.utku.emailhoneypot.service;

import jakarta.mail.*;
import jakarta.mail.event.MessageCountAdapter;

import com.sun.mail.imap.IMAPFolder;
import jakarta.mail.search.FlagTerm;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.emailhoneypot.model.EmailContent;
import me.utku.emailhoneypot.dto.EmailSetupRequest;
import me.utku.emailhoneypot.enums.ListenerStatus;
import me.utku.emailhoneypot.model.EmailListener;
import me.utku.emailhoneypot.repository.EmailListenerRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class EmailListenerService extends MessageCountAdapter {
    private final RestService restService;
    private final JWTService jwtService;
    private final EmailListenerRepository emailListenerRepository;

    public List<EmailListener> getAll(){
        List<EmailListener> emailListeners = new ArrayList<>();
        try{
            emailListeners = emailListenerRepository.findAll();
        }catch (Exception error){
            log.error("EmailListener service get all exception: {}", error.getMessage());
        }
        return emailListeners;
    }

    public EmailListener getById(String id){
        try{
            return emailListenerRepository.findById(id).orElse(null);
        }catch (Exception error){
            log.error("EmailListener service get by id exception: {}", error.getMessage());
            return null;
        }
    }

    public EmailListener create(EmailSetupRequest emailSetupRequest, HttpServletRequest httpServletRequest){
        EmailListener emailListener = new EmailListener();

        String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
        if(authToken != null && jwtService.validateJWT(authToken)){
            Properties props = new Properties();
            props.setProperty("mail.store.protocol", "imaps");
            props.setProperty("mail.imaps.host", "imap.gmail.com");
            props.setProperty("mail.imaps.port", "993");
            try{
                emailListener.setUsername(emailSetupRequest.getEmail());
                emailListener.setPassword(emailSetupRequest.getPassword());
                emailListener.setSession(Session.getInstance(props));
                emailListener.setStatus(ListenerStatus.LISTEN);
                this.sendUnseenMessages(emailListener);
                emailListener = emailListenerRepository.save(emailListener);
            }catch (Exception error){
                log.error("EmailListener service create exception: {}", error.getMessage());
            }
        }
        return emailListener;
    }

    //NOT COMPLETED
    public EmailListener update(EmailListener emailListener){
        try{
            return emailListenerRepository.save(emailListener);
        }catch (Exception error){
            log.error("EmailListener service update exception: {}", error.getMessage());
            return null;
        }
    }

    public boolean delete(String id){
        try{
            emailListenerRepository.deleteById(id);
            return true;
        }catch (Exception error){
            log.error("EmailListener service delete exception: {}", error.getMessage());
            return false;
        }
    }

    @Scheduled(fixedRate = 10000)
    @Async
    public void sendUnseenMessages(EmailListener emailListener){
        try{
            Store store = emailListener.getSession().getStore("imaps");
            store.connect(emailListener.getUsername(), emailListener.getPassword());

            IMAPFolder inbox = (IMAPFolder)store.getFolder("INBOX");
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
                    e.printStackTrace();
                }
            });
            inbox.close(true);
            store.close();
        } catch (MessagingException error) {
            log.error("EmailListener service writeUnseenMessagesToDb exception: {}", error.getMessage());
        }
    }

    /*public void startListening(EmailListener emailListener, HttpServletRequest httpServletRequest) {
        try{
            String authToken = httpServletRequest.getHeader("In-App-Auth-Token");
            if(authToken != null && jwtService.validateJWT(authToken)){
                Store store = emailListener.getSession().getStore("imaps");
                store.connect(emailListener.getUsername(), emailListener.getPassword());

                IMAPFolder inbox = (IMAPFolder)store.getFolder("INBOX");
                inbox.open(Folder.READ_WRITE);

                Thread keepAliveThread = new Thread(new KeepAliveRunnableService(inbox), "IdleConnectionKeepAlive");
                keepAliveThread.start();

                inbox.addMessageCountListener(new MessageCountAdapter() {
                    @Override
                    public void messagesAdded(MessageCountEvent event) {
                        Message[] messages = event.getMessages();
                        for (Message message : messages) {
                            try {
                                EmailContent email = new EmailContent(message.getFrom()[0].toString(), message.getSentDate(), message.getSubject());
                                restService.postSuspiciousActivity(email);
                            } catch (MessagingException e) {
                                log.error("Error email listener service: {}", e.getMessage());
                            }
                        }
                    }
                });

                // Start the IDLE Loop
                while (!Thread.interrupted()) {
                    try {
                        System.out.println("Starting IDLE");
                        inbox.idle();
                    } catch (MessagingException e) {
                        log.error("Messaging error email listener service idle loop: {}", e.getMessage());
                    }
                }

                // Interrupt and shutdown the keep-alive thread
                if (keepAliveThread.isAlive()) {
                    keepAliveThread.interrupt();
                }
            }
        } catch (MessagingException error) {
            log.error("Error email listener service: {}", error.getMessage());
        }
    }*/
}
