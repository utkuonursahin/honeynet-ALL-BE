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
import me.utku.honeynet.dto.report.ReportSettings;
import me.utku.honeynet.model.Report;
import me.utku.honeynet.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final ReportRepository reportRepository;
    private final SuspiciousActivityService suspiciousActivityService;
    private final SpringTemplateEngine springTemplateEngine;

    private List<SuspiciousActivityGroupByCategoryDTO> getCategoryByCount(Boolean filter, String firmRef) {
        return filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory("2023-01-01", firmRef) : null;
    }

    private List<SuspiciousActivityGroupByOriginCountryDTO> getCountryByCount(Boolean filter, String firmRef) {
        return filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginCountry("2023-01-01", firmRef) : null;
    }

    private List<SuspiciousActivityGroupByOriginSourceDTO> getSourceByCount(Boolean filter, String firmRef) {
        return filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginSource("2023-01-01", firmRef) : null;
    }

    public List<Report> getAllReports(String firmRef) {
        List<Report> reports = new ArrayList<>();
        try{
            reports = reportRepository.findAllByFirmRef(firmRef);
        } catch (Exception exception){
            log.error("Exception occurs in get all operation of ReportService : {}",exception.getMessage());
        }
        return reports;
    }

    public Report getReportById(String id){
        Report report = null;
        try{
            report = reportRepository.findById(id).orElse(null);
        } catch (Exception exception){
            log.error("Exception occurs in get by id operation of ReportService : {}",exception.getMessage());
        }
        return report;
    }

    public byte[] getReportPdfById(String id){
        try{
            Report report = reportRepository.findById(id).orElse(null);
            if(report == null){
                throw new Exception("No report found with given id");
            }
            FileInputStream fileInputStream = new FileInputStream(report.getReportPath());
            byte[] pdfBytes = fileInputStream.readAllBytes();
            fileInputStream.close();
            return pdfBytes;
        } catch (Exception exception){
            log.error("Exception occurs in get by id operation of ReportService : {}",exception.getMessage());
            return null;
        }
    }

    public byte[] getCoverImage(String id){
        byte[] image = null;
        try{
            Report report = reportRepository.findById(id).orElse(null);
            if(report == null) throw new Exception("No user found with given id");
            String imagePath = report.getReportCoverPath();
            Path path = Paths.get(imagePath);
            if(Files.exists(path)){
                image = Files.readAllBytes(path);
            }
        }catch (Exception error){
            log.error("Exception occurs in get image operation of ReportService: {}",error.getMessage());
        }
        return image;
    }

    public Report createReport(ReportSettings reportSettings, String firmRef) {
        Report report = null;
        try {
            report = new Report(
                getCategoryByCount(reportSettings.byCategory(), firmRef),
                getCountryByCount(reportSettings.byCountry(), firmRef),
                getSourceByCount(reportSettings.bySource(), firmRef),
                LocalDateTime.now().toInstant(ZoneOffset.UTC),
                null, null, firmRef
            );
            report.setId(UUID.randomUUID().toString());
            report.setReportPath("C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\honeynet-app-BE\\src\\main\\resources\\static\\reports\\" + report.getId() + ".pdf");
            report.setReportCoverPath("C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\honeynet-app-BE\\src\\main\\resources\\static\\images\\report-cover\\cover.png");
            reportRepository.save(report);
            log.info("New report document successfully created !");

            Context reportContext = createReportContext(reportSettings, firmRef);
            String finalHtml = createReportHtml(reportContext);
            htmlToPdf(finalHtml, report.getReportPath());
            log.info("Report pdf successfully created!");
        } catch (Exception exception) {
            log.error("Error occurs while creating a new report at ReportService: {}", exception.getMessage());
        }
        return report;
    }

    private Context createReportCoverContext(Report report){
        Context reportCoverContext = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("bgPath","C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\honeynet-app-BE\\src\\main\\resources\\static\\images\\report-cover\\bg.png");
        data.put("beamLogoPath","C:\\Users\\Utku\\Personal\\Projects\\Java Projects\\honeynet-ALL-BE\\honeynet-app-BE\\src\\main\\resources\\static\\images\\report-cover\\beamLogo.png");
        data.put("createdDate",report.getCreatedAt().toString());
        if(!report.getReportCategory().isEmpty()) data.put("categoryList","Category");
        if(!report.getReportCountry().isEmpty()) data.put("countryList","Country");
        if(!report.getReportSource().isEmpty()) data.put("sourceList","Source");
        reportCoverContext.setVariables(data);
        return reportCoverContext;
    }

    private String createReportCoverHtml(Context reportCoverContext){
        return springTemplateEngine.process("report-cover", reportCoverContext);
    }

    private String createReportHtml(Context reportContext){
        return springTemplateEngine.process("report", reportContext);
    }

    private void htmlToPdf(String processedHtml,String reportPath) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            DefaultFontProvider defaultFont = new DefaultFontProvider(false, true, false);
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(defaultFont);
            HtmlConverter.convertToPdf(processedHtml, pdfWriter, converterProperties);
            FileOutputStream fileOutputStream = new FileOutputStream(reportPath);
            byteArrayOutputStream.writeTo(fileOutputStream);
            byteArrayOutputStream.close();
            byteArrayOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception exception) {
            log.error("Exception occurs in htmlToPdf operation of ReportService : {}", exception.getMessage());
        }
    }

    private Context createReportContext(ReportSettings reportSettings, String firmRef) {
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();

        data.put("categoryList", getCategoryByCount(reportSettings.byCategory(), firmRef));
        data.put("countryList", getCountryByCount(reportSettings.byCountry(), firmRef));
        data.put("sourceList", getSourceByCount(reportSettings.bySource(), firmRef));

        log.info("Report Settings for {}: {}", firmRef, reportSettings);
        context.setVariables(data);

        return context;
    }

    private void htmlToPng(String processedHtml, String firmRef) {
        try {

        } catch (Exception exception) {
            log.error("Exception occurs in htmlToPdf operation of ReportService : {}", exception.getMessage());
        }
    }

    private void deleteFile(Path path){
        try{
            if(Files.exists(path)){
                Files.delete(path);
                log.info("File located at '{}' deleted successfully",path);
            }
        }catch (Exception error){
            log.error("Exception occurs in deleteFile operation of ReportService: {}\n File Path: '{}",error.getMessage(),path);
        }
    }

    public boolean deleteReport(String id){
        boolean status = false;
        try{
            Report report = reportRepository.findById(id).orElse(null);
            if(report == null) throw new Exception("No report found with given id");
            reportRepository.delete(report);
            log.info("Report document deleted successfully");
            this.deleteFile(Paths.get(report.getReportPath()));
//            this.deleteFile(Paths.get(report.getReportCoverPath()));
            status = true;
        }catch (Exception error){
            log.error("Exception occurs in delete operation of ReportService: {}",error.getMessage());
        }
        return status;
    }
}