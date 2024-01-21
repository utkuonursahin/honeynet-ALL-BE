package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.ServerInstance;
import me.utku.honeynet.service.ServerInstanceService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/server-info")
@RequiredArgsConstructor
public class ServerInstanceController {
    private final ServerInstanceService serverInstanceService;

    @GetMapping()
    public GenericResponse<List<ServerInstance>> getAllServerInfosByFirmRef(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ServerInstance> serverInstances = serverInstanceService.getAllByFirmRef(userDetails.getFirmRef());
        return GenericResponse.<List<ServerInstance>>builder().data(serverInstances).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<ServerInstance> getServerInfo(@PathVariable String id){
        ServerInstance serverInstance = serverInstanceService.get(id);
        return GenericResponse.<ServerInstance>builder().data(serverInstance).statusCode(200).build();
    }

    @PostMapping
    public GenericResponse<ServerInstance> saveServerInfo(@RequestParam String potId, @AuthenticationPrincipal CustomUserDetails userDetails){
        ServerInstance savedServerInstance = serverInstanceService.create(potId,userDetails.getFirmRef());
        return GenericResponse.<ServerInstance>builder().data(savedServerInstance).statusCode(200).build();
    }

    @PostMapping("/setup")
    public GenericResponse<ServerInstance> setupServer(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String potId){
        ServerInstance serverInstance = serverInstanceService.setup(potId, userDetails.getFirmRef());
        return GenericResponse.<ServerInstance>builder().data(serverInstance).statusCode(200).build();
    }

    @PostMapping("/shutdown")
    public GenericResponse<ServerInstance> shutdownServer(@RequestParam String id){
        ServerInstance serverInstance = serverInstanceService.shutdown(id);
        return GenericResponse.<ServerInstance>builder().data(serverInstance).statusCode(200).build();
    }

    @PostMapping("/start")
    public GenericResponse<ServerInstance> startServer(@RequestParam String id){
        ServerInstance serverInstance = serverInstanceService.start(id);
        return GenericResponse.<ServerInstance>builder().data(serverInstance).statusCode(200).build();
    }

    @DeleteMapping
    public GenericResponse<String> deleteServerInfo(@RequestParam String id){
        serverInstanceService.delete(id);
        return GenericResponse.<String>builder().data("ServerInstance deleted successfully").statusCode(200).build();
    }

    @DeleteMapping("/terminate")
    public GenericResponse<String> terminateServer(@RequestParam String id){
        serverInstanceService.terminate(id);
        return GenericResponse.<String>builder().data("Server terminated successfully").statusCode(200).build();
    }

}
