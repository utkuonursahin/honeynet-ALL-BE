package me.utku.honeynet.controller;

import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.GenericResponse;
import me.utku.honeynet.dto.report.ReportSettings;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.model.Report;
import me.utku.honeynet.service.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;

    @GetMapping()
    public GenericResponse<List<Report>> getAllReports(@AuthenticationPrincipal CustomUserDetails userDetails){
        List<Report> reportList = reportService.getAllReports(userDetails.getFirmRef());
        return GenericResponse.<List<Report>>builder().data(reportList).statusCode(200).build();
    }

    @GetMapping("/{id}")
    public GenericResponse<byte[]> getReportById(@PathVariable String id){
        byte[] report = reportService.getReportPdfById(id);
        return GenericResponse.<byte[]>builder().data(report).statusCode(200).build();
    }

    @GetMapping("/cover/{id}")
    public byte[] getReportCoverImageById(@PathVariable String id){
        return reportService.getCoverImage(id);
    }

    @PostMapping()
    public GenericResponse<Report> createReport(@RequestBody ReportSettings reportSettings, @AuthenticationPrincipal CustomUserDetails userDetails) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
        Report report = reportService.createReport(reportSettings, userDetails.getFirmRef());
        return GenericResponse.<Report>builder().data(report).statusCode(200).build();
    }

    @DeleteMapping("/{id}")
    public GenericResponse<Boolean> deleteReport(@PathVariable String id){
        boolean res = reportService.deleteReport(id);
        return GenericResponse.<Boolean>builder().data(res).statusCode(200).build();
    }
}
