package com.aurea.cleanuptool.statistics.generators

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile(['long-method', 'long-class'])
@Component
@Log4j2
class LongClassesGenerator extends BaseStatisticGenerator {

    LongClassesGenerator(JacocoCoverageService jacocoCoverageService,
                         ProjectConfiguration projectConfiguration) {
        super(jacocoCoverageService, projectConfiguration)
    }

    /**
     * Explore each unit to get information about classes which have methods with exactly same name,
     * and aggregate them according to minimum percent of equal methods - projectConfiguration.similarPercent.
     * @param sourceClass - class source representation given by com.github.javaparser
     */
    @Override
    void processUnitSourceClass(UnitSourceClass sourceClass) {
        addUnitSourceClass(sourceClass)
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.LONG_CLASS
    }
}
