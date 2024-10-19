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
//        System.out.println("Received");
        //sleep for 200ms
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new MResponse<String>().successMResponse().data(System.getProperty("os.name"));
    }

    @GetMapping("/count")
    public MResponse<String> count() {
        for(int i = 0 ; i < 10000 ; i ++){
            i++;
        }
        return new MResponse<String>().successMResponse().data(System.getProperty("os.name"));
    }
}
