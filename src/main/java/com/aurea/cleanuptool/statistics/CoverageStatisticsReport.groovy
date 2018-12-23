package com.aurea.cleanuptool.statistics

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.source.SourcesRepository
import com.aurea.cleanuptool.statistics.reports.ReportBuilder
import com.aurea.cleanuptool.statistics.reports.StatisticReportLine
import com.aurea.cleanuptool.statistics.reports.StatisticsReport
import groovy.util.logging.Log4j2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Generate coverage method reports
 */
@Component
@Log4j2
class CoverageStatisticsReport {

    final ProjectConfiguration projectConfiguration
    private CoverageMethodStatistic coverageMethodStatistic

    @Autowired
    final SourcesRepository sourcesRepository

    @Autowired
    List<StatisticsReport> statisticsReports

    private static final List<String> headerList = Collections.unmodifiableList(
            Arrays.asList("Report name", "Report size(lines)"))

    CoverageStatisticsReport(ProjectConfiguration projectConfiguration,
                             CoverageMethodStatistic coverageMethodStatistic) {

        this.projectConfiguration = projectConfiguration
        this.coverageMethodStatistic = coverageMethodStatistic
        ReportBuilder.setReportTemplate(projectConfiguration.reportTemplate)
    }

    void produceReport() {
        File outputDirectory = new File(projectConfiguration.out)
        outputDirectory.mkdirs()

        coverageMethodStatistic.updateStatistics()

        final List<StatisticReportLine> reportNames = new ArrayList<>()
        statisticsReports.each {
            StatisticReportLine line = new StatisticReportLine()
            if(coverageMethodStatistic.getReport(it.reportType).size() > 0) {
                it.produceReport()
                line.name = it.reportName
                line.link = it.reportName + ".html"
                line.values.add(String.valueOf(it.reportLineCount))
                reportNames.add(line)
            }
        }

        ReportBuilder.build(projectConfiguration.out + "/index.html", "Report for " + projectConfiguration.src,
                headerList, reportNames, new ArrayList<>(), new ArrayList<>())
    }
}