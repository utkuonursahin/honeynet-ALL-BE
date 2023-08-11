package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.chart.ServerInfoGroupByStatusDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.enums.ServerInfoStatus;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInfo;
import me.utku.honeynet.repository.ServerInfoRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ServerInfoService {
    private final MongoTemplate mongoTemplate;
    private final ServerInfoRepository serverInfoRepository;
    private final PotService potService;
    @Lazy
    private final RestService restService;

    public List<ServerInfo> getAll(){
        List<ServerInfo> serverInfos = null;
        try{
            serverInfos = serverInfoRepository.findAll();
        } catch (Exception exception){
            log.error("ServerInfo service getAll exception: {}",exception.getMessage());
        }
        return serverInfos;
    }

    public List<ServerInfo> getAllByFirmRef(String firmId){
        List<ServerInfo> serverInfos = null;
        try{
            serverInfos = serverInfoRepository.findAllByFirmRef(firmId);
            serverInfos.forEach(serverInfo -> {
                serverInfo.setPotRef(potService.get(serverInfo.getPotRef()).getPotName());
            });
        } catch (Exception exception){
            log.error("ServerInfo service getAll exception: {}",exception.getMessage());
        }
        return serverInfos;
    }

    public ServerInfo get(String id){
        ServerInfo serverInfo = null;
        try{
            serverInfo = serverInfoRepository.findById(id).orElseThrow();
        } catch (Exception exception){
            log.error("ServerInfo service get exception: {}",exception.getMessage());
        }
        return serverInfo;
    }

    public List<ServerInfoGroupByStatusDTO> groupAndCountServerInfoByStatus(String firmRef){
        try {
            GroupOperation groupOperation = group("status").count().as("count");
            MatchOperation matchOperation = match(Criteria.where("firmRef").is(firmRef));
            SortOperation sortOperation = sort(Sort.DEFAULT_DIRECTION, "status");
            ProjectionOperation projectionOperation = project("count").and("status").previousOperation();
            Aggregation aggregation = Aggregation.newAggregation(matchOperation, groupOperation,sortOperation,projectionOperation);
            AggregationResults<ServerInfoGroupByStatusDTO> results = mongoTemplate.aggregate(aggregation, "serverInfo", ServerInfoGroupByStatusDTO.class);
            return results.getMappedResults();
        } catch (Exception error) {
            log.error("SuspiciousActivity service groupAndCountSuspiciousActivitiesByCategory exception: {}", error.getMessage());
            return null;
        }
    }

    public int findAvailablePort(){
        try{
            ServerSocket serverSocket;
            int port;
            boolean isReserved;
            do{
                serverSocket = new ServerSocket(0);
                port = serverSocket.getLocalPort();
                serverSocket.close();
                isReserved = serverInfoRepository.existsByPort(Integer.toString(port));
            } while (isReserved);
            return port;
        } catch (Exception exception){
            log.error("ServerInfo service findAvailablePort exception: {}",exception.getMessage());
            return 0;
        }
    }

    public ServerInfo getByPotIdAndFirmId(String potId,String firmId){
        return serverInfoRepository.findByPotRefAndFirmRef(potId,firmId);
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
        try{
            Pot pot = potService.get(potId);
            if(pot == null) throw new Exception("No pot found with given id");
            serverInfo = create(potId,firmId);
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
            log.error("Server info service setup exception: {}",error.getMessage());
        }
        return serverInfo;
    }

    public ServerInfo create(String potId, String firmId){
        ServerInfo serverInfo = new ServerInfo();
        try{
            serverInfo = serverInfoRepository.findByPotRefAndFirmRef(potId,firmId);
            if(serverInfo != null){
                serverInfo.setStatus(ServerInfoStatus.RUN);
                serverInfo = serverInfoRepository.save(serverInfo);
                return serverInfo;
            }
            else {
                serverInfo = new ServerInfo();
                InetAddress ia = InetAddress.getLocalHost();
                serverInfo.setId(UUID.randomUUID().toString());
                serverInfo
                    .setPotRef(potId)
                    .setFirmRef(firmId)
                    .setHostName(ia.getHostName())
                    .setHost(ia.getHostAddress())
                    .setPort(Integer.toString(findAvailablePort()))
                    .setUrl("http://"+ia.getHostAddress()+":"+serverInfo.getPort())
                    .setStatus(ServerInfoStatus.RUN);
                serverInfo = serverInfoRepository.save(serverInfo);
            }
        }catch(Exception exception){
            log.error("ServerInfo service create exception: {}",exception.getMessage());
        }
        return serverInfo;
    }

    //NOT COMPLETED
    public ServerInfo update(String id, ServerInfo updatedParts){
        ServerInfo serverInfo = null;
        try{
            serverInfo = serverInfoRepository.findById(id).orElseThrow();
            serverInfo = serverInfoRepository.save(serverInfo);
        } catch (Exception exception){
            log.error("ServerInfo service update exception: {}",exception.getMessage());
        }
        return serverInfo;
    }

    public void delete(String id){
        try{
            serverInfoRepository.deleteById(id);
        } catch (Exception exception){
            log.error("ServerInfo service delete exception: {}",exception.getMessage());
        }
    }

    public ServerInfo shutdown(String id){
        ServerInfo serverInfo = null;
        try{
            serverInfo = serverInfoRepository.findById(id).orElseThrow();
            serverInfo.setStatus(ServerInfoStatus.SHUTDOWN);
            serverInfo = serverInfoRepository.save(serverInfo);
            restService.shutdownTargetServer(serverInfo);
        } catch (Exception exception){
            log.error("ServerInfo service shutdown exception: {}",exception.getMessage());
        }
        return serverInfo;
    }

    public ServerInfo start(String id){
        ServerInfo serverInfo = null;
        try{
            serverInfo = serverInfoRepository.findById(id).orElse(null);
            assert serverInfo != null;
            serverInfo = setup(serverInfo.getPotRef(),serverInfo.getFirmRef());
            serverInfo.setStatus(ServerInfoStatus.RUN);
            serverInfo = serverInfoRepository.save(serverInfo);
        } catch (Exception error){
            log.error("ServerInfo service start exception: {}",error.getMessage());
        }
        return serverInfo;
    }

    public void terminate(String id){
        try{
            ServerInfo serverInfo = serverInfoRepository.findById(id).orElseThrow();
            restService.shutdownTargetServer(serverInfo);
            serverInfoRepository.deleteById(id);
        } catch (Exception exception){
            log.error("ServerInfo service terminate exception: {}",exception.getMessage());
        }
    }
}
