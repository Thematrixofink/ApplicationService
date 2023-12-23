package com.hitices.pressure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.entity.*;
import org.apache.jmeter.samplers.SampleResult;

import java.util.List;

public interface PressureMeasurementService {

    void measure(TestPlanVO testPlanVO);

    void measure(int testPlanId) throws JsonProcessingException;

    void addResults(SampleResult result);

    List<SampleResult> getResults();

    List<TestPlanVO> getAllTestPlans() throws JsonProcessingException;

    TestPlanVO getTestPlanById(int testPlanId) throws JsonProcessingException;

    int deleteTestPlan(int testPlanId);

    int addTestPlan(TestPlanVO testPlan);

    void updateTestPlan(TestPlanVO testPlanVO);

    void addTimers(List<TimerVO> timers, int threadGroupId);

    int addTestResult(TestResultVO testResultVO);

    List<TestResultVO> getTestResultsByPlanId(int planId);

    TestResultVO getTestResultByResultId(int testResultId);
}
