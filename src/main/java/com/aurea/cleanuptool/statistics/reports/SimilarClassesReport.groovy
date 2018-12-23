package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Report for classes with equal method and equal code
 */
@Profile('similar-classes')
@Component
@Log4j2
class SimilarClassesReport implements StatisticsReport {

    private String reportName

    private int reportLineCount = 0

    private final CoverageMethodStatistic coverageMethodStatistic
    private final ProjectConfiguration projectConfiguration

    private static final List<String> headerList = Collections.unmodifiableList(
            Arrays.asList("Class", "Duplicated classes", "Loc uncovered", "Total Loc", "Total Methods"))

    private int similarClassCount
    private int hallClassCount
    private boolean mostEqualMethod

    SimilarClassesReport(CoverageMethodStatistic coverageMethodStatistic,
                         ProjectConfiguration projectConfiguration) {

        this.reportName = "similar-classes"
        this.coverageMethodStatistic = coverageMethodStatistic
        this.projectConfiguration = projectConfiguration
        mostEqualMethod = false
    }

    protected void setMostEqualMethod(boolean flag) {
        mostEqualMethod = flag
    }

    /**
     * Set name for class that will extend this
     * @param name
     */
    protected void setReportName(String name) {
        reportName = name
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
        List<SourceClassData> duplications = getReport()

        hallClassCount = duplications.size()

        log.info("Creating class duplication for parsed classes: " + duplications.size())
        if (projectConfiguration.groupByExtends) {
            List<StatisticReportLine> report = produceExtendsReport(duplications)

            reportLineCount = report.size()

            ReportBuilder.build(projectConfiguration.out + "/" + reportName + ".html", "Similar class report",
                    headerList, report, new ArrayList<>(), new ArrayList<>())

        } else {
            produceCustomReport(reportName, duplications, null)
        }
    }

    /**
     * Similar method report
     * @return
     */
    protected List<SourceClassData> getReport() {
        coverageMethodStatistic.getReport(getReportType())
    }

    /**
     * For groovy incapsulation purpose
     * @return
     */
    protected CoverageMethodStatistic getCoverageMethodStatistic() {
        coverageMethodStatistic
    }

    /**
     * For groovy incapsulation purpose
     * @return
     */
    protected ProjectConfiguration getProjectConfiguration() {
        projectConfiguration
    }

    /**
     * For groovy incapsulation purpose
     * @return
     */
    protected int getSimilarClassCount() {
        similarClassCount
    }

    /**
     * For groovy incapsulation purpose
     * @return
     */
    protected void setSimilarClassCount(int count) {
        similarClassCount = count
    }

    /**
     * Produce custom report for class extends super class
     * @param reportPath
     * @param duplications - statistic for all duplication classes
     * @param superClass - super class for grouping, could be null if no grouping set
     */
    protected void produceCustomReport(String reportPath,
                                       List<SourceClassData> duplications,
                                       String superClass) {

        similarClassCount = 0

        List<String> description = new ArrayList<>()
        List<StatisticReportLine> report = produceSimilarReport(duplications, superClass)

        description.add("Total duplication class count: " + similarClassCount +
                " of all class found: " + hallClassCount)

        if (superClass == null) {
            reportLineCount = report.size()
        }

        ReportBuilder.build(projectConfiguration.out + "/" + reportPath + ".html", "Similar class report",
                headerList, report, new ArrayList<>(), description)
    }

    /**
     * Produce similar statistic line report for class extends super class
     * @param duplications - statistic for all duplication classes
     * @param superClass - super class for grouping, could be null if no grouping set
     * @return
     */
    protected List<StatisticReportLine> produceSimilarReport(List<SourceClassData> duplications,
                                                             String superClass) {
        List<StatisticReportLine> report = new ArrayList<>()
        Set<String> uniqueClasses = new HashSet<>()
        int parsedCount = 0
        ClassDuplicationReport classDuplication =
                new ClassDuplicationReport(reportName, projectConfiguration.produceDiffs)
        classDuplication.setMostEqualMethod(mostEqualMethod)
        duplications.each { entry ->
            boolean parseFlag = false

            if (superClass == null) {
                parseFlag = true
            } else if (superClass.isEmpty()) {
                if (entry.declaration.extendedTypes.size() == 0) {
                    parseFlag = true
                }
            } else {
                if (entry.declaration.extendedTypes.find { it.name.toString().equals(superClass) } != null) {
                    parseFlag = true
                }
            }

            if (parseFlag) {
                parsedCount++

                if (!entry.references.isEmpty()) {
                    log.info("Report for class(" + parsedCount + "/" +
                            duplications.size() + "): " +
                            entry.declaration.name.toString())

                    String fullClassName = ClassesDiffReport.getClassFullName(entry)
                    String fileName = classDuplication.writeClassDetailsFile(entry, projectConfiguration.out)

                    if (!uniqueClasses.contains(fullClassName)) {
                        uniqueClasses.add(fullClassName)

                        StatisticReportLine line = new StatisticReportLine()
                        line.name = entry.declaration.name.toString()
                        line.link = fileName
                        String classes = writeSimilarClasses(entry)

                        classes += "Total classes: " + entry.references.size()
                        line.values.add(classes)
                        ClassDuplicationReport.addCoverageInfo(entry, line)
                        line.values.add(Integer.toString(entry.methods.size()))

                        report.add(line)
                        similarClassCount++

                        log.info("Similar class statistic for " + line.name)
                    }
                }
            }
        }
        report
    }

    /**
     * Produce reports for each superclass
     * @param duplications - super class for grouping, could be null if no grouping set
     * @return
     */
    protected List<StatisticReportLine> produceExtendsReport(List<SourceClassData> duplications) {
        List<StatisticReportLine> report = new ArrayList<>()
        Map<String, Integer> uniqueClasses = new TreeMap<>()
        duplications.each { entry ->
            String name = ""
            entry.declaration.extendedTypes.each {
                name += it.name.toString()
            }
            if (uniqueClasses.containsKey(name)) {
                uniqueClasses[name]++
            } else {
                uniqueClasses[name] = 1
            }
        }

        uniqueClasses.each { entry ->
            if (entry.value > 1) {
                StatisticReportLine line = new StatisticReportLine()
                if (entry.key.isEmpty()) {
                    line.name = "no_extends"
                } else {
                    line.name = entry.key
                }

                line.link = reportName + "_" + line.name
                line.values.add(entry.value.toString())
                log.info("creating report: " + reportName + "_" + line.name)
                log.info("creating report for key: " + entry.key)
                log.info("creating report for dup: " + duplications)
                produceCustomReport(line.link, duplications, entry.key)
                line.link += ".html"

                report.add(line)
            }
        }

        report
    }

    /**
     * Similar classes statistic
     * @param sourceClass
     * @return
     */
    protected String writeSimilarClasses(SourceClassData sourceClass) {
        String classes = ""
        sourceClass.references.eachWithIndex { dupClass, int i ->
            classes += dupClass.declaration.name.toString() + " equal methods: "
            classes += sourceClass.duplicationCount[i] + " of " + dupClass.methods.size()
            classes += ", "
        }
        classes
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.SIMILAR_CLASS
    }
}
