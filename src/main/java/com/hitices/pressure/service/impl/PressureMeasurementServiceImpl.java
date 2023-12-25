package com.hitices.pressure.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hitices.pressure.common.MeasureThread;
import com.hitices.pressure.entity.*;
import com.hitices.pressure.repository.PressureMeasurementMapper;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.jmeter.samplers.SampleResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class PressureMeasurementServiceImpl implements PressureMeasurementService {

    private final List<SampleResult> results = new ArrayList<>();

    @Autowired
    private PressureMeasurementMapper pressureMeasurementMapper;

    @Override
    public void measure(TestPlanVO testPlanVO) {
        MeasureThread measureThread = new MeasureThread(testPlanVO, this);
        Thread thread = new Thread(measureThread);
        thread.start();
        testPlanVO.setStatus("Running");
        pressureMeasurementMapper.updateTestPlan(testPlanVO);
    }

    @Override
    public void measure(int testPlanId) throws JsonProcessingException {
        TestPlanVO testPlanVO = getTestPlanById(testPlanId);
        measure(testPlanVO);
    }

    @Override
    public void addResults(SampleResult result) {
        //需要将result和http sampler proxy对应起来，暂时查找的方法就是result中的label是对应的http sampler proxy名字，
        //可进一步寻找更可行的方法，将二者id对应关系创建出来
        //此外，这里只是将结果存起来了，还需要保存进数据库，还未完成
        results.add(result);
    }

    @Override
    public List<SampleResult> getResults() {
        //浅拷贝，后续为保证变量不能随意篡改应改为深拷贝（但内部人员用谁会改呢），但是性能会下降
        return results;
    }

    @Override
    public List<TestPlanVO> getAllTestPlans() throws JsonProcessingException {
        List<TestPlanVO> testPlanVOList = pressureMeasurementMapper.getAllTestPlans();

        for (TestPlanVO testPlanVO : testPlanVOList) {
            refactorTestPlanVO(testPlanVO);
        }
        return testPlanVOList;
    }

    @Override
    public TestPlanVO getTestPlanById(int testPlanId) throws JsonProcessingException {
        TestPlanVO testPlanVO = pressureMeasurementMapper.getTestPlanById(testPlanId);
        if (testPlanVO != null) {
            refactorTestPlanVO(testPlanVO);
        }
        return testPlanVO;
    }

    @Override
    public AggregateReportVO getAggregateReportByPlanId(int planId) {
        AggregateReportVO aggregateReportVO = pressureMeasurementMapper.getAggregateReport(planId);
        if (aggregateReportVO != null) {
            return aggregateReportVO;
        }
        return null;
    }

    private static double calculateMedian(List<TestResultVO> sortedData) {
        int middle = sortedData.size() / 2;
        if (sortedData.size() % 2 == 0) {
            return (sortedData.get(middle - 1).getLatency() + sortedData.get(middle).getLatency()) / 2.0;
        } else {
            return sortedData.get(middle).getLatency();
        }
    }

    // 计算百分位数的方法
    private static double calculatePercentile(List<TestResultVO> sortedData, int percentile) {
        if (percentile < 0 || percentile > 100) {
            throw new IllegalArgumentException("百分位数必须在0到100之间");
        }
        double index = percentile / 100.0 * (sortedData.size() - 1);
        if (index == Math.floor(index)) {
            return sortedData.get((int) index).getLatency();
        } else {
            int lowerIndex = (int) Math.floor(index);
            int upperIndex = (int) Math.ceil(index);
            return (sortedData.get(lowerIndex).getLatency() + sortedData.get(upperIndex).getLatency()) / 2.0;
        }
    }

    public AggregateReportVO calculateReport(int planId) {
        class LatencyComparator implements Comparator<TestResultVO> {
            @Override
            public int compare(TestResultVO r1, TestResultVO r2) {
                return (int) (r1.getLatency() - r2.getLatency());
            }
        }
        class StartTimeComparator implements Comparator<TestResultVO> {
            @Override
            public int compare(TestResultVO r1, TestResultVO r2) {
                return r2.getTimestamp().compareTo(r1.getTimestamp());
            }
        }
        AggregateReportVO aggregateReportVO = new AggregateReportVO();
        List<TestResultVO> resultList = pressureMeasurementMapper.getTestResultsByPlanId(planId);
        int samplesNum = resultList.size();
        if(samplesNum > 0) {
            Collections.sort(resultList, new LatencyComparator());
            double min = resultList.get(0).getLatency();
            double max = resultList.get(samplesNum - 1).getLatency();
            double sum = resultList.stream().reduce(0f, (result, item) -> result + item.getLatency(), (i1, i2) -> i1 + i2);
            int errorNum = resultList.stream().reduce(0, (result, item) -> result + (item.isSuccess() ? 0: 1), (i1, i2) -> i1 + i2);
            double median = calculateMedian(resultList);
            double p50 = calculatePercentile(resultList, 50);
            double p95 = calculatePercentile(resultList, 95);
            double p99 = calculatePercentile(resultList, 99);
            aggregateReportVO.setSamplesNum(samplesNum);
            aggregateReportVO.setAverage(sum / samplesNum);
            aggregateReportVO.setErrorRate(errorNum / samplesNum);
            aggregateReportVO.setMax(max);
            aggregateReportVO.setMin(min);
            aggregateReportVO.setMedian(median);
            aggregateReportVO.setP50(p50);
            aggregateReportVO.setP95(p95);
            aggregateReportVO.setP99(p99);
            aggregateReportVO.setPlanId(planId);
            Collections.sort(resultList, new StartTimeComparator());
            double sec = (resultList.get(0).getTimestamp().getTime() - resultList.get(samplesNum - 1).getTimestamp().getTime()) / 1000;
            if (Double.compare(sec, 1.0) < 0) {
                sec = 1;
            }
            aggregateReportVO.setTps(samplesNum / sec);
        }
        return aggregateReportVO;
    }

    @Override
    public boolean addAggregateReport(int planId) {
        AggregateReportVO aggregateReportVO = calculateReport(planId);
        int res = pressureMeasurementMapper.insertAggregateReport(aggregateReportVO);
        if(res <= 0) {
            return false;
        }
        return true;
    }

    @Override
    public int updateAggregateReport(int planId) {
        AggregateReportVO aggregateReportVO = calculateReport(planId);
        return pressureMeasurementMapper.updateAggregateReport(aggregateReportVO);
    }

    public List<TimerVO> getTimersByThreadGroupId(int threadGroupId) {

        List<TimerVO> timers = pressureMeasurementMapper.getTimersByThreadGroupId(threadGroupId);
        if (timers == null) return null;

        List<TimerVO> res = new ArrayList<>();
        for (TimerVO timer : timers) {

            if (timer.getTimerType() == TimerType.CONSTANT_TIMER) {
                ConstantTimerVO constantTimer = pressureMeasurementMapper.getConstantTimerById(timer.getId());
                fillTimer(constantTimer, timer);
                res.add(constantTimer);
            } else if (timer.getTimerType() == TimerType.UNIFORM_RANDOM_TIMER) {
                UniformRandomTimerVO uniformRandomTimer = pressureMeasurementMapper.getUniformRandomTimerById(timer.getId());
                fillTimer(uniformRandomTimer, timer);
                res.add(uniformRandomTimer);
            } else if (timer.getTimerType() == TimerType.GAUSSIAN_RANDOM_TIMER) {
                GaussianRandomTimerVO gaussianRandomTimer = pressureMeasurementMapper.getGaussianRandomTimerById(timer.getId());
                fillTimer(gaussianRandomTimer, timer);
                res.add(gaussianRandomTimer);
            } else {
                PoissonRandomTimerVO poissonRandomTimer = pressureMeasurementMapper.getPoissonRandomTimerById(timer.getId());
                fillTimer(poissonRandomTimer, timer);
                res.add(poissonRandomTimer);
            }
        }
        return res;
    }

    @Override
    public int deleteTestPlan(int testPlanId) {
        return pressureMeasurementMapper.deleteTestPlan(testPlanId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int addTestPlan(TestPlanVO testPlan) {

        testPlan.setStatus("Created");
        pressureMeasurementMapper.insertTestPlan(testPlan);
        testPlan.getThreadGroupList().forEach(threadGroupVO -> threadGroupVO.setTestPlanId(testPlan.getId()));
        saveThreadGroups(testPlan.getThreadGroupList());
        return 1;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateTestPlan(TestPlanVO testPlanVO) {

        pressureMeasurementMapper.updateTestPlan(testPlanVO);

        pressureMeasurementMapper.deleteThreadGroupByTestPlanId(testPlanVO.getId());
        testPlanVO.getThreadGroupList().forEach(threadGroupVO -> threadGroupVO.setTestPlanId(testPlanVO.getId()));
        saveThreadGroups(testPlanVO.getThreadGroupList());
    }

    @Override
    public void addTimers(List<TimerVO> timers, int threadGroupId) {
        if (threadGroupId > 0) {
            timers.forEach(timer -> timer.setThreadGroupId(threadGroupId));
        }

        List<ConstantTimerVO> constantTimerVOList = new ArrayList<>();
        List<UniformRandomTimerVO> uniformRandomTimerVOList = new ArrayList<>();
        List<GaussianRandomTimerVO> gaussianRandomTimerVOList = new ArrayList<>();
        List<PoissonRandomTimerVO> poissonRandomTimerVOList = new ArrayList<>();

        for (TimerVO timer : timers) {
            if (timer instanceof ConstantTimerVO) {
                timer.setTimerType(TimerType.CONSTANT_TIMER);
                constantTimerVOList.add((ConstantTimerVO) timer);
            } else if (timer instanceof UniformRandomTimerVO) {
                timer.setTimerType(TimerType.UNIFORM_RANDOM_TIMER);
                uniformRandomTimerVOList.add((UniformRandomTimerVO) timer);
            } else if (timer instanceof GaussianRandomTimerVO) {
                timer.setTimerType(TimerType.GAUSSIAN_RANDOM_TIMER);
                gaussianRandomTimerVOList.add((GaussianRandomTimerVO) timer);
            } else {
                timer.setTimerType(TimerType.POISSON_RANDOM_TIMER);
                poissonRandomTimerVOList.add((PoissonRandomTimerVO) timer);
            }
        }

        pressureMeasurementMapper.insertTimers(timers);

        if (constantTimerVOList.size() > 0) {
            pressureMeasurementMapper.insertConstantTimerList(constantTimerVOList);
        }
        if (uniformRandomTimerVOList.size() > 0) {
            pressureMeasurementMapper.insertUniformRandomTimerList(uniformRandomTimerVOList);
        }
        if (gaussianRandomTimerVOList.size() > 0) {
            pressureMeasurementMapper.insertGaussianRandomTimerList(gaussianRandomTimerVOList);
        }
        if (poissonRandomTimerVOList.size() > 0) {
            pressureMeasurementMapper.insertPoissonRandomTimerList(poissonRandomTimerVOList);
        }

    }

    public int addTestResult(TestResultVO testResultVO) {
        return pressureMeasurementMapper.insertTestResult(testResultVO);
    }

    public List<TestResultVO> getTestResultsByPlanId(int planId) {
        return pressureMeasurementMapper.getTestResultsByPlanId(planId);
    }

    public TestResultVO getTestResultByResultId(int testResultId) {
        return pressureMeasurementMapper.getTestResultByResultId(testResultId);
    }


    private void saveThreadGroups(List<ThreadGroupVO> threadGroupVOList) {

        pressureMeasurementMapper.insertThreadGroups(threadGroupVOList);

        for (ThreadGroupVO threadGroup : threadGroupVOList) {
            List<HTTPSamplerProxyVO> httpSamplerProxyVOList = new ArrayList<>();
            List<HeaderManagerVO> headerManagerVOList = new ArrayList<>();
            List<CookieManagerVO> cookieManagerVOList = new ArrayList<>();

            if (threadGroup.getHttpSamplerProxyVO() != null) {
                HTTPSamplerProxyVO httpSamplerProxyVO = threadGroup.getHttpSamplerProxyVO();
                httpSamplerProxyVO.setArgumentsString(JSON.toJSONString(httpSamplerProxyVO.getArguments()));
                httpSamplerProxyVO.setThreadGroupId(threadGroup.getId());
                httpSamplerProxyVOList.add(httpSamplerProxyVO);
            }
            if (threadGroup.getHeaderManagerVO() != null) {
                HeaderManagerVO headerManagerVO = threadGroup.getHeaderManagerVO();
                headerManagerVO.setThreadGroupId(threadGroup.getId());
                headerManagerVO.setHeaderListString(JSON.toJSONString(headerManagerVO.getHeaderList()));
                headerManagerVOList.add(headerManagerVO);
            }
            if (threadGroup.getCookieManagerVO() != null) {
                CookieManagerVO cookieManagerVO = threadGroup.getCookieManagerVO();
                cookieManagerVO.setThreadGroupId(threadGroup.getId());
                cookieManagerVO.setCookiesString(JSON.toJSONString(cookieManagerVO.getCookies()));
                cookieManagerVOList.add(cookieManagerVO);
            }
            if (threadGroup.getLoopControllerVO() != null) {
                LoopControllerVO loopControllerVO = threadGroup.getLoopControllerVO();
                loopControllerVO.setThreadGroupId(threadGroup.getId());
                pressureMeasurementMapper.insertLoopController(loopControllerVO);
            }

            if (httpSamplerProxyVOList.size() > 0)
                pressureMeasurementMapper.insertHTTPSamplerProxyList(httpSamplerProxyVOList);
            if (headerManagerVOList.size() > 0) pressureMeasurementMapper.insertHeaderManagerList(headerManagerVOList);
            if (cookieManagerVOList.size() > 0) pressureMeasurementMapper.insertCookieManagerList(cookieManagerVOList);

            addTimers(threadGroup.getTimers(), threadGroup.getId());
        }
    }

    private void refactorTestPlanVO(TestPlanVO testPlanVO) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();

        for (ThreadGroupVO threadGroupVO : testPlanVO.getThreadGroupList()) {

            threadGroupVO.setTimers(getTimersByThreadGroupId(threadGroupVO.getId()));

            HTTPSamplerProxyVO httpSamplerProxyVO = threadGroupVO.getHttpSamplerProxyVO();
            if (httpSamplerProxyVO.getArgumentsString() != null) {
                httpSamplerProxyVO.setArguments(mapper.readValue(httpSamplerProxyVO.getArgumentsString(),
                        new TypeReference<Map<String, String>>() {
                        }));
                httpSamplerProxyVO.setArgumentsString(null);
            }
            HeaderManagerVO headerManagerVO = threadGroupVO.getHeaderManagerVO();
            if (headerManagerVO != null) {
                headerManagerVO.setHeaderList(mapper.readValue(headerManagerVO.getHeaderListString(),
                        new TypeReference<Map<String, String>>() {
                        }));
                headerManagerVO.setHeaderListString(null);
            }
            CookieManagerVO cookieManagerVO = threadGroupVO.getCookieManagerVO();
            if (cookieManagerVO != null) {
                cookieManagerVO.setCookies(mapper.readValue(cookieManagerVO.getCookiesString(),
                        new TypeReference<List<String>>() {
                        }));
                cookieManagerVO.setCookiesString(null);
            }
        }
    }

    private <concreteTimer extends TimerVO> void fillTimer(concreteTimer concreteTimer, TimerVO timer) {
        concreteTimer.setThreadGroupId(timer.getThreadGroupId());
        concreteTimer.setTimerType(timer.getTimerType());
    }


}