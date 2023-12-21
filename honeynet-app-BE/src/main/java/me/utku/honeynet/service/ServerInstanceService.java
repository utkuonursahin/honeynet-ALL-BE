package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.chart.ServerInfoGroupByStatusDTO;
import me.utku.honeynet.enums.ServerInstanceStatus;
import me.utku.honeynet.model.Pot;
import me.utku.honeynet.model.ServerInstance;
import me.utku.honeynet.repository.ServerInstanceRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ServerInstanceService {
    private final MongoTemplate mongoTemplate;
    private final ServerInstanceRepository serverInstanceRepository;
    private final PotService potService;
    @Lazy
    private final RestService restService;

    public List<ServerInstance> getAll(){
        List<ServerInstance> serverInstances = new ArrayList<ServerInstance>();
        try{
            serverInstances = serverInstanceRepository.findAll();
        } catch (Exception exception){
            log.error("Exception occurs in get all operation of ServerInstanceService : {}",exception.getMessage());

        }
        return serverInstances;
    }

    public List<ServerInstance> getAllByFirmRef(String firmId){
        List<ServerInstance> serverInstances = null;
        try{
            serverInstances = serverInstanceRepository.findAllByFirmRef(firmId);
            if (serverInstances != null){
                serverInstances.forEach(serverInfo -> {
                    serverInfo.setPotRef(potService.get(serverInfo.getPotRef()).getPotName());
                });
            }else{
                throw new Exception("There is no server with given firmId");
            }
        } catch (Exception exception){
            log.error("Exception occurs in get all by firm ref operation of ServerInstanceService: {}",exception.getMessage());
        }
        return serverInstances;
    }

    public ServerInstance get(String id){
        ServerInstance serverInstance = null;
        try{
            serverInstance = serverInstanceRepository.findById(id).orElseThrow();
        } catch (Exception exception){
            log.error("Exception occurs in get operation of ServerInstanceService : {}",exception.getMessage());
        }
        return serverInstance;
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
            log.error("Exception occurs in groupAndCountServerInfoByStatus operation of ServerInstanceService : {}", error.getMessage());
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
                isReserved = serverInstanceRepository.existsByPort(Integer.toString(port));
            } while (isReserved);
            return port;
        } catch (Exception exception){
            log.error("Exception occurs in findAvailablePort of ServerInstanceService : {}",exception.getMessage());
            return 0;
        }
    }

    public ServerInstance getByPotIdAndFirmId(String potId, String firmId){
        try{
            return serverInstanceRepository.findByPotRefAndFirmRef(potId,firmId);
        }catch (Exception exception){
            log.error("Exception occurs in getByPotIdAndFirmId of ServerInstanceService : {}",exception.getMessage());
            return null;
        }
    }

    public String sanitize(String input){
        String output = input.replaceAll("[|&]", "");
        if(!output.equals(input)){
            log.warn("Pot service sanitize warning: {} -> {}",input,output);
        }
        return output;
    }

    public ServerInstance create(String potId, String firmId){
        ServerInstance serverInstance = new ServerInstance();
        try{
            serverInstance = serverInstanceRepository.findByPotRefAndFirmRef(potId,firmId);
            if(serverInstance != null){
                serverInstance.setStatus(ServerInstanceStatus.RUN);
                serverInstance = serverInstanceRepository.save(serverInstance);
                return serverInstance;
            }
            else {
                serverInstance = new ServerInstance();
                InetAddress ia = InetAddress.getLocalHost();
                serverInstance.setId(UUID.randomUUID().toString());
                serverInstance
                    .setPotRef(potId)
                    .setFirmRef(firmId)
                    .setHostName(ia.getHostName())
                    .setHost(ia.getHostAddress())
                    .setPort(Integer.toString(findAvailablePort()))
                    .setUrl("http://"+ia.getHostAddress()+":"+ serverInstance.getPort())
                    .setStatus(ServerInstanceStatus.RUN);
                serverInstance = serverInstanceRepository.save(serverInstance);
            }
            log.info("New Server has been created");
        }catch(Exception exception){
            log.error("Exception occurs during the creation of Server : {}",exception.getMessage());
        }
        return serverInstance;
    }

    //NOT COMPLETED
    public ServerInstance update(String id, ServerInstance updatedParts){
        ServerInstance serverInstance = null;
        try{
            serverInstance = serverInstanceRepository.findById(id).orElseThrow();
            serverInstance = serverInstanceRepository.save(serverInstance);
            log.info("{} has been updated with ID : {}",potService.get(serverInstance.getPotRef()).getPotName(), id);
        } catch (Exception exception){
            log.error("Exception occurs in update operation of ServerInstanceService : {}",exception.getMessage());
        }
        return serverInstance;
    }

    public void delete(String id){
        ServerInstance serverInstance;
        try{
            serverInstance = serverInstanceRepository.findById(id).orElseThrow();
            serverInstanceRepository.deleteById(id);
            log.info("{} has been deleted successfully with ID : {}",potService.get(serverInstance.getPotRef()).getPotName(),id);
        } catch (Exception exception){
            log.error("Exception occurs in delete operation of ServerInstanceService : {}",exception.getMessage());
        }
    }

    public void extractJar(String path){
        Process process;
        try{
            process = Runtime.getRuntime()
                .exec("cmd /c cd "
                    + sanitize(path)
                    + " & mvnw.cmd clean install"
                );
            process.waitFor();
        }catch (Exception error){
            log.error("Exception occurs in extractJar operation of ServerInstanceService : {}",error.getMessage());
        }
    }

    public ServerInstance setup(String potId, String firmId){
        ServerInstance serverInstance = new ServerInstance();
        Process process;
        try{
            Pot pot = potService.get(potId);
            if(pot == null) throw new Exception("No pot found with given id");
            serverInstance = create(potId,firmId);
            process = Runtime.getRuntime()
                .exec("cmd /c cd "
                    + sanitize(pot.getServerPath())
                    + " & start java -jar "
                    + sanitize(pot.getServerFileName())
                    + " --be.id="+sanitize(serverInstance.getId())
                    + " --be.firmId="+sanitize(serverInstance.getFirmRef())
                    + " --server.port="+sanitize(serverInstance.getPort())
                );
            process.waitFor();
        }catch (Exception error){
            log.error("Exception occurs in setup operation of ServerInstanceService : {}",error.getMessage());}
        return serverInstance;
    }

    public ServerInstance shutdown(String id){
        ServerInstance serverInstance = null;
        try{
            serverInstance = serverInstanceRepository.findById(id).orElseThrow();
            serverInstance.setStatus(ServerInstanceStatus.SHUTDOWN);
            serverInstance = serverInstanceRepository.save(serverInstance);
            restService.shutdownTargetServer(serverInstance);
            log.info("{} has been shut down on port : {}",potService.get(serverInstance.getPotRef()).getPotName(), serverInstance.getPort());
        } catch (Exception exception){
            log.error("Exception occurs during shutdown in ServerInstanceService : {}",exception.getMessage());
        }
        return serverInstance;
    }

    public ServerInstance start(String id){
        ServerInstance serverInstance = null;
        try{
            serverInstance = serverInstanceRepository.findById(id).orElse(null);
            assert serverInstance != null;
            serverInstance = setup(serverInstance.getPotRef(), serverInstance.getFirmRef());
            serverInstance.setStatus(ServerInstanceStatus.RUN);
            serverInstance = serverInstanceRepository.save(serverInstance);
            log.info("{} has been initialized on port : {}",potService.get(serverInstance.getPotRef()).getPotName(), serverInstance.getPort());
        } catch (Exception error){
            log.error("Exception occurs in start operation of ServerInstanceService : {}",error.getMessage());
        }
        return serverInstance;
    }

    public void terminate(String id){
        try{
            ServerInstance serverInstance = serverInstanceRepository.findById(id).orElseThrow();
            restService.shutdownTargetServer(serverInstance);
            serverInstanceRepository.deleteById(id);
            log.info("{} has been terminated from port : {} ",potService.get(serverInstance.getPotRef()).getPotName(), serverInstance.getPort());
        } catch (Exception exception){
            log.error("Exception occurs in terminate operation of ServerInstanceService : {}",exception.getMessage());
        }
    }
}
