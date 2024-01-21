package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.repository.PotRepository;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PotService {
    private final PotRepository potRepository;

    public List<Pot> getAll() {
        List<Pot> pots = new ArrayList<>();
        try {
            pots = potRepository.findAll();
        } catch (Exception exception) {
            log.error("Exception occurs in get all operation of PotService : {}", exception.getMessage());
        }
        return pots;
    }

    public Pot get(String id) {
        Pot pot = new Pot();
        try{
            pot = potRepository.findById(id).orElse(null);
        }catch(Exception exception){
            log.error("Exception occurs in get operation of PotService : {}",exception.getMessage());
        }
        return pot;
    }

    public byte[] getImage(String id){
        byte[] image = null;
        try{
            Pot pot = potRepository.findById(id).orElse(null);
            if(pot == null) throw new Exception("No pot found with given id");
            String imagePath = pot.getPreviewImagePath();
            Path path = Paths.get(imagePath);
            if(Files.exists(path)){
                image = Files.readAllBytes(path);
            }
        }catch (Exception error){
            log.error("Exception occurs in get image operation of PotService: {}",error.getMessage());
        }
        return image;
    }

    public Pot create(Pot newPot) {
        Pot pot = new Pot();
        try {
            newPot.setId(UUID.randomUUID().toString());
            pot = potRepository.save(newPot);
            log.info("New pot has been created as {} with ID : {} ",pot.getPotName(), pot.getId());
        } catch (Exception exception){
            log.error("Exception occurs in create operation of PotService: {}",exception.getMessage());
        }
        return pot;
    }

    //NOT COMPLETED
    public Pot update(String potId, Pot updatedParts){
        Pot existPot = new Pot();
        try{
            existPot = potRepository.findById(potId).orElse(null);
            if (existPot == null) throw new Exception("No pot found with given id!");
            potRepository.save(existPot);
            log.info("{} has been updated successfully with ID : {}" ,existPot.getPotName(),potId);
        }catch(Exception exception){
            log.error("Exception occurs in update operation of PotService: {}",exception.getMessage());
        }
        return existPot;
    }

    public boolean delete(String id) {
        boolean isDeleted = false;
        Pot pot;
        try{
            pot = potRepository.findById(id).orElseThrow();
            potRepository.deleteById(id);
            isDeleted = true;
            log.info("{} has been deleted successfully with ID : {}",pot.getPotName(),id);
        } catch(Exception exception){
            log.error("Exception occurs in delete operation of PotService : {}",exception.getMessage());
        }
        return isDeleted;
    }
}