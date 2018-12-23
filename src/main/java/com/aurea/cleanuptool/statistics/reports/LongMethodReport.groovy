package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile('long-method')
@Component
@Log4j2
class LongMethodReport implements StatisticsReport {
    private String reportName

    private int reportLineCount = 0

    private final CoverageMethodStatistic coverageMethodStatistic
    private final ProjectConfiguration projectConfiguration

    private static final List<String> headerList = Collections.unmodifiableList(
            Arrays.asList("Name", "Modifier", "Loc", "Loc not covered",
                    "Method count", "Long method count"))

    LongMethodReport(CoverageMethodStatistic coverageMethodStatistic,
                     ProjectConfiguration projectConfiguration) {

        this.reportName = "long-methods"
        this.coverageMethodStatistic = coverageMethodStatistic
        this.projectConfiguration = projectConfiguration
    }

    /**
     * Report name for generating report start page
     * @return
     */
    @Override
    String getReportName() {
        reportName
    }

    /**
     * Produced report line count for statistic view
     * @return
     */
    @Override
    int getReportLineCount() {
        reportLineCount
    }

    /**
     * Report generator
     */
    @Override
    void produceReport() {
        List<SourceClassData> longMethod = coverageMethodStatistic.getReport(
                CoverageMethodStatistic.ReportType.LONG_CLASS)
        List<StatisticReportLine> report = new ArrayList<>()
        List<String> description = new ArrayList<>()
        longMethod.each { clazz ->
            int longCount = clazz.methods.findAll {
                it.toString().split("\r?\n").size() > 60
            }.size()
            if (longCount > 0) {
                report.add(printClassDetails(clazz, longCount))
            }
        }
        reportLineCount = report.size()

        ReportBuilder.build(projectConfiguration.out + "/" + reportName + ".html", "Long method report",
                headerList, report, new ArrayList<>(), description)
    }

    private static StatisticReportLine printClassDetails(SourceClassData clazz, int longCount) {
        StatisticReportLine line = new StatisticReportLine()
        line.name = clazz.packageName + "." + clazz.declaration.name.toString()
        String access
        if (clazz.declaration.public) {
            access = "public"
        } else if (clazz.declaration.private) {
            access = "private"
        } else if (clazz.declaration.protected) {
            access = "protected"
        } else {
            access = "package private"
        }

        line.values.add(access)

        if (clazz.methodCoverage.total) {
            line.values.add(String.valueOf(clazz.methodCoverage.total))
            line.values.add(String.valueOf(clazz.methodCoverage.uncovered))
        } else {
            int loc = clazz.declaration.toString().split("\r?\n").size()
            line.values.add(String.valueOf(loc))
            line.values.add(String.valueOf(loc))
        }

        line.values.add(String.valueOf(clazz.methods.size()))
        line.values.add(String.valueOf(longCount))

        line
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.LONG_CLASS
    }
}
