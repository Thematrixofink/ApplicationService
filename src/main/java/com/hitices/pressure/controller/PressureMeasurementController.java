package com.hitices.pressure.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.common.MResponse;
import com.hitices.pressure.entity.TestPlanVO;
import com.hitices.pressure.entity.TestResultVO;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/getTestResultsByID")
    public MResponse<List<TestResultVO>> getTestResultsById(int testPlanId) {
        return new MResponse<List<TestResultVO>>().successMResponse().data(pressureMeasurementService.getTestResultsByPlanId(testPlanId));
    }

    @GetMapping("/getTestResultByResultId")
    public MResponse<TestResultVO> getTestResultByResultId(int testResultId) {
        return new MResponse<TestResultVO>().successMResponse().data(pressureMeasurementService.getTestResultByResultId(testResultId));
    }
}
