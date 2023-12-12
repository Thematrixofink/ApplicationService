package com.hitices.pressure.entity;

import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

public class MyResultCollector extends ResultCollector {

    private PressureMeasurementService pressureMeasurementService;
    private int planId;

    public MyResultCollector(){
        super();
    }

    public MyResultCollector(Summariser summariser, int planId){
        super(summariser);
        this.planId = planId;
    }

    public void setPressureMeasurementService(PressureMeasurementService pressureMeasurementService){
        this.pressureMeasurementService = pressureMeasurementService;
    }

    @Override
    public void sampleOccurred(SampleEvent event){
        super.sampleOccurred(event);
        SampleResult result = event.getResult();
        System.out.println("message:"+result.getResponseMessage());
        System.out.println("data:"+result.getSamplerData());
        System.out.println("code:"+result.getResponseCode());
        System.out.println("response data:"+result.getResponseDataAsString());
        System.out.println("request headers:"+result.getRequestHeaders());

        TestResultVO testResultVO = new TestResultVO(0,
                planId,
                new Date(result.getTimeStamp()),
                new Date(result.getStartTime()),
                new Date(result.getEndTime()),
                new Date(result.getIdleTime()),
                new Date(result.getIdleTime()),
                result.getConnectTime(),
                result.getLatency(),
                result.getResponseCode(),
                new String(result.getResponseData()),
                result.getResponseMessage(),
                result.getResponseHeaders(),
                result.getBytesAsLong(),
                result.getHeadersSize(),
                result.getBodySizeAsLong(),
                result.getSentBytes());

        pressureMeasurementService.addTestResult(testResultVO);
    }
}
