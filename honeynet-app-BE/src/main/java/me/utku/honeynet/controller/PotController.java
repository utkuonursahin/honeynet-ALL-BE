package me.utku.honeynet.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
  public GenericResponse<List<Pot>> getPots(
      @RequestParam(required = false) String firmId,
      @AuthenticationPrincipal CustomUserDetails userDetails, HttpSession session){
    List<Pot> pots = potService.getAll(firmId,userDetails, session);
    return GenericResponse.<List<Pot>>builder().data(pots).statusCode(200).build();
  }

  @GetMapping("/{id}")
    public GenericResponse<Pot> getPot(@PathVariable String id){
    Pot pot = potService.get(id);
    return GenericResponse.<Pot>builder().data(pot).build();
  }

  @GetMapping("/image/{id}")
  public byte[] getPotImage(@PathVariable String id){
    return potService.getImage(id);
  }

  @GetMapping("/phishing-email")
  public GenericResponse<List<EmailListener>> getEmailListener(){
    List<EmailListener> emailListeners = restService.forwardGetALlEmailListeners();
    return GenericResponse.<List<EmailListener>>builder().data(emailListeners).build();
  }

  @PostMapping
  public GenericResponse<Pot> createPot(@RequestBody Pot newPot){
    Pot pot = potService.create(newPot);
    return GenericResponse.<Pot>builder().data(pot).build();
  }

  @PostMapping("/setup")
  public GenericResponse<Boolean> setupPot(@RequestParam String potId,@RequestParam String firmId){
    Boolean result = potService.setup(potId, firmId);
    return GenericResponse.<Boolean>builder().data(result).build();
  }


  @PostMapping("/phishing-email")
  public GenericResponse<EmailListener> createEmailListener(@RequestBody EmailSetupRequest emailSetupRequest){
    EmailListener emailListener = restService.forwardCreateEmailListener(emailSetupRequest);
    return GenericResponse.<EmailListener>builder().data(emailListener).build();
  }

  @PatchMapping("/{id}")
  public GenericResponse<Pot> updatePot(@PathVariable String id, @RequestBody Pot reqBody){
    Pot pot = potService.update(id,reqBody);
    return GenericResponse.<Pot>builder().data(pot).build();
  }

  @PatchMapping("/phishing-email/{id}")
  public GenericResponse<EmailListener> updateEmailListener(@PathVariable String id, @RequestBody EmailListener updatePart){
    EmailListener emailListener = restService.forwardUpdateEmailListener(id,updatePart);
    return GenericResponse.<EmailListener>builder().data(emailListener).build();
  }

  @DeleteMapping("/{id}")
  public GenericResponse<Boolean> deletePot(@PathVariable String id){
    boolean result = potService.delete(id);
    return GenericResponse.<Boolean>builder().data(result).build();
  }

  @DeleteMapping("/phishing-email/{id}")
  public GenericResponse<Boolean> deleteEmailListener(@PathVariable String id){
    Boolean result = restService.forwardDeleteEmailListener(id);
    return GenericResponse.<Boolean>builder().data(result).build();
  }
}