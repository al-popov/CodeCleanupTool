package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.source.SourceClassData

class ClassDuplicationReport {

    private static final List<String> headerListClasses = Collections.unmodifiableList(
            Arrays.asList("FQCN", "Equal names method count", "Total method count", "Loc not covered",
                    "Total Loc", "Similarity percentage", "Extends", "implements"))

    private final String pathToFiles

    private final boolean produceDiffReport
    private boolean mostEqualMethod

    ClassDuplicationReport(String path, boolean produceDiffReport) {
        pathToFiles = path
        this.produceDiffReport = produceDiffReport
        mostEqualMethod = false
    }

    void setMostEqualMethod(boolean flag) {
        mostEqualMethod = flag
    }

    /**
     * Create per class similarity report
     * @param clazz - class for report
     * @param reportPath - path for report
     * @return - generated report file name
     */
    String writeClassDetailsFile(SourceClassData clazz,
                                 String reportPath) {
        String filePath = reportPath + "/"
        String relativePath = pathToFiles + "/" + clazz.packageName + "/"

        File newDirectoryStructureParent = new File(filePath + relativePath)
        newDirectoryStructureParent.mkdirs()
        List<StatisticReportLine> report = new ArrayList<>()
        String fileName = relativePath + clazz.declaration.name.toString() + ".html"
        StatisticReportLine line = new StatisticReportLine()

        line.name = ClassesDiffReport.getClassFullName(clazz)
        line.link = ""
        line.values.add(Integer.toString(clazz.methods.size()))//equal
        line.values.add(Integer.toString(clazz.methods.size()))//total
        addCoverageInfo(clazz, line)

        line.values.add("")//similarity
        line.values.add(clazz.declaration.extendedTypes.name.toString())
        line.values.add(clazz.declaration.implementedTypes.toString())
        if (produceDiffReport) {
            line.values.add("")//avg percent
            line.values.add("")//per line percent
            line.values.add("")//compare link
        }
        report.add(line)

        clazz.references.eachWithIndex { entry, int i ->
            line = new StatisticReportLine()
            line.name = ClassesDiffReport.getClassFullName(entry)
            line.link = "../" + entry.packageName + "/" + entry.declaration.name.toString() + ".html"

            line.values.add(Integer.toString(clazz.duplicationCount[i]))
            line.values.add(Integer.toString(entry.methods.size()))
            addCoverageInfo(entry, line)

            line.values.add(String.format("%.2f", clazz.similarPercent[i] * 100) + " %")
            line.values.add(entry.declaration.extendedTypes.name.toString())
            line.values.add(entry.declaration.implementedTypes.toString())

            if (produceDiffReport) {
                ClassesDiffReport diff = new ClassesDiffReport(clazz, entry, pathToFiles, produceDiffReport)
                diff.setMostEqualMethod(mostEqualMethod)
                diff.writeClassCompare(reportPath)
                line.values.add(String.format("%.2f", diff.averagePercent * 100) + " %")
                line.values.add(String.format("%.2f", diff.classSimilarity * 100) + " %")
                line.values.add("<a href=\"" + diff.relativeReportPath + "\"'>compare</a>")
            }
            report.add(line)

        }

        List<String> headers
        if (produceDiffReport) {
            headers = new ArrayList<>(headerListClasses)
            headers.add("Compare")
            headers.add("Avg. same name method similarity")
            headers.add("Per line similarity")
        } else {
            headers = headerListClasses
        }

        ReportBuilder.build(filePath + fileName, "Class duplication report", headers, report, new ArrayList<>(), new ArrayList<>())
        fileName
    }

    /**
     * Place coverage information to report
     * @param clazz - class for coverage statistic
     * @param line - output statistic report
     */
    static void addCoverageInfo(SourceClassData clazz, StatisticReportLine line) {
        line.values.add(Integer.toString(clazz.methodCoverage.uncovered))

        if (clazz.methodCoverage.total > 0) {
            line.values.add(Integer.toString(clazz.methodCoverage.total))
        } else {
            int lineCount = 0

            clazz.methods.each {
                String mbody = it.toString()
                List<String> mlines = mbody.split("\r?\n")
                lineCount += mlines.size()
            }

            line.values.add(Integer.toString(lineCount))
        }
    }
}
