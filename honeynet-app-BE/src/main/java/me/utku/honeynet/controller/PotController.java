package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.clone.CloneRequest;
import me.utku.honeynet.dto.clone.CloneResponse;
import me.utku.honeynet.dto.email.EmailListener;
import me.utku.honeynet.dto.email.EmailSetupRequest;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInstance;
import me.utku.honeynet.service.PotService;
import me.utku.honeynet.service.RestService;
import me.utku.honeynet.service.ServerInstanceService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
@Slf4j
@RestController
@RequestMapping("/pot")
@RequiredArgsConstructor
public class PotController {
    private final PotService potService;
    private final RestService restService;
    private final ServerInstanceService serverInstanceService;
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
        List<EmailListener> emailListeners = restService.forwardGetAllEmailListeners(serverInstanceService.findServerUrl(potId, userDetails.getFirmRef()));
        return GenericResponse.<List<EmailListener>>builder().data(emailListeners).build();
    }

    @PostMapping
    public GenericResponse<Pot> createPot(@RequestBody Pot newPot) {
        Pot pot = potService.create(newPot);
        return GenericResponse.<Pot>builder().data(pot).build();
    }

    @PostMapping("/phishing-email")
    public GenericResponse<EmailListener> createEmailListener(
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody EmailSetupRequest emailSetupRequest) {
        EmailListener emailListener = restService.forwardCreateEmailListener(serverInstanceService.findServerUrl(potId,userDetails.getFirmRef()), emailSetupRequest);
        return GenericResponse.<EmailListener>builder().data(emailListener).build();
    }

    @PostMapping("/web-clone")
    public GenericResponse<CloneResponse> createEmailListener(
        @RequestParam String potId,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody CloneRequest cloneRequest) {
        CloneResponse cloneResponse = null;
        try{
            cloneResponse = restService.forwardCloneSite(cloneRequest.cloneUrl(), serverInstanceService.findServerUrl(potId, userDetails.getFirmRef()));
            ServerInstance serverInstance = serverInstanceService.getByPotIdAndFirmId(potId,userDetails.getFirmRef());
            serverInstanceService.shutdown(serverInstance.getId());
            Thread.sleep(100);
            serverInstanceService.extractJar(new File("").getAbsoluteFile().getParent()+"\\clone-honeypot-BE");
            serverInstanceService.start(serverInstance.getId());
        } catch (Exception error){
            log.error("Error while cloning site: {}", error.getMessage());
        }
        return GenericResponse.<CloneResponse>builder().data(cloneResponse).build();
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
        EmailListener emailListener = restService.forwardUpdateEmailListener(id, serverInstanceService.findServerUrl(potId, userDetails.getFirmRef()), updatePart);
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
        Boolean result = restService.forwardDeleteEmailListener(id, serverInstanceService.findServerUrl(potId, userDetails.getFirmRef()));
        return GenericResponse.<Boolean>builder().data(result).build();
    }
}