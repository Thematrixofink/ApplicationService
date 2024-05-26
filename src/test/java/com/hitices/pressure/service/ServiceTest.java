package com.hitices.pressure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.entity.TestPlanVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.hitices.pressure.utils.JMeterUtil;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Autowired
    private PressureMeasurementService pressureMeasurementService;

    @Test
    public void testAddAggregateReport() {
        pressureMeasurementService.addAggregateReport(17);
    }

    @Test
    public void updateAggregateReport() {
        pressureMeasurementService.updateAggregateReport(17);
    }

    @Test
    public void createAggregateReport() {
        pressureMeasurementService.addAggregateReport(17);
    }

    @Test
    public void testForSaveConfig() throws IOException {
        TestPlanVO testPlanVO = pressureMeasurementService.getTestPlanById(15);
        System.out.println(testPlanVO.getStatus());
        JMeterUtil.saveTestPlan(testPlanVO, pressureMeasurementService);
    }

    @Test
    public void testGetStartAndEnd() {
        System.out.println(pressureMeasurementService.getStartAndEndOfTest(17)[0]);
    }
}
