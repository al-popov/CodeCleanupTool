package com.aurea.cleanuptool.statistics.generators

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2

@Log4j2
class BaseStatisticGenerator implements StatisticGenerator {


    private final JacocoCoverageService jacocoCoverageService
    private final ProjectConfiguration projectConfiguration

    private final List<SourceClassData> generatedReport = new ArrayList<>()

    BaseStatisticGenerator(JacocoCoverageService jacocoCoverageService,
                           ProjectConfiguration projectConfiguration) {

        this.jacocoCoverageService = jacocoCoverageService
        this.projectConfiguration = projectConfiguration
    }

    protected JacocoCoverageService getJacocoCoverageService() {
        jacocoCoverageService
    }

    protected ProjectConfiguration getProjectConfiguration() {
        projectConfiguration
    }

    @Override
    void processUnitSourceClass(UnitSourceClass sourceClass) {

    }

    @Override
    void setSourceClassList(List<UnitSourceClass> sourceClassList) {

    }

    protected void addUnitSourceClass(UnitSourceClass sourceClass) {
        SourceClassData source = new SourceClassData()

        source.packageName = sourceClass.packageName
        source.declaration = sourceClass.declaration
        source.methods = sourceClass.methods
        source.references = new ArrayList<>()
        source.duplicationCount = new ArrayList<>()
        source.similarPercent = new ArrayList<>()
        source.sourceCode = sourceClass.sourceCode

        source.methodCoverage = jacocoCoverageService.getClassCoverage(sourceClass.packageName,
                sourceClass.declaration.name.toString())

        source.methods.each { srcMethod ->
            source.methodNames.add(srcMethod.name.toString())
            String param = srcMethod.parameters.toString()
            if (param.length() > 2) {
                source.methodSignature.add(param)
            }
        }

        generatedReport.each {
            updateSourceClassData(source, it)
            updateSourceClassData(it, source)
        }

        if (!source.references.isEmpty()) {
            log.info("Found new reference class" + source.declaration.name.toString()
                    + " for " + source.declaration.name.toString() + ", method count: "
                    + source.references.size())
        }

        generatedReport.add(source)
    }

    protected void updateSourceClassData(SourceClassData baseClass, SourceClassData newClass) {

    }

    List<SourceClassData> getReport() {
        generatedReport
    }

    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.NONE
    }
}
