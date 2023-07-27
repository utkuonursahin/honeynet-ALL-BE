package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.model.Firm;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInfo;
import me.utku.honeynet.repository.ServerInfoRepository;
import org.springframework.stereotype.Service;

import java.net.ServerSocket;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ServerInfoService {
    private final ServerInfoRepository serverInfoRepository;

    public int findAvailablePort(){
        try{
            ServerSocket serverSocket = new ServerSocket(0);
            int port = serverSocket.getLocalPort();
            serverSocket.close();
            return port;
        } catch (Exception exception){
            log.error("ServerInfo service findAvailablePort exception: {}",exception.getMessage());
            return 0;
        }
    }

    public ServerInfo create(Pot pot, Firm firm){
        ServerInfo serverInfo = new ServerInfo();
        try{
            serverInfo.setId(UUID.randomUUID().toString());
            serverInfo.setPot(pot);
            serverInfo.setFirm(firm);
            serverInfo.setPort(Integer.toString(findAvailablePort()));
            Boolean isExists = serverInfoRepository.existsServerInfosByPotIdAndFirmId(pot.getId(),firm.getId());
            if(!isExists){
                serverInfo = serverInfoRepository.save(serverInfo);
            } else {
                serverInfo = serverInfoRepository.findByPotIdAndFirmId(pot.getId(),firm.getId());
            }
        }catch(Exception exception){
            log.error("ServerInfo service create exception: {}",exception.getMessage());
        }
        return serverInfo;
    }
}
