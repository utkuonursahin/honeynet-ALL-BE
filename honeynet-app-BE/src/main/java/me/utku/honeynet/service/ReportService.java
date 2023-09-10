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
import me.utku.honeynet.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final SuspiciousActivityService suspiciousActivityService;


    public List<ReportCategory> getCategoryandCount(Boolean filter) {
        List<SuspiciousActivityGroupByCategoryDTO> resultList = suspiciousActivityService.groupAndCountSuspiciousActivitiesByCategory("2023-01-01", "283e11c6-b8fc-406c-bb01-49014a12e309");
        List<ReportCategory> categoryList = new ArrayList<>();
        if (resultList != null) {
            for (SuspiciousActivityGroupByCategoryDTO dto : resultList) {
                String category = dto.category();
                Long count = dto.count();

                ReportCategory reportCategory = new ReportCategory(category, count);
                categoryList.add(reportCategory);
            }
        }
        return (filter)? categoryList : null;
    }
    public List<ReportCountry> getCountryandCount(Boolean filter){
        List<SuspiciousActivityGroupByOriginCountryDTO> resultList = suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginCountry("2023-01-01","283e11c6-b8fc-406c-bb01-49014a12e309");
        List<ReportCountry> countryList = new ArrayList<>();
        if (countryList!=null){
            for (SuspiciousActivityGroupByOriginCountryDTO dto:resultList){
                String country = dto.country();
                Long count = dto.count();
                ReportCountry reportCountry = new ReportCountry(country,count);
                countryList.add(reportCountry);
            }
        }
        return (filter)? countryList : null;
    }
    public List<ReportSource> getSourceandCount(Boolean filter){
        List<SuspiciousActivityGroupByOriginSourceDTO> resultList = suspiciousActivityService.groupAndCountSuspiciousActivitiesByOriginSource("2023-01-01","283e11c6-b8fc-406c-bb01-49014a12e309");
        List<ReportSource> sourceList = new ArrayList<>();
        if (sourceList!=null){
            for (SuspiciousActivityGroupByOriginSourceDTO dto:resultList){
                String sourceIp = dto.source();
                Long count = dto.count();
                ReportSource reportSource = new ReportSource(sourceIp,count);
                sourceList.add(reportSource);
            }
        }
        return (filter) ? sourceList : null;
    }

    public String htmlToPdf(String processedHtml){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            PdfWriter pdfWriter = new PdfWriter(byteArrayOutputStream);
            DefaultFontProvider defaultFont = new DefaultFontProvider(false, true, false);
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setFontProvider(defaultFont);
            HtmlConverter.convertToPdf(processedHtml, pdfWriter, converterProperties);
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\Desktop\\save\\report.pdf");
            byteArrayOutputStream.writeTo(fileOutputStream);
            byteArrayOutputStream.close();
            byteArrayOutputStream.flush();
            fileOutputStream.close();
            return null;

        }catch (Exception exception){
            //exception
        }
        return null;
    }

    public Context setData(Boolean categoryFilter, Boolean countryFilter, Boolean sourceFilter){
        Context context = new Context();
        Map<String, Object> data = new HashMap<>();
        data.put("categoryList",getCategoryandCount(categoryFilter));
        data.put("countryList",getCountryandCount(countryFilter));
        data.put("sourceList",getSourceandCount(sourceFilter));
        context.setVariables(data);

        return context;
    }






}









