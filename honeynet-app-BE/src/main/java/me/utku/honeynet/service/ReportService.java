package me.utku.honeynet.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;
import com.itextpdf.kernel.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import me.utku.honeynet.dto.report.ReportCategory;
import me.utku.honeynet.dto.report.ReportCountry;
import me.utku.honeynet.dto.report.ReportSettings;
import me.utku.honeynet.dto.report.ReportSource;
import me.utku.honeynet.model.Report;
import me.utku.honeynet.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final SuspiciousActivityService suspiciousActivityService;
    private final FirmService firmService;

    public List<ReportCategory> getCategoryandCount(Boolean filter, String firmRef) {
        //List<SuspiciousActivityGroupByCategoryDTO> resultList = suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory("2023-01-01", firmRef);
        List<SuspiciousActivityGroupByCategoryDTO> resultList = filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory("2023-01-01", firmRef) : null;
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        List<ReportCategory> categoryList = new ArrayList<>();
        for (SuspiciousActivityGroupByCategoryDTO dto : resultList) {
            String category = dto.category();
            Long count = dto.count();
            ReportCategory reportCategory = new ReportCategory(category, count);
            categoryList.add(reportCategory);
        }
        return categoryList;
    }

    public List<ReportCountry> getCountryandCount(Boolean filter, String firmRef) {
        List<SuspiciousActivityGroupByOriginCountryDTO> resultList = filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginCountry("2023-01-01", firmRef) : null;
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        List<ReportCountry> countryList = new ArrayList<>();
        for (SuspiciousActivityGroupByOriginCountryDTO dto : resultList) {
            String country = dto.country();
            Long count = dto.count();
            ReportCountry reportCountry = new ReportCountry(country, count);
            countryList.add(reportCountry);
        }
        return countryList;
    }

    public List<ReportSource> getSourceandCount(Boolean filter, String firmRef) {
        List<SuspiciousActivityGroupByOriginSourceDTO> resultList = filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginSource("2023-01-01", firmRef) : null;
        if (resultList == null || resultList.isEmpty()) {
            return null;
        }
        List<ReportSource> sourceList = new ArrayList<>();
        for (SuspiciousActivityGroupByOriginSourceDTO dto : resultList) {
            String sourceIp = dto.source();
            Long count = dto.count();
            ReportSource reportSource = new ReportSource(sourceIp, count);
            sourceList.add(reportSource);
        }
        return sourceList;
    }

    public byte[] htmlToPdf(String processedHtml, ReportSettings reportSettings, String firmRef) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Report report = createReport(reportSettings, firmRef);
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            DefaultFontProvider defaultFont = new DefaultFontProvider(false, true, false);
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(defaultFont);
            HtmlConverter.convertToPdf(processedHtml, pdfWriter, converterProperties);
            FileOutputStream fileOutputStream = new FileOutputStream(report.getReportPath());
            byteArrayOutputStream.writeTo(fileOutputStream);
            byteArrayOutputStream.close();
            byteArrayOutputStream.flush();
            fileOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception exception) {
            log.error("Exception occurs in htmlToPdf operation of ReportService : {}", exception.getMessage());
        }
        return null;
    }

    private Report createReport(ReportSettings reportSettings, String firmRef) {
        Report report = new Report();
        try {
            report.setId(UUID.randomUUID().toString());

            while(reportRepository.existsById(report.getId())) {
                report.setId(UUID.randomUUID().toString());
            }

            report.setReportInitDate(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            report.setReportPath("C:\\Users\\Utku\\Desktop\\reports\\" + report.getId() + ".pdf");
            report.setFirmRef(firmRef);

            List<ReportCategory> categoryList = getCategoryandCount(reportSettings.getByCategory(), firmRef);
            List<ReportCountry> countryList = getCountryandCount(reportSettings.getByCountry(), firmRef);
            List<ReportSource> sourceList = getSourceandCount(reportSettings.getBySource(), firmRef);

            report.setReportCategory(categoryList);
            report.setReportCountry(countryList);
            report.setReportSource(sourceList);

            reportRepository.save(report);
            log.info("New Report successfully created !");
        } catch (Exception exception) {
            log.error("Error occurs while creating a new report at ReportService: {}", exception.getMessage());
        }
        return report;
    }


    public Context setContext(ReportSettings reportSettings, String firmRef) {
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();

        List<ReportCategory> categoryList = getCategoryandCount(reportSettings.getByCategory(), firmRef);
        List<ReportCountry> countryList = getCountryandCount(reportSettings.getByCountry(), firmRef);
        List<ReportSource> sourceList = getSourceandCount(reportSettings.getBySource(), firmRef);

        data.put("categoryList", categoryList);
        data.put("countryList", countryList);
        data.put("sourceList", sourceList);

        log.info("Report Settings for {}: {}", firmRef, reportSettings);
        context.setVariables(data);

        return context;
    }
}