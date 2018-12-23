package com.aurea.cleanuptool.statistics;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.Assert.assertEquals;

import com.aurea.cleanuptool.config.ProjectConfiguration;
import com.aurea.cleanuptool.coverage.*;
import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.Whitebox;
import java.io.InputStream;
import java.util.Map;

public class CoverageMethodStatisticTest {

    private CoverageMethodStatistic coverageMethodStatistic;
    private ProjectConfiguration projectConfiguration;
    private JacocoCoverageService jacocoCoverageService;
    private JacocoCoverageRepository jacocoCoverageRepository;

    @Before
    public void setup() throws Exception {
        InputStream stream = JacocoCoverageRepository.class.getResourceAsStream("jacoco.xml");

        System.out.println(stream);

        jacocoCoverageRepository = JacocoCoverageRepository.fromStream(stream);
        projectConfiguration = new ProjectConfiguration();
        Whitebox.setInternalState(projectConfiguration,"src",
                JacocoCoverageRepository.class.getResource(".").getPath());

        jacocoCoverageService = new JacocoCoverageService(projectConfiguration);
        Whitebox.setInternalState(jacocoCoverageService, "jacocoCoverageRepository", jacocoCoverageRepository);
        Whitebox.invokeMethod(jacocoCoverageService, "getRepoCoverage");
        coverageMethodStatistic = new CoverageMethodStatistic(projectConfiguration, jacocoCoverageService);
    }

    @Test
    public void getCoveredMethodsShouldBeTwo() {
        int count = jacocoCoverageService.getCoveredMethods();

        assertThat(count).isEqualTo(2);
    }
}
