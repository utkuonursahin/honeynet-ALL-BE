package me.utku.webThreatsHoneypotBE.controller;

import lombok.RequiredArgsConstructor;
import me.utku.webThreatsHoneypotBE.dto.GenericResponse;
import me.utku.webThreatsHoneypotBE.model.BruteForceRequest;
import me.utku.webThreatsHoneypotBE.service.BruteForceRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brute-force-request")
@RequiredArgsConstructor
public class BruteForceRequestController {
    private final BruteForceRequestService bruteForceRequestService;
    @GetMapping()
    public GenericResponse<List<BruteForceRequest>> getAllBruteForceRequests(){
        List<BruteForceRequest> bruteForceRequests = bruteForceRequestService.getAll();
        return GenericResponse.<List<BruteForceRequest>>builder().data(bruteForceRequests).build();
    }
    @GetMapping("/{id}")
    public GenericResponse<BruteForceRequest> getBruteForceRequest(@PathVariable String id){
        BruteForceRequest bruteForceRequest = bruteForceRequestService.getById(id);
        return GenericResponse.<BruteForceRequest>builder().data(bruteForceRequest).build();
    }
    @PostMapping()
    public GenericResponse<BruteForceRequest> createBruteForceRequest(@RequestBody BruteForceRequest reqBody){
        BruteForceRequest bruteForceRequest = bruteForceRequestService.create(reqBody);
        return GenericResponse.<BruteForceRequest>builder().data(bruteForceRequest).build();
    }
    @PatchMapping("/{id}")
    public GenericResponse<BruteForceRequest> updateBruteForceRequest(@PathVariable String id, @RequestBody BruteForceRequest reqBody){
        BruteForceRequest bruteForceRequest = bruteForceRequestService.update(id,reqBody);
        return GenericResponse.<BruteForceRequest>builder().data(bruteForceRequest).build();
    }
    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deleteBruteForceRequest(@PathVariable String id){
        boolean result = bruteForceRequestService.delete(id);
        return GenericResponse.<Boolean>builder().data(result).build();
    }
}