package com.aurea.cleanuptool.statistics

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.generators.StatisticGenerator
import groovy.util.logging.Log4j2
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Produce all coverage methods statistics
 */
@Component
@Log4j2
class CoverageMethodStatistic {

    static final enum ReportType {
        NONE, DEAD_CODE, LONG_CLASS, SIMILAR_CLASS, SIMILAR_METHOD, SIMILAR_SIGNATURE
    }

    private final ProjectConfiguration projectConfiguration
    private final JacocoCoverageService jacocoCoverageService

    private final List<UnitSourceClass> sourceClassList = new ArrayList<>()

    @Autowired
    private final List<StatisticGenerator> statisticsGenerator

    CoverageMethodStatistic(ProjectConfiguration projectConfiguration,
                            JacocoCoverageService jacocoCoverageService) {

        this.projectConfiguration = projectConfiguration
        this.jacocoCoverageService = jacocoCoverageService
    }

    void exploreUnit(UnitSourceClass sourceClass) {
        if (sourceClass.declaration && sourceClass.declaration.name) {
            sourceClassList.add(sourceClass)
            statisticsGenerator.each { it.processUnitSourceClass(sourceClass) }
        }
    }

    void updateStatistics() {
        statisticsGenerator.each { it.setSourceClassList(sourceClassList) }
    }

    List<SourceClassData> getReport(ReportType type) {
        List<SourceClassData> source = new ArrayList<>()

        statisticsGenerator.findAll() {
            it.reportType == type
        }.each {
            source = it.report
        }

        source
    }
}
