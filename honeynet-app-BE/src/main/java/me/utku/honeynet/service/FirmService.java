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
            log.error("Firm service getAll exception: {}", exception.getMessage());
        }
        return firms;
    }

    public Firm get(String id){
        Firm firm = new Firm();
        try{
            firm = firmRepository.findById(id).orElse(null);
        }catch (Exception error){
            log.error("Firm service get exception: {}", error.getMessage());
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
            log.error("Pot service getImage exception: {}",error.getMessage());
        }
        return image;
    }

    public Firm create(Firm newFirm){
        Firm firm = new Firm();
        try{
            newFirm.setId(UUID.randomUUID().toString());
            firm = firmRepository.save(newFirm);
        }catch (Exception error){
            log.error("Firm service create exception: {}", error.getMessage());
        }
        return firm;
    }

    //NOT COMPLETED
    public Firm update(String id, Firm updatedFirm){
        Firm firm = new Firm();
        try{
            firm = firmRepository.save(updatedFirm);
        }catch (Exception error){
            log.error("Firm service create exception: {}", error.getMessage());
        }
        return firm;
    }

    public boolean delete(String id){
        boolean result = false;
        try{
            firmRepository.deleteById(id);
            result = true;
        }catch (Exception error){
            log.error("Firm service delete exception: {}", error.getMessage());
        }
        return result;
    }
}
