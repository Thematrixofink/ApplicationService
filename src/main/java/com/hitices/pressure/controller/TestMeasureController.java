package com.hitices.pressure.controller;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.common.MResponse;
import com.hitices.pressure.entity.TestPlanVO;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/testMeasure")
public class TestMeasureController {

    @GetMapping("/singleTest")
    public MResponse<Integer> getAllTestPlans() {
        Integer res = 1208;
        return new MResponse<Integer>().successMResponse().data(res);
    }

    @GetMapping("/getSystem")
    public MResponse<String> getSystem() {
        return new MResponse<String>().successMResponse().data(System.getProperty("os.name"));
    }
}
