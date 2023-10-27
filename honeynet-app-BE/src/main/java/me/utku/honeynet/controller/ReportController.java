package me.utku.honeynet.controller;
import lombok.RequiredArgsConstructor;
import me.utku.honeynet.dto.report.ReportSettings;
import me.utku.honeynet.dto.security.CustomUserDetails;
import me.utku.honeynet.service.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

@Controller
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final SpringTemplateEngine springTemplateEngine;
    @PostMapping
    public ResponseEntity<byte[]> generateDocument(@RequestBody ReportSettings reportSettings, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String finalHtml = null;
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf");
        Context dataContext = reportService.setContext(reportSettings, userDetails.getFirmRef());
        finalHtml = springTemplateEngine.process("report",dataContext);
        byte[] pdfBytes = reportService.htmlToPdf(finalHtml, reportSettings, userDetails.getFirmRef());
        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(pdfBytes);
    }
}
