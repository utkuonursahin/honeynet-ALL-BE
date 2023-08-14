package me.utku.honeynet.repository;

import com.mongodb.lang.Nullable;
import me.utku.honeynet.model.EmailInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;


public interface EmailInfoRepository  extends MongoRepository<EmailInfo,String> {

    EmailInfo findEmailById(String id);

    @Query
    Page<EmailInfo> findAllByEmailReceiverContains(String receiver, Pageable pageable);

    @Query
    Page<EmailInfo> findAllByEmailReceiverAndEmailDateBetween(String receiver,LocalDateTime start, LocalDateTime end,Pageable pageable);

}
