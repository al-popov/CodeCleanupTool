package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.statistics.generators.DiffCodeGenerator
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.ast.body.MethodDeclaration
import difflib.DiffRow

/**
 * This class creates report for class comparison in diff like style
 */
class ClassesDiffReport {

    private final SourceClassData firstClass
    private final SourceClassData secondClass

    private float averageSimilarPercent
    private float classSimilarity
    private int methodCount

    private final String pathToFiles

    private Set<MethodDeclaration> equalMethods

    private final boolean produceDiffReport
    private boolean mostEqualMethod

    ClassesDiffReport(SourceClassData first,
                      SourceClassData second,
                      String path,
                      boolean produceDiffReport) {

        firstClass = first
        secondClass = second

        pathToFiles = path
        mostEqualMethod = false

        this.produceDiffReport = produceDiffReport
    }

    void setMostEqualMethod(boolean flag) {
        mostEqualMethod = flag
    }

    /**
     * Average per method(for methods with same name) similarity percent
     * @return
     */
    float getAveragePercent() {
        averageSimilarPercent
    }

    /**
     * Total class similarity for per line comparison
     * @return
     */
    float getClassSimilarity() {
        classSimilarity
    }

    /**
     * Create report path
     * @return
     */
    String getRelativeReportPath() {
        String relativePath = "../" + firstClass.packageName + "/"
        String fileName = firstClass.declaration.name.toString() +
                secondClass.declaration.name.toString() + ".html"

        relativePath + fileName
    }

    /**
     * Create output file for two classes comparison
     * @param reportPath - report path
     * @return
     */
    String writeClassCompare(String reportPath) {
        String filePath = reportPath + "/" + pathToFiles + "/" + firstClass.packageName + "/"
        String relativePath = "../" + firstClass.packageName + "/"
        String fileName = firstClass.declaration.name.toString() +
                secondClass.declaration.name.toString() + ".html"

        File newDirectoryStructureParent = new File(filePath)
        newDirectoryStructureParent.mkdirs()

        methodCount = 0
        averageSimilarPercent = 0

        if (produceDiffReport) {
            List<StatisticReportLine> report = createReport()
            List<String> headerListCompare = new ArrayList<>()
            List<String> description = new ArrayList<>()

            headerListCompare.add(getClassFullName(firstClass))
            headerListCompare.add(getClassFullName(secondClass))

            description.add("Total method count in class " + firstClass.declaration.name.toString() +
                    ": " + firstClass.methods.size())
            description.add("Total method count in class " + secondClass.declaration.name.toString() +
                    ": " + secondClass.methods.size())
            if (methodCount == 0) {
                methodCount = 1
            }

            averageSimilarPercent /= methodCount //method comparation similarity

            description.add("Average per method similarity: " +
                    String.format("%.2f", averageSimilarPercent * 100) + " %")

            description.add("Class similarity: " + String.format("%.2f", classSimilarity * 100) + " %")
            generateSuperClass(description)

            ReportBuilder.build(filePath + fileName, "Diff report", headerListCompare,
                    report, new ArrayList<>(), description)
        } else {
            calculateClassSimilarity()
            if (methodCount == 0) {
                methodCount = 1
            }
            averageSimilarPercent /= methodCount //method comparation similarity
        }

        relativePath + fileName
    }

    /**
     * Create report for report builder
     * @return
     */
    private List<StatisticReportLine> createReport() {
        List<StatisticReportLine> report = new ArrayList<>()
        equalMethods = new HashSet<>()
        firstClass.methods.each { method ->
            if (mostEqualMethod) {
                exploreMostSimilarMethod(method, report)
            } else {
                exploreSameNameMethod(method, report)
            }

        }

        report.add(new StatisticReportLine())
        report.add(new StatisticReportLine())

        StatisticReportLine line = new StatisticReportLine()
        classSimilarity = createDiffOutput(firstClass.sourceCode,
                secondClass.sourceCode, line.values)

        report.add(line)

        report
    }

    /**
     * Create diff for same name method
     * @param method
     * @param report
     */
    private void exploreSameNameMethod(MethodDeclaration method, List<StatisticReportLine> report) {
        StatisticReportLine line = new StatisticReportLine()
        String methodName = method.name.toString() + method.parameters.toString()

        boolean first = true

        secondClass.methods.each {
            String mName = it.name.toString() + it.parameters.toString()

            if (mName.equals(methodName)) {
                float curPercent = createDiffOutput(method.toString(), it.toString(), line.values)
                averageSimilarPercent += curPercent
                methodCount++

                if (curPercent > 0.9) {
                    equalMethods.add(method)
                }

                report.add(line)
                first = false
                line = new StatisticReportLine()
            }
        }

        if (first && line.values.isEmpty()) {
            secondClass.methods.each {
                String mName = it.name.toString()

                if (mName.equals(method.name.toString())) {
                    averageSimilarPercent += createDiffOutput(method.toString(), it.toString(), line.values)
                    methodCount++

                    report.add(line)
                    first = false
                    line = new StatisticReportLine()
                }
            }
        }

        if (first) {
            report.add(line)
        }
    }

    /**
     * Create diff for most similar method
     * @param method
     * @param report
     */
    private void exploreMostSimilarMethod(MethodDeclaration method, List<StatisticReportLine> report) {
        StatisticReportLine line = new StatisticReportLine()
        float mostPercent = 0
        MethodDeclaration method2 = null

        secondClass.methods.each {
            float curPercent = calculateSimilarity(method.toString(), it.toString())
            if (curPercent > mostPercent) {
                mostPercent = curPercent
                method2 = it
            }
        }

        if (method2 != null) {
            mostPercent = createDiffOutput(method.toString(), method2.toString(), line.values)
            averageSimilarPercent += mostPercent
            methodCount++

            if (mostPercent > 0.9) {
                equalMethods.add(method)
            }

            report.add(line)
        }
    }

    /**
     * Generate super class for this two classes based on most similar modules
     * @param description - created class placed in description section under table
     */
    private void generateSuperClass(List<String> description) {
        if (!equalMethods.isEmpty()) {
            description.add("Base Class suggestion: ")
            String className = "public "

            if (firstClass.declaration.abstract) {
                className += "abstract "
            }

            className += firstClass.declaration.name.toString() + "Base "

            firstClass.declaration.extendedTypes.each {
                className += "extends " + it.name.toString() + " "
            }

            className += "{"

            description.add(className)

            equalMethods.each {
                List<String> lines = it.toString().split("\r?\n")
                lines.each { s -> description.add(s) }
            }

            description.add("}")
        }
    }

    /**
     * Calculate class similarity in case we don't produce report
     */
    private void calculateClassSimilarity() {
        firstClass.methods.each { method ->
            String methodName = method.name.toString() + method.parameters.toString()
            boolean first = true

            if (mostEqualMethod) {
                float mostPercent = 0
                secondClass.methods.each {
                    float curPercent = calculateSimilarity(method.toString(), it.toString())
                    if (curPercent > mostPercent) {
                        mostPercent = curPercent
                        first = false
                    }
                }

                if (!first) {
                    averageSimilarPercent += mostPercent
                    methodCount++
                }
            } else {
                secondClass.methods.each {
                    String mName = it.name.toString() + it.parameters.toString()

                    if (mName.equals(methodName)) {
                        float curPercent = calculateSimilarity(method.toString(), it.toString())
                        averageSimilarPercent += curPercent
                        methodCount++
                        first = false
                    }
                }

                if (first) {
                    secondClass.methods.each {
                        String mName = it.name.toString()

                        if (mName.equals(method.name.toString())) {
                            averageSimilarPercent += calculateSimilarity(method.toString(), it.toString())
                            methodCount++
                        }
                    }
                }
            }
        }
        classSimilarity = calculateSimilarity(firstClass.declaration.toString(),
                secondClass.declaration.toString())
    }

    /**
     * Class full name with package
     * @param clazz
     * @return
     */
    static String getClassFullName(SourceClassData clazz) {
        clazz.packageName + "." + clazz.declaration.name.toString()
    }

    static String getClassFullName(ClassOrInterfaceDeclaration clazz, String packageName) {
        packageName + "." + clazz.name.toString()
    }

    /**
     * Diff like class comparation output
     * @param originalCode
     * @param revisedCode
     * @param lines - output report
     * @return - method similarity percent
     */
    private static float createDiffOutput(String originalCode, String revisedCode, List<String> lines) {
        final List<DiffRow> diffRows = DiffCodeGenerator.getDiffRows(originalCode, revisedCode)
        String mBody1 = "<table border=\"0\" cellspacing=\"0\" cellpadding=\"0\"" +
                " style=\"text-align:left;\" width = \"100%\">"

        String mBody2 = mBody1
        int equalcount = 0

        diffRows.each {
            String oldLineStyle = ""
            String newLineStyle = ""

            switch (it.tag) {
                case DiffRow.Tag.EQUAL:
                    equalcount++
                    break

                case DiffRow.Tag.CHANGE:
                    if (it.oldLine.isEmpty()) {
                        oldLineStyle = "background:lightgrey"
                        newLineStyle = "background:lightblue"
                    } else if (it.newLine.isEmpty()) {
                        oldLineStyle = "background:lightblue"
                        newLineStyle = "background:lightgrey"
                    } else {
                        oldLineStyle = "background:lightgreen"
                        newLineStyle = "background:lightgreen"
                    }
                    break

                case DiffRow.Tag.DELETE:
                    oldLineStyle = "background:lightblue"
                    newLineStyle = "background:lightgrey"
                    break

                case DiffRow.Tag.INSERT:
                    oldLineStyle = "background:lightgrey"
                    newLineStyle = "background:lightblue"
                    break

                default:
                    break
            }

            mBody1 += getHighlightedLine(it.oldLine, oldLineStyle)
            mBody2 += getHighlightedLine(it.newLine, newLineStyle)
        }

        mBody1 += "</table>"
        mBody2 += "</table>"

        lines.add(mBody1)
        lines.add(mBody2)
        float percentage = equalcount
        percentage / diffRows.size()
    }

    /**
     * Calculate similarity if we do not produce report
     * @param originalCode
     * @param revisedCode
     * @return
     */
    static float calculateSimilarity(String originalCode, String revisedCode) {
        final List<DiffRow> diffRows = DiffCodeGenerator.getDiffRows(originalCode, revisedCode)

        //excluding method declaration from comparation
        if (diffRows.size() > 0) {
            diffRows.remove(0)
        }

        float count = diffRows.size()

        diffRows.removeIf { it.tag != DiffRow.Tag.EQUAL }
        int count2 = diffRows.size()
        diffRows.removeIf { it.oldLine.equals("{") || it.oldLine.equals("}") }
        count += diffRows.size() - count2
        if (count <= 0) count = 1

        diffRows.size() / count
    }

    /**
     * Html highlighted string output for compared code lines
     * @param line
     * @param style
     * @return
     */
    private static String getHighlightedLine(String line, String style) {
        if (line.isEmpty()) {
            line = "&nbsp"
        }

        "<tr style=\"$style\"><td>$line</td></tr>"
    }
}
