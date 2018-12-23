package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Report for long classes
 */
@Profile('long-class')
@Component
@Log4j2
class LongClassesReport implements StatisticsReport {

    private String reportName

    private int reportLineCount = 0

    private final CoverageMethodStatistic coverageMethodStatistic
    private final ProjectConfiguration projectConfiguration

    private static final List<String> headerList = Collections.unmodifiableList(
            Arrays.asList("Name", "Modifier", "Loc", "Loc not covered",
                    "Method count"))

    LongClassesReport(CoverageMethodStatistic coverageMethodStatistic,
                      ProjectConfiguration projectConfiguration) {

        this.reportName = "long-classes"
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
        List<SourceClassData> longClasses = coverageMethodStatistic.getReport(
                CoverageMethodStatistic.ReportType.LONG_CLASS)
        List<StatisticReportLine> report = new ArrayList<>()
        List<String> description = new ArrayList<>()
        longClasses.findAll {
            it.declaration.toString().split("\r?\n").size() > 300
        }.each {
            report.add(printClassDetails(it))
        }
        reportLineCount = report.size()

        ReportBuilder.build(projectConfiguration.out + "/" + reportName + ".html", "Long classes report",
                headerList, report, new ArrayList<>(), description)
    }

    private static StatisticReportLine printClassDetails(SourceClassData clazz) {
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

        line
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.LONG_CLASS
    }
}
