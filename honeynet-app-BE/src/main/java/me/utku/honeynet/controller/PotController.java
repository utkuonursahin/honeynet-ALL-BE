package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.EmailListener;
import me.utku.honeynet.dto.EmailSetupRequest;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.service.PotService;
import me.utku.honeynet.service.RestService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pot")
@RequiredArgsConstructor
public class PotController {
    private final PotService potService;
    private final RestService restService;

    @GetMapping()
    public GenericResponse<List<Pot>> getPots() {
        List<Pot> pots = potService.getAll();
        return GenericResponse.<List<Pot>>builder().data(pots).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<Pot> getPot(@PathVariable String id) {
        Pot pot = potService.get(id);
        return GenericResponse.<Pot>builder().data(pot).build();
    }

    @GetMapping("/image/{id}")
    public byte[] getPotImage(@PathVariable String id) {
        return potService.getImage(id);
    }

    @GetMapping("/phishing-email")
    public GenericResponse<List<EmailListener>> getEmailListener(
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<EmailListener> emailListeners = restService.forwardGetAllEmailListeners(potId, userDetails.getFirmRef());
        return GenericResponse.<List<EmailListener>>builder().data(emailListeners).build();
    }

    @PostMapping
    public GenericResponse<Pot> createPot(@RequestBody Pot newPot) {
        Pot pot = potService.create(newPot);
        return GenericResponse.<Pot>builder().data(pot).build();
    }

    @PostMapping("/setup")
    public GenericResponse<Boolean> setupPot(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String potId) {
        Boolean result = potService.setup(potId, userDetails.getFirmRef());
        return GenericResponse.<Boolean>builder().data(result).build();
    }


    @PostMapping("/phishing-email")
    public GenericResponse<EmailListener> createEmailListener(
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody EmailSetupRequest emailSetupRequest) {
        EmailListener emailListener = restService.forwardCreateEmailListener(potId, userDetails.getFirmRef(), emailSetupRequest);
        return GenericResponse.<EmailListener>builder().data(emailListener).build();
    }

    @PatchMapping("/{id}")
    public GenericResponse<Pot> updatePot(@PathVariable String id, @RequestBody Pot reqBody) {
        Pot pot = potService.update(id, reqBody);
        return GenericResponse.<Pot>builder().data(pot).build();
    }

    @PatchMapping("/phishing-email/{id}")
    public GenericResponse<EmailListener> updateEmailListener(
        @PathVariable String id,
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody EmailListener updatePart) {
        EmailListener emailListener = restService.forwardUpdateEmailListener(id, potId, userDetails.getFirmRef(), updatePart);
        return GenericResponse.<EmailListener>builder().data(emailListener).build();
    }

    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deletePot(@PathVariable String id) {
        boolean result = potService.delete(id);
        return GenericResponse.<Boolean>builder().data(result).build();
    }

    @DeleteMapping("/phishing-email/{id}")
    public GenericResponse<Boolean> deleteEmailListener(
        @PathVariable String id,
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails){
        Boolean result = restService.forwardDeleteEmailListener(id,potId,userDetails.getFirmRef());
        return GenericResponse.<Boolean>builder().data(result).build();
    }
}