package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.email.EmailInfoFilter;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.email.PaginatedEmailInfos;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.EmailInfo;
import me.utku.honeynet.service.EmailInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/email-info")
@RequiredArgsConstructor
public class EmailInfoController {
    private final EmailInfoService emailInfoService;
    @PostMapping("/filter")
    public GenericResponse<PaginatedEmailInfos> getAllFilteredEmails(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestBody EmailInfoFilter emailInfoFilter
    ){
        PaginatedEmailInfos emailInfos = emailInfoService.filterEmails(emailInfoFilter,page,size);
        return GenericResponse.<PaginatedEmailInfos>builder().data(emailInfos).statusCode(200).build();
    }

    @PostMapping
    public GenericResponse<EmailInfo> createEmail(@RequestBody EmailInfo newEmail){
        EmailInfo email = emailInfoService.create(newEmail);
        return GenericResponse.<EmailInfo>builder().data(email).statusCode(200).build();
    }
    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deleteEmail(@PathVariable(name = "id") String id){
        Boolean isDeleted = emailInfoService.delete(id);
        return GenericResponse.<Boolean>builder().data(isDeleted).statusCode(200).build();
    }
}





