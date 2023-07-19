package me.utku.emailhoneypot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.emailhoneypot.model.EmailListener;
import me.utku.emailhoneypot.dto.EmailSetupRequest;
import me.utku.emailhoneypot.service.EmailListenerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/email-listener")
@RequiredArgsConstructor

public class EmailListenerController {
    private final EmailListenerService emailListenerService;

    @GetMapping
    public List<EmailListener> getEmailListeners(HttpServletRequest httpServletRequest){
        return emailListenerService.getAll(httpServletRequest);
    }

    @PostMapping
    public EmailListener setupEmailListener(@RequestBody EmailSetupRequest emailSetupRequest, HttpServletRequest httpServletRequest){
        EmailListener emailListener = emailListenerService.create(emailSetupRequest,httpServletRequest);
        emailListener.setPassword(null);
        return emailListener;
    }

    @DeleteMapping("/{id}")
    public Boolean deleteEmailListener(@PathVariable String id, HttpServletRequest httpServletRequest){
        return emailListenerService.delete(id,httpServletRequest);
    }
}
