package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.EmailSetupRequest;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.service.PotService;
import me.utku.honeynet.service.RestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pot")
@RequiredArgsConstructor
public class PotController {
  private final PotService potService;
  private final RestService restService;

  @GetMapping
  public GenericResponse<List<Pot>> getPots() {
    List<Pot> pots = potService.getAll();
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

  @PostMapping
  public GenericResponse<Pot> createPot(@RequestBody Pot newPot){
    Pot pot = potService.create(newPot);
    return GenericResponse.<Pot>builder().data(pot).build();
  }

  @PostMapping("/email/setup")
  public GenericResponse<EmailListenerResponse> setupEmailListener(@RequestBody EmailSetupRequest emailSetupRequest){
    EmailListenerResponse emailListenerResponse = restService.forwardEmailListenerSetupReq(emailSetupRequest);
    return GenericResponse.<EmailListenerResponse>builder().data(emailListenerResponse).build();
  }

  @PatchMapping("/{id}")
  public GenericResponse<Pot> updatePot(@PathVariable String id, @RequestBody Pot reqBody){
    Pot pot = potService.update(id,reqBody);
    return GenericResponse.<Pot>builder().data(pot).build();
  }

  @DeleteMapping("/{id}")
  public GenericResponse<Boolean> deletePot(@PathVariable String id){
    boolean result = potService.delete(id);
    return GenericResponse.<Boolean>builder().data(result).build();
  }
}