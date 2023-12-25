package com.hitices.pressure.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
}
