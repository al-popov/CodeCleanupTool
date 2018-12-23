package com.aurea.cleanuptool.statistics.generators

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile('similar-classes')
@Component
@Log4j2
class SimilarClassesGenerator extends BaseStatisticGenerator {

    private int percentLevel
    private final boolean groupByExtends

    SimilarClassesGenerator(JacocoCoverageService jacocoCoverageService,
                            ProjectConfiguration projectConfiguration) {
        super(jacocoCoverageService, projectConfiguration)
        percentLevel = projectConfiguration.similarPercent
        groupByExtends = projectConfiguration.groupByExtends
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

    /**
     * Update similar class information compared with newly added class
     * @param newClass - newClass class
     * @param oldClass - class for comparison
     */
    protected void updateSourceClassData(SourceClassData newClass, SourceClassData oldClass) {
        if (groupByExtends && !newClass.declaration.extendedTypes.toString().equals(
                oldClass.declaration.extendedTypes.toString())) {
            return
        }
        int methodCount = 0

        newClass.methodNames.each { methodName ->
            if (oldClass.methodNames.contains(methodName)) {
                methodCount++
            }
        }

        if (methodCount > 0) {
            float percent1 = methodCount / oldClass.methods.size()
            float percent2 = methodCount / newClass.methods.size()

            if (percent1 > percent2) {
                percent1 = percent2
            }

            if (percent1 * 100 >= percentLevel) {
                newClass.references.add(oldClass)
                newClass.duplicationCount.add(methodCount)
                newClass.similarPercent.add(percent1)
            }
        }
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.SIMILAR_CLASS
    }
}
