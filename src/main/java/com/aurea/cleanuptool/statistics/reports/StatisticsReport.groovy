package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.statistics.CoverageMethodStatistic

/**
 * Statistics method report interface
 */
interface StatisticsReport {
    String getReportName()
    int getReportLineCount()

    void produceReport()

    CoverageMethodStatistic.ReportType getReportType()
}
