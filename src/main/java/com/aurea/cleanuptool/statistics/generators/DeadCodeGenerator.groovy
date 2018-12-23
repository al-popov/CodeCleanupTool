package com.aurea.cleanuptool.statistics.generators

import com.aurea.cleanuptool.config.ProjectConfiguration
import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceClassData
import com.aurea.cleanuptool.source.UnitSourceClass
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import groovy.util.logging.Log4j2
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile('dead-code')
@Component
@Log4j2
class DeadCodeGenerator extends BaseStatisticGenerator {

    DeadCodeGenerator(JacocoCoverageService jacocoCoverageService,
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

    /**
     * Update similar class information compared with newly added class
     * @param baseClass - source class
     * @param newClass - class for comparison
     */
    @Override
    protected void updateSourceClassData(SourceClassData baseClass, SourceClassData newClass) {
        if (baseClass.packageName == newClass.packageName ||
                newClass.sourceCode.contains("import " + baseClass.packageName +
                "." + baseClass.declaration.name.toString() + ";")) {

            if (newClass.sourceCode.contains(" " + baseClass.declaration.name.toString() + ".") ||
                    newClass.sourceCode.contains(" " + baseClass.declaration.name.toString() + "(") ||
                    newClass.sourceCode.contains(" " + baseClass.declaration.name.toString() + " ") ||
                    newClass.sourceCode.contains("\t" + baseClass.declaration.name.toString() + ".") ||
                    newClass.sourceCode.contains("\t" + baseClass.declaration.name.toString() + "(") ||
                    newClass.sourceCode.contains("\t" + baseClass.declaration.name.toString() + " ")) {

                baseClass.references.add(newClass)
            }
        }
    }

    @Override
    CoverageMethodStatistic.ReportType getReportType() {
        CoverageMethodStatistic.ReportType.DEAD_CODE
    }
}
