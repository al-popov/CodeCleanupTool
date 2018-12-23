package com.aurea.cleanuptool.statistics.generators;

import com.aurea.cleanuptool.source.SourceClassData;
import com.aurea.cleanuptool.source.UnitSourceClass;
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic;

import java.util.List;

/**
 * Statistic generator interface
 */
public interface StatisticGenerator {
    void processUnitSourceClass(UnitSourceClass sourceClass);
    void setSourceClassList(List<UnitSourceClass> sourceClassList);
    List<SourceClassData> getReport();
    CoverageMethodStatistic.ReportType getReportType();
}
