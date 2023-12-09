package com.hitices.pressure.utils;

import com.hitices.pressure.entity.*;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.gui.LoopControlPanel;
import org.apache.jmeter.control.gui.TestPlanGui;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.Header;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.control.gui.HttpTestSampleGui;
import org.apache.jmeter.protocol.http.gui.HeaderPanel;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.timers.*;
import org.apache.jmeter.timers.gui.ConstantTimerGui;
import org.apache.jmeter.timers.gui.GaussianRandomTimerGui;
import org.apache.jmeter.timers.gui.PoissonRandomTimerGui;
import org.apache.jmeter.timers.gui.UniformRandomTimerGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.util.List;
import java.util.Map;

public class JMeterUtil {

    public static StandardJMeterEngine init(String JMeterHome) {

        File jmeterPropertiesFile = new File(JMeterHome + "bin\\jmeter.properties");
        if (!jmeterPropertiesFile.exists()) return null;

        JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
        JMeterUtils.setJMeterHome(JMeterHome);
        JMeterUtils.initLocale();

        return new StandardJMeterEngine();
    }

    public static TestPlan createTestPlan(TestPlanVO testPlanVO) {
        TestPlan testPlan = new TestPlan();
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
        testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
        testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

        if (!StringUtils.isBlank(testPlanVO.getTestPlanName()))
            testPlan.setName(testPlanVO.getTestPlanName());
        if (!StringUtils.isBlank(testPlanVO.getComment()))
            testPlan.setName(testPlanVO.getComment());
        testPlan.setSerialized(testPlanVO.isSerialized());
        testPlan.setTearDownOnShutdown(testPlanVO.isTearDown());
        testPlan.setFunctionalMode(testPlanVO.isFunctionalMode());
        return testPlan;
    }

    public static ThreadGroup createThreadGroup(ThreadGroupVO threadGroupVO) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());

        if (!StringUtils.isBlank(threadGroupVO.getThreadGroupName()))
            threadGroup.setName(threadGroupVO.getThreadGroupName());
        if (!StringUtils.isBlank(threadGroupVO.getComment()))
            threadGroup.setComment(threadGroupVO.getComment());

        threadGroup.setNumThreads(threadGroupVO.getThreadNum());
        threadGroup.setRampUp(threadGroupVO.getRampUp());
        threadGroup.setScheduler(threadGroupVO.isScheduler());
        threadGroup.setDuration(threadGroupVO.getDuration());
        threadGroup.setDelay(threadGroupVO.getDelay());


        return threadGroup;
    }

    public static LoopController createLoopController(LoopControllerVO loopControllerVO) {

        LoopController loopController = new LoopController();
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());

        if (!StringUtils.isBlank(loopControllerVO.getLoopControllerName()))
            loopController.setName(loopControllerVO.getLoopControllerName());
        if (!StringUtils.isBlank(loopControllerVO.getComment()))
            loopController.setComment(loopControllerVO.getComment());

        loopController.setLoops(loopControllerVO.getLoopNum());
        loopController.setContinueForever(loopControllerVO.isContinueForever());

        loopController.initialize();
        return loopController;
    }

    public static HTTPSamplerProxy createHTTPSamplerProxy(HTTPSamplerProxyVO request) {

        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());

        if(!StringUtils.isBlank(request.getName()))
            httpSamplerProxy.setName(request.getName());
        if (!StringUtils.isBlank(request.getComment()))
            httpSamplerProxy.setComment(request.getComment());
        if(!StringUtils.isBlank(request.getContentEncoding())){
            httpSamplerProxy.setContentEncoding(request.getContentEncoding());
        }

        httpSamplerProxy.setProtocol(request.getProtocol());
        httpSamplerProxy.setDomain(request.getServer());

        if (request.getPort() == -1) {
            if (StringUtils.equalsIgnoreCase(request.getProtocol(), "http")) {
                httpSamplerProxy.setPort(80);
            } else if (StringUtils.equalsIgnoreCase(request.getProtocol(), "https")) {
                httpSamplerProxy.setPort(443);
            }
        } else {
            httpSamplerProxy.setPort(request.getPort());
        }

        httpSamplerProxy.setPath(request.getPath());
        httpSamplerProxy.setMethod(request.getMethod());
        httpSamplerProxy.setConnectTimeout("5000");
        httpSamplerProxy.setUseKeepAlive(request.isUseKeepAlive());
        httpSamplerProxy.setFollowRedirects(request.isFollowRedirects());
        httpSamplerProxy.setAutoRedirects(request.isAutoRedirects());

        if (request.getArguments() != null && !request.getArguments().isEmpty()) {
            Arguments arguments = new Arguments();
            arguments.setProperty(TestElement.GUI_CLASS, ConfigTestElement.class.getName());
            arguments.setProperty(TestElement.TEST_CLASS, Arguments.class.getName());
            Map<String, String> requestArguments = request.getArguments();
            for (String key : requestArguments.keySet()) {
                Argument argument = new Argument(key, requestArguments.get(key));

                arguments.addArgument(argument);
            }
            httpSamplerProxy.setArguments(arguments);

        }

        if (StringUtils.equalsIgnoreCase(request.getMethod(), "post")) {
            httpSamplerProxy.addNonEncodedArgument("", request.getBody(), "");
        }

        return httpSamplerProxy;
    }

    public static HeaderManager createHeaderManager(HeaderManagerVO headerManagerVO) {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
        headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());

        if (!StringUtils.isBlank(headerManagerVO.getHeaderManagerName()))
            headerManager.setName(headerManagerVO.getHeaderManagerName());
        if (!StringUtils.isBlank(headerManagerVO.getComment()))
            headerManager.setComment(headerManagerVO.getComment());

        Map<String, String> headers = headerManagerVO.getHeaderList();
        for (String key : headers.keySet()) {
            headerManager.add(new Header(key, headers.get(key)));
        }

        return headerManager;
    }

    public static ResultCollector createResultCollector(PressureMeasurementService service) {
        Summariser summer = new Summariser();
        MyResultCollector resultCollector = new MyResultCollector(summer);
        resultCollector.setName("collector");
        resultCollector.setPressureMeasurementService(service);
        resultCollector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        resultCollector.setProperty(TestElement.GUI_CLASS, ViewResultsFullVisualizer.class.getName());
        resultCollector.setEnabled(true);
//        resultCollector.setFilename("result\\result.jtl");

        return resultCollector;
    }

    public static void addTimers(HashTree threadGroupTree, List<TimerVO> timers) {
        for (TimerVO timerVO : timers) {

            if (timerVO instanceof ConstantTimerVO) {
                ConstantTimer timer = new ConstantTimer();
                timer.setProperty(TestElement.TEST_CLASS, ConstantTimer.class.getName());
                timer.setProperty(TestElement.GUI_CLASS, ConstantTimerGui.class.getName());
                timer.setDelay(((ConstantTimerVO) timerVO).getThreadDelay());
                if(!StringUtils.isBlank(timerVO.getTimerName())) timer.setName(timerVO.getTimerName());
                if(!StringUtils.isBlank(timerVO.getComment())) timer.setComment(timerVO.getComment());
                threadGroupTree.add(timer);
            } else if (timerVO instanceof UniformRandomTimerVO) {
                UniformRandomTimer timer = new UniformRandomTimer();
                timer.setProperty(TestElement.TEST_CLASS, UniformRandomTimer.class.getName());
                timer.setProperty(TestElement.GUI_CLASS, UniformRandomTimerGui.class.getName());
                timer.setDelay(((UniformRandomTimerVO) timerVO).getConstantDelayOffset());
                timer.setRange(((UniformRandomTimerVO) timerVO).getRandomDelayMaximum());
                if(!StringUtils.isBlank(timerVO.getTimerName())) timer.setName(timerVO.getTimerName());
                if(!StringUtils.isBlank(timerVO.getComment())) timer.setComment(timerVO.getComment());
                threadGroupTree.add(timer);
            } else if (timerVO instanceof GaussianRandomTimerVO) {
                GaussianRandomTimer timer = new GaussianRandomTimer();
                timer.setProperty(TestElement.TEST_CLASS, GaussianRandomTimer.class.getName());
                timer.setProperty(TestElement.GUI_CLASS, GaussianRandomTimerGui.class.getName());
                timer.setDelay(((GaussianRandomTimerVO) timerVO).getConstantDelayOffset());
                timer.setRange(((GaussianRandomTimerVO) timerVO).getDeviation());
                if(!StringUtils.isBlank(timerVO.getTimerName())) timer.setName(timerVO.getTimerName());
                if(!StringUtils.isBlank(timerVO.getComment())) timer.setComment(timerVO.getComment());
                threadGroupTree.add(timer);
            } else if (timerVO instanceof PoissonRandomTimerVO) {
                PoissonRandomTimer timer = new PoissonRandomTimer();
                timer.setProperty(TestElement.TEST_CLASS, PoissonRandomTimer.class.getName());
                timer.setProperty(TestElement.GUI_CLASS, PoissonRandomTimerGui.class.getName());
                timer.setDelay(((PoissonRandomTimerVO) timerVO).getConstantDelayOffset());
                timer.setRange(((PoissonRandomTimerVO) timerVO).getLambda());
                if(!StringUtils.isBlank(timerVO.getTimerName())) timer.setName(timerVO.getTimerName());
                if(!StringUtils.isBlank(timerVO.getComment())) timer.setComment(timerVO.getComment());
                threadGroupTree.add(timer);
            }
        }
    }

}
