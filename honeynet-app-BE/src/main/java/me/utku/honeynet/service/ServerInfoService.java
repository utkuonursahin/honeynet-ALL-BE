package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.model.ServerInfo;
import me.utku.honeynet.repository.ServerInfoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerInfoService {
    private final ServerInfoRepository serverInfoRepository;

    public ServerInfo create(String potId, String firmId){
        ServerInfo serverInfo = new ServerInfo();
        try{
            serverInfo.setId(UUID.randomUUID().toString());
            serverInfo.setPotId(potId);
            serverInfo.setFirmId(firmId);
            serverInfo.setPort("8090");
            Boolean isExists = serverInfoRepository.existsServerInfosByPotIdAndFirmId(potId,firmId);
            if(!isExists){
                serverInfo = serverInfoRepository.save(serverInfo);
            } else {
                serverInfo = serverInfoRepository.findByPotIdAndFirmId(potId,firmId);
            }
        }catch(Exception exception){
            log.error("ServerInfo service create exception: {}",exception.getMessage());
        }
        return serverInfo;
    }
}
