package com.hitices.pressure.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.common.MResponse;
import com.hitices.pressure.entity.*;
import com.hitices.pressure.service.PressureMeasurementService;
import com.hitices.pressure.utils.ExcelGenerator;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/pressureMeasurement")
public class PressureMeasurementController {

    @Autowired
    private PressureMeasurementService pressureMeasurementService;

    @GetMapping("/testPlans")
    public MResponse<List<TestPlanVO>> getAllTestPlans() {
        try {
            return new MResponse<List<TestPlanVO>>().successMResponse().data(pressureMeasurementService.getAllTestPlans());
        } catch (JsonProcessingException e) {
            return new MResponse<List<TestPlanVO>>().failedMResponse();
        }
    }

    @GetMapping("getTestPlanById")
    public MResponse<TestPlanVO> getTestPlanById(@RequestParam("testPlanId") int testPlanId) {
        try {
            return new MResponse<TestPlanVO>().successMResponse().data(pressureMeasurementService.getTestPlanById(testPlanId));
        } catch (JsonProcessingException e) {
            return new MResponse<TestPlanVO>().failedMResponse();
        }
    }

    @PostMapping("/pressure")
    public MResponse<List<SampleResult>> pressureMeasurement(@RequestBody TestPlanVO testPlanVO) {
        pressureMeasurementService.measure(testPlanVO);
        return new MResponse<List<SampleResult>>().successMResponse().data(pressureMeasurementService.getResults());
    }

    @GetMapping("/measure")
    public MResponse<Boolean> measure(@RequestParam("testPlanId") int testPlanId) {
        try {
            pressureMeasurementService.measure(testPlanId);
        } catch (JsonProcessingException e) {
            return new MResponse<Boolean>().failedMResponse().data(false);
        }

        return new MResponse<Boolean>().successMResponse().data(true);
    }

    @PostMapping("/createTestPlan")
    public MResponse<Object> createTestPlan(@RequestBody TestPlanVO testPlanVO) {
        if (pressureMeasurementService.addTestPlan(testPlanVO) <= 0) {
            return new MResponse<>().failedMResponse();
        }
        return new MResponse<>().successMResponse();
    }

    @PostMapping("/createBoundaryTest")
    public MResponse<Object> createBoundaryTest(@RequestBody TestPlanVO testPlanVO) {
        if (pressureMeasurementService.addTestPlan(testPlanVO) <= 0) {
            return new MResponse<>().failedMResponse();
        }
        return new MResponse<>().successMResponse();
    }

    @GetMapping("/deleteTestPlan")
    public MResponse<Object> deleteTestPlan(@RequestParam("testPlanId") int testPlanId) {
        int res = pressureMeasurementService.deleteTestPlan(testPlanId);
        return new MResponse<>().successMResponse().set("rows", res);
    }

    @PostMapping("/updateTestPlan")
    public MResponse<Object> updateTestPlan(@RequestBody TestPlanVO testPlanVO){
        pressureMeasurementService.updateTestPlan(testPlanVO);
        return new MResponse<>().successMResponse();
    }

    @GetMapping("/updateAggregateReport")
    public MResponse<Object> updateAggregateReport(int planId) {
        if(pressureMeasurementService.updateAggregateReport(planId) <= 0) {
            return new MResponse<>().failedMResponse();
        }
        return new MResponse<>().successMResponse();
    }

    @GetMapping("/createAggregateReport")
    public MResponse<Object> createAggregateReport(int planId) {
        if(pressureMeasurementService.addAggregateReport(planId)) {
            return new MResponse<>().successMResponse();
        }
        return new MResponse<>().failedMResponse();
    }

    @GetMapping("/getTestResultsByID")
    public MResponse<List<TestResultVO>> getTestResultsById(int testPlanId) {
        return new MResponse<List<TestResultVO>>().successMResponse().data(pressureMeasurementService.getTestResultsByPlanId(testPlanId));
    }

    @GetMapping("/getTestResultByResultId")
    public MResponse<TestResultVO> getTestResultByResultId(int testResultId) {
        return new MResponse<TestResultVO>().successMResponse().data(pressureMeasurementService.getTestResultByResultId(testResultId));
    }

    @GetMapping("/getAggregateReportByPlanId")
    public MResponse<AggregateReportVO> getAggregateReportByPlanId(int planId) {
        return new MResponse<AggregateReportVO>().successMResponse().data(pressureMeasurementService.getAggregateReportByPlanId(planId));
    }

    @GetMapping("/getStartAndEndOfTest")
    public MResponse<int[]> getStartAndEndOfTest(int planId) {
        int[] startAndEnd = pressureMeasurementService.getStartAndEndOfTest(planId);
        if(startAndEnd[0] == -1) {
            return new MResponse<int[]>().failedMResponse().data(startAndEnd);
        }
        return new MResponse<int[]>().successMResponse().data(startAndEnd);
    }



    @PostMapping("/aggregateReportExcel")
    public ResponseEntity<InputStreamResource> generateExcel(
            @RequestParam int planId,
            @RequestBody MonitorParam monitorParam
    ) throws IOException {
        AggregateReportVO aggregateReportVO = pressureMeasurementService.getAggregateReportByPlanId(planId);
        ArrayList<HardwareRecord> cpuUsage = monitorParam.getCpuUsage();
        ArrayList<HardwareRecord> memoryUsage = monitorParam.getMemoryUsage();
        ArrayList<NetworkRecord> byteTransmitted = monitorParam.getByteTransmitted();
        ArrayList<NetworkRecord> byteReceived = monitorParam.getByteReceived();
        InputStream inputStream = ExcelGenerator.generateAggregateReportExcel(aggregateReportVO, cpuUsage, memoryUsage, byteTransmitted, byteReceived);
//        InputStream inputStream = ExcelGenerator.generateAggregateReportExcel(aggregateReportVO, cpuUsage, memoryUsage, byteTransmitted, byteReceived);

        if (inputStream != null) {
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(inputStream.available())
                    .body(resource);
        } else {
            // 处理InputStream为null的情况，可以返回适当的错误响应
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(null);
        }
    }


}
