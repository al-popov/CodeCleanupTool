package com.aurea.cleanuptool.statistics.reports

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile('similar-signature')
@Component
@Log4j2
class SimilarSignatureReport extends SimilarClassesReport {

    SimilarSignatureReport(CoverageMethodStatistic coverageMethodStatistic,
                           ProjectConfiguration projectConfiguration) {
        super(coverageMethodStatistic, projectConfiguration)

        setReportName("similar-signature")
        setMostEqualMethod(true)
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.SIMILAR_SIGNATURE
    }
}
