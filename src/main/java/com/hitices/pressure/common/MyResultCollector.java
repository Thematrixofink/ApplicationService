package com.hitices.pressure.common;

import com.hitices.pressure.domain.vo.TestResultVO;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

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
        TestResultVO testResultVO = new TestResultVO(0,
                planId,
                new Date(result.getTimeStamp()),
                new Date(result.getStartTime()),
                new Date(result.getEndTime()),
                result.getIdleTime(),
                result.getIdleTime(),
                result.getConnectTime(),
                result.getLatency(),
                result.isSuccessful(),
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

    @Override
    public void sampleStopped(SampleEvent e) {

    }
}
