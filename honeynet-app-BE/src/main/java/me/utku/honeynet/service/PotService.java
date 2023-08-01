package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.Firm;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInfo;
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
    private final ServerInfoService serverInfoService;
    private final FirmService firmService;
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
            newPot.setId(UUID.randomUUID().toString());
            pot = potRepository.save(newPot);
        } catch (Exception exception){
            log.error("Pot service create exception: {}",exception.getMessage());
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

    public String sanitize(String input){
        String output = input.replaceAll("[|&]", "");
        if(!output.equals(input)){
            log.warn("Pot service sanitize warning: {} -> {}",input,output);
        }
        return output;
    }

    public ServerInfo setup(String potId, String firmId){
        ServerInfo serverInfo = new ServerInfo();
        try {
            Pot pot = potRepository.findById(potId).orElse(null);
            if(pot == null) throw new Exception("No pot/firm found with given id");
            serverInfo = serverInfoService.create(potId,firmId);
            Runtime.getRuntime()
                .exec("cmd /c cd "
                    + sanitize(pot.getServerPath())
                    + " & start java -jar "
                    + sanitize(pot.getServerFileName())
                    + " --be.id="+sanitize(serverInfo.getId())
                    + " --be.firmId="+sanitize(serverInfo.getFirmRef())
                    + " --server.port="+sanitize(serverInfo.getPort())
                );
        }catch (Exception error){
            log.error("Pot service setup exception: {}",error.getMessage());
        }
        return serverInfo;
    }
}