package com.hitices.pressure.entity;

import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class MyResultCollector extends ResultCollector {

    private PressureMeasurementService pressureMeasurementService;

    public MyResultCollector(){
        super();
    }

    public MyResultCollector(Summariser summariser){
        super(summariser);
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
        pressureMeasurementService.addResults(result);
    }
}
