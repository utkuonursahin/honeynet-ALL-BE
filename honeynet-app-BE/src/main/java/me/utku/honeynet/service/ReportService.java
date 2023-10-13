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
import me.utku.honeynet.dto.report.ReportSource;
import me.utku.honeynet.enums.ReportContains;
import me.utku.honeynet.model.Firm;
import me.utku.honeynet.model.Report;
import me.utku.honeynet.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final SuspiciousActivityService suspiciousActivityService;
    private final FirmService firmService;


    //for returning all firmRefs from FirmService
    public List<String> getAllFirmRefs(){
        List<String> firmRefs = new ArrayList<>();
        firmService.getAll().forEach((firm)->{
            firmRefs.add(firm.getId());
        });
        return firmRefs;
    };

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
        List<SuspiciousActivityGroupByOriginCountryDTO> resultList =filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginCountry("2023-01-01", firmRef) : null;
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
        return  countryList  ;
    }

    public List<ReportSource> getSourceandCount(Boolean filter, String firmRef) {
        List<SuspiciousActivityGroupByOriginSourceDTO> resultList =filter ? suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginSource("2023-01-01", firmRef):null;
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
        return   sourceList ;
    }


    public byte[] htmlToPdf(String processedHtml, Boolean categoryFilter, Boolean countryFilter, Boolean sourceFilter) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Random random = new Random();
        Integer pdfIdentifier = random.nextInt(9999 - 1 + 1) + 1; // Generate a random number between 1 and 9999
        Report newestReport = reportRepository.findFirstByOrderByReportInitDateDesc();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(newestReport.getReportPath());
        Integer savedIdentifier = matcher.find() ? Integer.parseInt(matcher.group()) : 0;

        // Check if pdfIdentifier is the same as savedIdentifier, and generate a new one if needed
        do {
            if (Objects.equals(pdfIdentifier, savedIdentifier)) {
                pdfIdentifier = random.nextInt(9999 - 1 + 1) + 1;
            }
            String filePath = "D:\\Desktop\\save\\report" + pdfIdentifier + ".pdf";

            try {
                PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
                DefaultFontProvider defaultFont = new DefaultFontProvider(false, true, false);
                ConverterProperties converterProperties = new ConverterProperties();
                converterProperties.setFontProvider(defaultFont);
                HtmlConverter.convertToPdf(processedHtml, pdfWriter, converterProperties);
                saveReport(filePath,categoryFilter, countryFilter, sourceFilter);
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                byteArrayOutputStream.writeTo(fileOutputStream);
                byteArrayOutputStream.close();
                byteArrayOutputStream.flush();
                fileOutputStream.close();
                return byteArrayOutputStream.toByteArray();

            } catch (Exception exception) {
                log.error("Exception occurs in htmlToPdf operation of ReportService : {}", exception.getMessage());
            }
        } while (Objects.equals(pdfIdentifier, savedIdentifier));

        return null;
    }


    public Context setData(Boolean categoryFilter, Boolean countryFilter, Boolean sourceFilter){
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();

        for (String firmRef: getAllFirmRefs()){
            System.out.println(firmRef);
            if (categoryFilter && !firmRef.isEmpty()) {
                List<ReportCategory> categoryList = getCategoryandCount(categoryFilter, firmRef);
                log.info("Category List for {}: {}", firmRef, categoryList);
                data.put("categoryList", categoryList);
            }
            if (countryFilter && !firmRef.isEmpty()) {
                List<ReportCountry> countryList = getCountryandCount(countryFilter, firmRef);
                log.info("Country List for {}: {}", firmRef, countryList);
                data.put("countryList", countryList);
            }
            if (sourceFilter && !firmRef.isEmpty()) {
                List<ReportSource> sourceList = getSourceandCount(sourceFilter, firmRef);
                log.info("Source List for {}: {}", firmRef, sourceList);
                data.put("sourceList", sourceList);
            }
        }
        context.setVariables(data);

        return context;
    }
    public void saveReport(String filePath, Boolean categoryFilter, Boolean countryFilter, Boolean sourceFilter) {
        try {
            for (String firmRef : getAllFirmRefs()) {
                Report report = new Report();
                report.setReportInitDate(new Date());
                report.setReportPath(filePath);
                ReportContains[] reportContainsList = new ReportContains[3];


                List<ReportCategory> categoryList = getCategoryandCount(categoryFilter, firmRef);
                if (categoryList != null) {
                    report.setReportCategory(categoryList);
                    reportContainsList[0] = ReportContains.CATEGORY;
                }else {
                    report.setReportCategory(null);
                    reportContainsList[0] = null;
                }

                List<ReportCountry> countryList = getCountryandCount(countryFilter, firmRef);
                if (countryList != null) {
                    report.setReportCountry(countryList);
                    reportContainsList[1] = ReportContains.COUNTRY;
                }else{
                    report.setReportCategory(null);
                    reportContainsList[1] = null;
                }

                List<ReportSource> sourceList = getSourceandCount(sourceFilter, firmRef);
                if (sourceList != null) {
                    report.setReportSource(sourceList);
                    reportContainsList[2] = ReportContains.SOURCE;
                }else{
                    report.setReportSource(null);
                    reportContainsList[2] = null;
                }
                report.setReportContains(reportContainsList);
                reportRepository.save(report);
            }
            log.info("New Report successfully created !");
        } catch (Exception exception) {
            log.error("Error occurs while creating a new report at ReportService: {}", exception.getMessage());
        }
    }




}









