package com.aurea.cleanuptool.coverage;

import static java.util.stream.Collectors.toMap;

import com.aurea.coverage.CoverageIndex;
import com.aurea.coverage.parser.JacocoParsers;
import com.aurea.coverage.unit.ClassCoverage;
import com.aurea.coverage.unit.MethodCoverage;
import com.aurea.coverage.unit.Named;
import com.aurea.coverage.unit.PackageCoverage;
import com.google.common.collect.ImmutableMap;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Stream;

/**
 * Jacoco coverage repository based on com.aurea.coverage.parser.JacocoParsers
 */
public class JacocoCoverageRepository {

    private final CoverageIndex index;

    private JacocoCoverageRepository(CoverageIndex index) {
        this.index = index;
    }

    /**
     * Create repository for jacoco.xm in path
     *
     * @param path - path to jacoco.xml file
     * @return generates this class
     */
    public static JacocoCoverageRepository fromFile(String path) {
        Path pathToJacoco = Paths.get(path);

        return new JacocoCoverageRepository(JacocoParsers.fromXml(pathToJacoco));
    }

    /**
     * Create repository for jacoco.xm in path
     *
     * @param stream - opened jacoco.xml file as a stream
     * @return generates this class
     */
    public static JacocoCoverageRepository fromStream(InputStream stream) {
        return new JacocoCoverageRepository(JacocoParsers.fromXml(stream));
    }

    /**
     * Dicoveres the repository for coverage information
     *
     * @return <pakage, <class, <mehodname, Coverage method data>>>
     */
    public ImmutableMap<String, Map<String, Map<String, MethodCoverage>>> getPackagesCoverage() {
        Stream<PackageCoverage> packageCoverages = index.getModuleCoverage().packageCoverages();

        Collector<PackageCoverage, ?, Map<String, Map<String, Map<String, MethodCoverage>>>>
                mapPackageCoveragesToNames = toMap(Named::getName, pc -> {
            Collector<ClassCoverage, ?, Map<String, Map<String, MethodCoverage>>>
                    mapClassCoveragesToMethodCoverages = toMap(Named::getName, cc -> {
                Collector<MethodCoverage, ?, Map<String, MethodCoverage>>
                        mapMethodCoveragesToNames = toMap(Named::getName, Function.identity(),
                        (mc1, mc2) -> {
                            if (mc1.getTotal() > mc2.getTotal()) {
                                return mc1;
                            } else {
                                return mc2;
                            }
                        });

                return cc.methodCoverages().distinct().collect(mapMethodCoveragesToNames);
            });

            return pc.classCoverages().collect(mapClassCoveragesToMethodCoverages);
        });

        Map<String, Map<String, Map<String, MethodCoverage>>> collected =
                packageCoverages.collect(mapPackageCoveragesToNames);

        return ImmutableMap.copyOf(collected);
    }

}
