package com.aurea.cleanuptool.statistics.generators

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import com.aurea.cleanuptool.statistics.reports.ClassesDiffReport
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile('similar-method')
@Component
@Log4j2
class SimilarMethodGenerator extends BaseStatisticGenerator {

    private int percentLevel
    private final boolean groupByExtends

    SimilarMethodGenerator(JacocoCoverageService jacocoCoverageService,
                           ProjectConfiguration projectConfiguration) {
        super(jacocoCoverageService, projectConfiguration)
        percentLevel = projectConfiguration.similarPercent
        groupByExtends = projectConfiguration.groupByExtends
    }

    /**
     * Explore each unit to get information about classes which have similar code methods,
     * and aggregate them according to minimum percent of equal methods - projectConfiguration.similarPercent.
     * @param sourceClass - class source representation given by com.github.javaparser
     */
    @Override
    void processUnitSourceClass(UnitSourceClass sourceClass) {
        addUnitSourceClass(sourceClass)
    }

    /**
     * Compare each class for methods with similar code
     * @param newClass - newClass class
     * @param oldClass - class for comparison
     */
    protected void updateSourceClassData(SourceClassData newClass, SourceClassData oldClass) {
        if (groupByExtends && !newClass.declaration.extendedTypes.toString().equals(
                oldClass.declaration.extendedTypes.toString())) {
            return
        }

        int methodCount = newClass.methods.size()
        int simCount = 0
        float percent = 0

        if (methodCount < oldClass.methods.size()) {
            methodCount = oldClass.methods.size()
        }

        newClass.methods.each { srcMethod ->
            float curPercent = 0

            oldClass.methods.each { dupMethod ->
                float cur = ClassesDiffReport.calculateSimilarity(srcMethod.toString(),
                        dupMethod.toString())

                if (cur > curPercent) {
                    curPercent = cur
                }
            }
            percent += curPercent
            if (curPercent >= percentLevel) {
                simCount++
            }
        }
        percent /= methodCount

        if (percent * 100 >= percentLevel) {
            newClass.references.add(oldClass)
            newClass.duplicationCount.add(simCount)
            newClass.similarPercent.add(percent)
        }
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.SIMILAR_METHOD
    }
}
