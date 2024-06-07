package com.hitices.pressure.common;
import com.hitices.pressure.entity.AggregateReportVO;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;


public class AggregateReport extends ResultCollector {

    private transient PercentageCalculator calculator = new PercentageCalculator();
    private PressureMeasurementService pressureMeasurementService;
    private int planId;

    public AggregateReport() {
        super();
    }

    public AggregateReport(Summariser summariser, int planId){
        super(summariser);
        this.planId = planId;
    }

    public void setPressureMeasurementService(PressureMeasurementService pressureMeasurementService) {
        this.pressureMeasurementService = pressureMeasurementService;
    }

    @Override
    public void sampleOccurred(SampleEvent e) {
        super.sampleOccurred(e);
        SampleResult result = e.getResult();
        calculator.addSample(result);
    }

    @Override
    public void testEnded() {
        double[] percentileList = this.calculator.getPercentile(new double[]{0.5, 0.95, 0.99});
        this.pressureMeasurementService.addAggregateReport(
                new AggregateReportVO(
                        0,
                        this.planId,
                        this.getSampleCount(),
                        this.getMeanResponseTime(),
                        percentileList[0],
                        this.getMinResponseTime(),
                        this.getMaxResponseTime(),
                        percentileList[0],
                        percentileList[1],
                        percentileList[2],
                        this.getThroughput(),
                        this.getErrorRate()
                )
        );
        super.testEnded();
    }


    @Override
    public void clearData() {
        super.clearData();
        calculator.clear();
    }

    public long getSampleCount() {
        return calculator.getCount();
    }

    public double getMeanResponseTime() {
        return calculator.getMean();
    }

    public long getMaxResponseTime() {
        return calculator.getMax();
    }

    public long getMinResponseTime() {
        return calculator.getMin();
    }


    public double getErrorRate() {
        return calculator.getErrorPercentage();
    }

    public double getThroughput() {
        return calculator.getRate();
    }

    public double getReceivedBytesPerSecond() {
        return calculator.getBytesPerSecond();
    }

    public double getSentBytesPerSecond() {
        return calculator.getSentBytesPerSecond();
    }
}
