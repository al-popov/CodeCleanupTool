package com.aurea.cleanuptool.coverage

import com.aurea.coverage.unit.MethodCoverage
import com.aurea.cleanuptool.config.ProjectConfiguration
import com.google.common.collect.ImmutableMap
import groovy.util.logging.Log4j2
import org.springframework.stereotype.Component

/**
 * Jacoco service for easy repository usage
 */
@Component
@Log4j2
class JacocoCoverageService {

    private final JacocoCoverageRepository jacocoCoverageRepository

    private ImmutableMap<String, Map<String, Map<String, MethodCoverage>>> repoCoverage

    private int totalUncovered = 0
    private int totalCovered = 0
    private int totalMethods = 0
    private int totalCoveredMethods = 0

    JacocoCoverageService(ProjectConfiguration projectConfiguration) {
        try {
            jacocoCoverageRepository = JacocoCoverageRepository.fromFile(projectConfiguration.jacoco)
            log.info("JaCoCo coverage repository ser to " + projectConfiguration.jacoco + "/jacoco.xml")
            getRepoCoverage()
        } catch (Exception e) {
            log.info("JaCoCo not set, so we have no information about coverage")
            log.info(e.toString())
            repoCoverage = ImmutableMap.copyOf(new HashMap<String, Map<String, Map<String, MethodCoverage>>>())
        }
    }

    private getRepoCoverage() throws NullPointerException {
        repoCoverage = jacocoCoverageRepository.getPackagesCoverage()

        repoCoverage.each {
            it.value.each { entry ->
                entry.value.each { ent ->
                    totalMethods++

                    totalUncovered += ent.value.instructionUncovered
                    totalCovered += ent.value.instructionCovered

                    if (ent.value.instructionUncovered == 0) {
                        totalCoveredMethods++
                    }
                }
            }
        }
    }

    /**
     * Total uncovered locs for repository
     * @return
     */
    int getTotalUncovered() {
        totalUncovered
    }

    /**
     * Total covered locs for repository
     * @return
     */
    int getTotalCovered() {
        totalCovered
    }

    /**
     * Total methods in repository
     * @return
     */
    int getTotalMethods() {
        totalMethods
    }

    /**
     * Total covered methods in repository
     * @return
     */
    int getCoveredMethods() {
        totalCoveredMethods
    }

    /**
     * Coverage information for method
     * @param pkgName - method Package
     * @param clsName - method Class
     * @param methodName - method Name
     * @param params - method params
     * @return - method coverage information
     */
    MethodCoverage getMethodCoverage(String pkgName, String clsName, String methodName, String params) {
        MethodCoverage cover = null
        if (repoCoverage.containsKey(pkgName)) {
            def pkg = repoCoverage[pkgName]
            if (pkg.containsKey(clsName)) {
                def cls = pkg[clsName]

                if (cls.containsKey(methodName + params)) {
                    cover = cls[methodName + params]
                }
            }
        }

        cover
    }

    /**
     * Coverage information for class
     * @param pkgName - class package
     * @param clsName - class name
     * @return - class coverage information
     */
    MethodCoverage getClassCoverage(String pkgName, String clsName) {
        int instructionCovered = 0
        int instructionUncovered = 0

        int covered = 0
        int uncovered = 0

        if (repoCoverage.containsKey(pkgName)) {
            def pkg = repoCoverage[pkgName]
            if (pkg.containsKey(clsName)) {
                def cls = pkg[clsName]

                cls.each {
                    instructionCovered += it.value.instructionCovered
                    instructionUncovered += it.value.instructionUncovered

                    covered += it.value.covered
                    uncovered += it.value.uncovered
                }
            }
        }

        new MethodCoverage("", instructionCovered, instructionUncovered, covered, uncovered)
    }
}
