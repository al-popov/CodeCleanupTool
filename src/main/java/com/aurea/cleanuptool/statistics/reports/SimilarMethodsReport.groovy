package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Statistic by similar method code
 */
@Profile('similar-method')
@Component
@Log4j2
class SimilarMethodsReport extends SimilarClassesReport {

    SimilarMethodsReport(CoverageMethodStatistic coverageMethodStatistic,
                         ProjectConfiguration projectConfiguration) {
        super(coverageMethodStatistic, projectConfiguration)

        setReportName("similar-methods")
        setMostEqualMethod(true)
    }

    /**
     * Write class details statistic
     * @param sourceClass
     * @return
     */
    @Override
    protected String writeSimilarClasses(SourceClassData sourceClass) {
        String classes = ""
        sourceClass.references.eachWithIndex { dupClass, int i ->
            classes += dupClass.declaration.name.toString() + " similarity percent: "
            classes += String.format("%.2f", sourceClass.similarPercent[i] * 100) + " %, "
        }
        classes
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.SIMILAR_METHOD
    }
}
