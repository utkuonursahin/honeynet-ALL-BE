package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.EmailInfoFilter;
import me.utku.honeynet.dto.PaginatedEmailInfos;
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

    public PaginatedEmailInfos getAllEmailInfos(int page, int size){
        try{
            Pageable pageable = PageRequest.of(page,size);
            Page<EmailInfo> infos = emailInfoRepository.findAll(pageable);
            return createPaginatedEmailInfos(infos,page,size);
        }catch (Exception exception){
            log.error("Paginated get Email Info exception : {}",exception.getMessage());
        }return null;
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
//            emailInfos = emailInfoRepository.findAllByEmailReceiverAndEmailDateBetween(
//                emailInfoFilter.getReceiverFilter(),
//                emailInfoFilter.getDateFilters()[0],
//                emailInfoFilter.getDateFilters()[1],
//                pageable
//            );
            emailInfos = emailInfoRepository.findAllByEmailReceiver(
                    emailInfoFilter.getReceiverFilter(),
                    pageable
            );
            return createPaginatedEmailInfos(emailInfos, page, size);


        }catch (Exception exception){
            log.error("EmailInfo service filterEmails exception: {}", exception.getMessage());
        }
        return null;
    }





    public EmailInfo create(EmailInfo emailInfo){//revision needed
        EmailInfo email = new EmailInfo();
        try {
            emailInfo.setId(UUID.randomUUID().toString());//check later
            email = emailInfoRepository.save(emailInfo);
            log.info("New email has created.");
        }catch (Exception exception){
            log.error("EmailInfo service exception : {}",exception.getMessage());
        }return email;
    }
    //without paging
    public List<EmailInfo> getAll(){
        List<EmailInfo> emails = new ArrayList<>();
        try{
            emails =  emailInfoRepository.findAll();
        }catch (Exception exception){
            log.error("EmailInfo service getAll exception: {}", exception.getMessage());
        }
        return emails;
    }

    public EmailInfo get(String id){
        EmailInfo email = new EmailInfo();
        try{
            email = emailInfoRepository.findEmailById(id);
        }catch (Exception exception){
            log.error("EmailInfo service get exception: {}", exception.getMessage());
        }
        return email;
    }
//    public List<EmailInfo> getByReceiver(String receiver){
//        List<EmailInfo> emails = new ArrayList<>();
//      try{
//          emails  = emailInfoRepository.findAllByEmailReceiverAndEmailDateBetween();
//      }catch (Exception exception){
//          log.error("EmailInfo service get by receiver exception : {}",exception.getMessage());
//      }
//      return emails;
//    };

    public EmailInfo update( String id, EmailInfo updatedEmail){
        EmailInfo email = new EmailInfo();
        try{
            if (emailInfoRepository.existsById(id)){
                email = emailInfoRepository.save(updatedEmail);
                email.setId(id);
                log.info("Email has been updated");
            }else {log.info("There is no email with id : {}",id);}
        }catch (Exception exception){
            log.error("EmailInfo service update expression : {}",exception.getMessage());
        }
        return email;
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
    public boolean deleteAll(){
        boolean isDeleted = false;
        try {
            emailInfoRepository.deleteAll();
            isDeleted = true;
            log.info("All emails have been deleted.");
        }catch (Exception exception){
            log.error("EmailInfo service deleteAll exception : {}",exception.getMessage());
        }
        return isDeleted;
    }

}
