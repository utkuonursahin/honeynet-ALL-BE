package me.utku.honeynet.repository;

import me.utku.honeynet.model.EmailInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import java.time.LocalDateTime;


public interface EmailInfoRepository  extends MongoRepository<EmailInfo,String> {

    @Query
    Page<EmailInfo> findAllByEmailReceiverContainsAndEmailDateBetween(String receiver, LocalDateTime start, LocalDateTime end, Pageable pageable);
}
