package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.email.EmailInfoFilter;
import me.utku.honeynet.dto.email.PaginatedEmailInfos;
import me.utku.honeynet.model.EmailInfo;
import me.utku.honeynet.repository.EmailInfoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailInfoService {
    private final EmailInfoRepository emailInfoRepository;

    public PaginatedEmailInfos createPaginatedEmailInfos(Page<EmailInfo> emailInfos, int page, int size){
        PaginatedEmailInfos paginatedEmailInfos = new PaginatedEmailInfos();
        paginatedEmailInfos.setEmailInfoList(emailInfos.getContent());
        paginatedEmailInfos.setCurrentSize(Long.valueOf(size));
        paginatedEmailInfos.setCurrentPage(Long.valueOf(page));
        paginatedEmailInfos.setTotalSize(Long.valueOf(emailInfos.getTotalElements()));
        paginatedEmailInfos.setTotalPage(Long.valueOf(emailInfos.getTotalPages()));
        return paginatedEmailInfos;
    }
    public PaginatedEmailInfos filterEmails(EmailInfoFilter emailInfoFilter, int page, int size) {
        try {
            if (emailInfoFilter.getDateFilters().length != 2) {
                emailInfoFilter.setDateFilters(new LocalDateTime[]{
                        LocalDateTime.of(2023, Month.JULY, 01, 00, 00),
                        LocalDateTime.now()
                });
            }
            if (emailInfoFilter.getReceiverFilter() == null){
                emailInfoFilter.setReceiverFilter("mcayhan6006@gmail.com");
            }
            System.out.println(emailInfoFilter);
            Pageable pageable = PageRequest.of(page,size);
            Page<EmailInfo> emailInfos = null;
            emailInfos = emailInfoRepository.findAllByEmailReceiverContainsAndEmailDateBetween(
                    emailInfoFilter.getReceiverFilter(),
                    emailInfoFilter.getDateFilters()[0],
                    emailInfoFilter.getDateFilters()[1],
                    pageable
            );
            return createPaginatedEmailInfos(emailInfos, page, size);


        }catch (Exception exception){
            log.error("EmailInfo service filterEmails exception: {}", exception.getMessage());
        }
        return null;
    }

    public EmailInfo create(EmailInfo emailInfo){
        EmailInfo email = new EmailInfo();
        try {
            emailInfo.setId(UUID.randomUUID().toString());
            email = emailInfoRepository.save(emailInfo);
            log.info("New email has created.");
        }catch (Exception exception){
            log.error("EmailInfo service exception : {}",exception.getMessage());
        }return email;
    }

    public boolean delete(String id){
        boolean isDeleted = false;
        try{
            if (emailInfoRepository.existsById(id)){
                emailInfoRepository.deleteById(id);
                log.info("Email with id : {} deleted",id);
                isDeleted = true;
            }else log.info("Email with id : {} couldn't find", id);
        }catch (Exception exception){
            log.error("EmailInfo service deleteById exception : {}",exception.getMessage());
        }
        return isDeleted;
    }


}
