package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.ServerInfo;
import me.utku.honeynet.service.ServerInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/server-info")
@RequiredArgsConstructor
public class ServerInfoController {
    private final ServerInfoService serverInfoService;

    @GetMapping()
    public GenericResponse<List<ServerInfo>> getAllServerInfosByFirmRef(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<ServerInfo> serverInfos = serverInfoService.getAllByFirmRef(userDetails.getFirmRef());
        return GenericResponse.<List<ServerInfo>>builder().data(serverInfos).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<ServerInfo> getServerInfo(@PathVariable String id){
        ServerInfo serverInfo = serverInfoService.get(id);
        return GenericResponse.<ServerInfo>builder().data(serverInfo).statusCode(200).build();
    }

    @PostMapping
    public GenericResponse<ServerInfo> saveServerInfo(@RequestParam String potId, @AuthenticationPrincipal CustomUserDetails userDetails){
        ServerInfo savedServerInfo = serverInfoService.create(potId,userDetails.getFirmRef());
        return GenericResponse.<ServerInfo>builder().data(savedServerInfo).statusCode(200).build();
    }

    @PostMapping("/setup")
    public GenericResponse<ServerInfo> setupServer(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestParam String potId){
        ServerInfo serverInfo = serverInfoService.setup(potId, userDetails.getFirmRef());
        return GenericResponse.<ServerInfo>builder().data(serverInfo).statusCode(200).build();
    }

    @PostMapping("/shutdown")
    public GenericResponse<ServerInfo> shutdownServer(@RequestParam String id){
        ServerInfo serverInfo = serverInfoService.shutdown(id);
        return GenericResponse.<ServerInfo>builder().data(serverInfo).statusCode(200).build();
    }

    @PostMapping("/start")
    public GenericResponse<ServerInfo> startServer(@RequestParam String id){
        ServerInfo serverInfo = serverInfoService.start(id);
        return GenericResponse.<ServerInfo>builder().data(serverInfo).statusCode(200).build();
    }

    @DeleteMapping
    public GenericResponse<String> deleteServerInfo(@RequestParam String id){
        serverInfoService.delete(id);
        return GenericResponse.<String>builder().data("ServerInfo deleted successfully").statusCode(200).build();
    }

    @DeleteMapping("/terminate")
    public GenericResponse<String> terminateServer(@RequestParam String id){
        serverInfoService.terminate(id);
        return GenericResponse.<String>builder().data("Server terminated successfully").statusCode(200).build();
    }

}
