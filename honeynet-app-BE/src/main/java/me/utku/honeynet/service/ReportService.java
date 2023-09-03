package me.utku.honeynet.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByCategoryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginCountryDTO;
import me.utku.honeynet.dto.chart.SuspiciousActivityGroupByOriginSourceDTO;
import me.utku.honeynet.dto.report.ReportCategory;
import me.utku.honeynet.dto.report.ReportCountry;
import me.utku.honeynet.dto.report.ReportSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class ReportService {
    private final SuspiciousActivityService suspiciousActivityService;

    public List<ReportCategory> getCategoryandCount() {
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
        return categoryList;
    }
    public List<ReportCountry> getCountryandCount(){
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
        return countryList;
    }
    public List<ReportSource> getSourceandCount(){
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
        return  sourceList;
    }


}