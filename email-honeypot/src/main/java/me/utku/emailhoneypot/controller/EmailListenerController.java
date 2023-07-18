package me.utku.emailhoneypot.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.utku.emailhoneypot.model.EmailListener;
import me.utku.emailhoneypot.dto.EmailSetupRequest;
import me.utku.emailhoneypot.dto.GenericResponse;
import me.utku.emailhoneypot.service.EmailListenerService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/email")
@RequiredArgsConstructor

public class EmailListenerController {
    private final EmailListenerService emailListenerService;

    @PostMapping
    public GenericResponse<EmailListener> setupEmailListener(@RequestBody EmailSetupRequest emailSetupRequest, HttpServletRequest httpServletRequest){
        EmailListener emailListener = emailListenerService.create(emailSetupRequest,httpServletRequest);
        return GenericResponse.<EmailListener>builder().data(emailListener).statusCode(200).build();
    }
}
