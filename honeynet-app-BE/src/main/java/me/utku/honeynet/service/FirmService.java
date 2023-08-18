package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.model.Firm;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.repository.FirmRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirmService {
    private final FirmRepository firmRepository;

    public List<Firm> getAll(){
        List<Firm> firms = new ArrayList<>();
        try{
            firms = firmRepository.findAll();
        }catch (Exception exception) {
            log.error("Exception occurs in get all operation of FirmService : {}", exception.getMessage());
        }
        return firms;
    }

    public Firm get(String id){
        Firm firm = new Firm();
        try{
            firm = firmRepository.findById(id).orElse(null);
        }catch (Exception error){
            log.error("Exception occurs in get operation of FirmService: {}", error.getMessage());
        }
        return firm;
    }

    public byte[] getImage(String id){
        byte[] image = null;
        try{
            Firm firm =firmRepository.findById(id).orElse(null);
            if(firm == null) throw new Exception("No user found with given id");
            String imagePath = firm.getPreviewImgPath();
            Path path = Paths.get(imagePath);
            if(Files.exists(path)){
                image = Files.readAllBytes(path);
            }
        }catch (Exception error){
            log.error("Exception occurs in get image operation of FirmService: {}",error.getMessage());
        }
        return image;
    }

    public Firm create(Firm newFirm){
        Firm firm = new Firm();
        try{
            newFirm.setId(UUID.randomUUID().toString());
            firm = firmRepository.save(newFirm);
            log.info("Firm successfully created as {} with ID : {}",firm.getFirmName(),firm.getId());
        }catch (Exception error){
            log.error("Exception occurs in create operation of FirmService : {}", error.getMessage());
        }
        return firm;
    }

    //NOT COMPLETED
    public Firm update(String id, Firm updatedFirm){
        Firm firm = new Firm();
        try{
            firm = firmRepository.save(updatedFirm);
            log.info("Firm with name {} and ID : {} has been updated",firm.getFirmName(),firm.getId());
        }catch (Exception error){
            log.error("Exception occurs in update operation of FirmService: {}", error.getMessage());
        }
        return firm;
    }

    public boolean delete(String id){
        boolean result = false;
        Firm firm;
        try{
            firm = firmRepository.findById(id).orElseThrow();
            firmRepository.deleteById(id);
            result = true;
            log.info("Firm named {} has been deleted",firm.getFirmName());
        }catch (Exception error){
            log.error("Firm service delete exception: {}", error.getMessage());
        }
        return result;
    }
}
