package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.model.Firm;
import me.utku.honeynet.service.FirmService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/firm")
@RequiredArgsConstructor
public class FirmController {
    private final FirmService firmService;
    @GetMapping
    public GenericResponse<List<Firm>> getAllFirms() {
        List<Firm> firms = firmService.getAll();
        return GenericResponse.<List<Firm>>builder().data(firms).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<Firm> getFirm(@PathVariable String id) {
        Firm firm = firmService.get(id);
        return GenericResponse.<Firm>builder().data(firm).statusCode(200).build();
    }

    @GetMapping("/image/{id}")
    public byte[] getFirmImage(@PathVariable String id){
        return firmService.getImage(id);
    }

    @PostMapping
    public GenericResponse<Firm> createFirm(@RequestBody Firm newFirm) {
        Firm firm = firmService.create(newFirm);
        return GenericResponse.<Firm>builder().data(firm).statusCode(200).build();
    }

    @PatchMapping("/{id}")
    public GenericResponse<Firm> updateFirm(@PathVariable String id, @RequestBody Firm reqBody) {
        Firm firm = firmService.update(id, reqBody);
        return GenericResponse.<Firm>builder().data(firm).statusCode(200).build();
    }

    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deleteFirm(@PathVariable String id) {
        boolean result = firmService.delete(id);
        return GenericResponse.<Boolean>builder().data(result).statusCode(200).build();
    }
}
