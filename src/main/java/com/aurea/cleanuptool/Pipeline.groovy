package com.aurea.cleanuptool

import com.aurea.cleanuptool.source.SourceFilter
import com.aurea.cleanuptool.source.SourcesRepository
import com.aurea.cleanuptool.source.Unit
import com.aurea.cleanuptool.source.UnitSource
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import com.aurea.cleanuptool.statistics.CoverageStatisticsReport
import groovy.util.logging.Log4j2
import one.util.streamex.StreamEx
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
@Log4j2
class Pipeline {

    final CoverageStatisticsReport coverageStatisticsReport

    final UnitSource source
    final SourceFilter sourceFilter

    @Autowired
    final SourcesRepository sourcesRepository
    @Autowired
    final CoverageMethodStatistic coverageMethodStatistic

    @Autowired
    Pipeline(UnitSource unitSource,
             SourceFilter sourceFilter,
             CoverageStatisticsReport coverageStatisticsReport) {

        this.source = unitSource
        this.sourceFilter = sourceFilter
        this.coverageStatisticsReport = coverageStatisticsReport
    }

    void start() {
        log.info "Getting units from $source"
        StreamEx<Unit> units = source.units(sourceFilter)

        long totalUnits = source.size(sourceFilter)
        AtomicInteger counter = new AtomicInteger()

        log.info "Collecting class implementation for ${totalUnits} units"

        units.map {
            log.info "${counter.incrementAndGet()} / $totalUnits: $it.fullName"
            sourcesRepository.parseUnit(it)
        }
        .map { it }
                .each { coverageMethodStatistic.exploreUnit(it) }

        coverageStatisticsReport.produceReport()
    }
}
