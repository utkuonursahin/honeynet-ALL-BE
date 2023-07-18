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
            log.error("Pot service getAll exception: {}", exception.getMessage());
        }
        return pots;
    }

    public Pot get(String id) {
        Pot pot = new Pot();
        try{
            pot = potRepository.findById(id).orElse(null);
        }catch(Exception exception){
            log.error("Pot service get exception: {}",exception.getMessage());
        }
        return pot;
    }

    public byte[] getImage(String id){
        byte[] image = null;
        try{
            Pot user =potRepository.findById(id).orElse(null);
            if(user == null) throw new Exception("No user found with given id");
            String imagePath = user.getPreviewImagePath();
            Path path = Paths.get(imagePath);
            if(Files.exists(path)){
                image = Files.readAllBytes(path);
            }
        }catch (Exception error){
            log.error("Pot service getImage exception: {}",error.getMessage());
        }
        return image;
    }

    public Pot create(Pot newPot) {
        Pot pot = new Pot();
        try {
            pot = potRepository.save(newPot);
        } catch (Exception exception){
            log.error("Pot service create exception: {}",exception.getMessage());
        }
        return pot;
    }

    public Pot update(String potId, Pot updatedParts){
        Pot existPot = new Pot();
        try{
            existPot = potRepository.findById(potId).orElse(null);
            if (existPot == null) throw new Exception("No pot found with given id!");
            if (updatedParts.getName() != null) {
                existPot.setName(updatedParts.getName());
            }
            if (updatedParts.getUrl() != null) {
                existPot.setUrl(updatedParts.getUrl());
            }
            if (updatedParts.getSetupUrl() != null) {
                existPot.setSetupUrl(updatedParts.getSetupUrl());
            }
            if (updatedParts.getCategory() != null) {
                existPot.setCategory(updatedParts.getCategory());
            }
            if (updatedParts.getPreviewImagePath() != null) {
                existPot.setPreviewImagePath(updatedParts.getPreviewImagePath());
            }
            potRepository.save(existPot);
        }catch(Exception exception){
            log.error("Pot service update exception: {}",exception.getMessage());
        }
        return existPot;
    }

    public boolean delete(String id) {
        boolean isDeleted = false;
        try{
            potRepository.deleteById(id);
            isDeleted = true;
        } catch(Exception exception){
            log.error("Pot service delete exception: {}",exception.getMessage());
        }
        return isDeleted;
    }
}