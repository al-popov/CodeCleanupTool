package com.aurea.cleanuptool.config

import com.aurea.cleanuptool.coverage.JacocoCoverageService
import com.aurea.cleanuptool.source.SourceFilter
import com.aurea.cleanuptool.source.SourceFilters
import com.aurea.cleanuptool.statistics.CoverageMethodStatistic
import com.aurea.cleanuptool.statistics.CoverageStatisticsReport
import com.github.javaparser.JavaParser
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

import javax.annotation.PostConstruct

@Configuration
@EnableAspectJAutoProxy
class BaseConfig {

    @PostConstruct
    void disableCommentNodes() {
        JavaParser.getStaticConfiguration().setStoreTokens(false)
        JavaParser.getStaticConfiguration().setAttributeComments(false)
    }

    @Bean
    StopWatchAspect stopWatchAspect() {
        new StopWatchAspect()
    }

    @Bean
    SourceFilter sourceFilter() {
        SourceFilters.empty()
    }

    @Bean
    TypeSolver combinedTypeSolver(ProjectConfiguration projectConfiguration) {
        def solver = new CombinedTypeSolver(new ReflectionTypeSolver())

        solver.add(new JavaParserTypeSolver(new File(projectConfiguration.src)))

        projectConfiguration.resolvePaths
                .collect { new File(it) }
                .findAll { it.exists() && it.isDirectory() }
                .each { solver.add(new JavaParserTypeSolver(it)) }

        projectConfiguration.resolveJars.stream()
                .map { new File(it) }
                .filter { it.exists() }
                .peek { addIndividualJarFileSolver(solver, it) }
                .filter { it.isDirectory() }
                .each {
            it.traverse {
                addIndividualJarFileSolver(solver, it)
            }
        }

        solver
    }

    static void addIndividualJarFileSolver(CombinedTypeSolver solver, File file) {
        if (file.isFile() && file.name.toLowerCase().endsWith('.jar')) {
            solver.add(new JarTypeSolver(file.path))
        }
    }

    @Bean
    JavaParserFacade javaParserFacade(TypeSolver solver) {
        JavaParserFacade.get(solver)
    }

    @Bean
    CoverageStatisticsReport coverageStatisticsReport(ProjectConfiguration projectConfiguration,
                                                      CoverageMethodStatistic coverageMethodStatistic) {
        new CoverageStatisticsReport(projectConfiguration, coverageMethodStatistic)
    }

    @Bean
    CoverageMethodStatistic coverageMethodStatistic(ProjectConfiguration projectConfiguration,
                                                    JacocoCoverageService jacocoCoverageService) {
        new CoverageMethodStatistic(projectConfiguration, jacocoCoverageService)
    }
}
