package com.hitices.pressure;

import com.hitices.pressure.common.MyResultCollector;
import org.apache.jmeter.config.Arguments;
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
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.threads.gui.ThreadGroupGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.ViewResultsFullVisualizer;
import org.apache.jorphan.collections.HashTree;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@SpringBootApplication
@ComponentScan("com.hitices")
@MapperScan("com.hitices.pressure.repository")
public class ApplicationMain {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationMain.class, args);
    }

    public void test() {
        StandardJMeterEngine standardJMeterEngine = new StandardJMeterEngine();
        File jmeterPropertiesFile = new File("D:\\ProgramData\\apache-jmeter-5.6.2\\bin\\jmeter.properties");
        if (jmeterPropertiesFile.exists()) {
            JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
            JMeterUtils.setJMeterHome("D:\\ProgramData\\apache-jmeter-5.6.2");
            JMeterUtils.initLocale();
            // 创建测试计划
            TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
            testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());
            testPlan.setProperty(TestElement.GUI_CLASS, TestPlanGui.class.getName());
            testPlan.setUserDefinedVariables((Arguments) new ArgumentsPanel().createTestElement());

            HTTPSamplerProxy exampleSampler = createHTTPSamplerProxy(
                    "api.tttt.one", 443, "/rest-v2/login/sign_up", "POST");
            LoopController loopController = createLoopController(1, false);
            ThreadGroup threadGroup = createThreadGroup("test Thread Group", 1,
                    2, false, 0, 0);
            threadGroup.setSamplerController(loopController);
            HashTree subtree = new HashTree();
            Summariser summer = null;
            String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
            if (summariserName.length() > 0) {
                summer = new Summariser(summariserName);
            }
//            ResultCollector resultCollector = new ResultCollector(summer);
//            resultCollector.setName("收集结果");
//            resultCollector.setProperty(TestElement.TEST_CLASS,ResultCollector.class.getName());
//            resultCollector.setProperty(TestElement.GUI_CLASS, ViewResultsFullVisualizer.class.getName());
//            resultCollector.setFilename("D:\\result\\result.jtl");
            MyResultCollector resultCollector = new MyResultCollector(summer, 0);
            resultCollector.setName("收集结果");
            resultCollector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
            resultCollector.setProperty(TestElement.GUI_CLASS, ViewResultsFullVisualizer.class.getName());
            resultCollector.setFilename("D:\\result\\result.jtl");
            HeaderManager headerManager = new HeaderManager();
            headerManager.setName("信息头管理");
            headerManager.setProperty(TestElement.TEST_CLASS, HeaderManager.class.getName());
            headerManager.setProperty(TestElement.GUI_CLASS, HeaderPanel.class.getName());
            headerManager.add(new Header("Content-Type", "application/json"));
            HashTree tt = new HashTree();
            subtree.add(exampleSampler);
            //subtree.add(loopController);
            subtree.add(headerManager);
            subtree.add(resultCollector);
            tt.add(threadGroup, subtree);
            HashTree tree = new HashTree();
//            tree.add(testPlan,subtree);
            tree.add(testPlan, tt);
            try {
                SaveService.saveTree(tree, new FileOutputStream("D:\\result\\test.jmx"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 配置jmeter
            standardJMeterEngine.configure(tree);
//            SampleResult sample = exampleSampler.sample();
//            System.out.println("message:"+sample.getResponseMessage());
//            System.out.println("data:"+sample.getSamplerData());
//            System.out.println("code:"+sample.getResponseCode());
//            System.out.println("response data:"+sample.getResponseDataAsString());
            // 运行
            standardJMeterEngine.run();
        }
    }

    /**
     * 创建线程组
     */
    public static ThreadGroup createThreadGroup(String threadGroupName, Integer threadNum, Integer rampUp,
                                                Boolean scheduler, Integer duration, Integer delay) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setEnabled(true);
        threadGroup.setName(threadGroupName);
        threadGroup.setNumThreads(threadNum);
        threadGroup.setRampUp(rampUp);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setProperty(TestElement.GUI_CLASS, ThreadGroupGui.class.getName());
        threadGroup.setScheduler(scheduler);
        threadGroup.setDuration(duration);
        threadGroup.setDelay(delay);

        return threadGroup;
    }

    /**
     * 创建循环控制器
     */
    public static LoopController createLoopController(Integer loopNum, Boolean continueForever) {
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setEnabled(true);
        loopController.setLoops(loopNum);
        loopController.setContinueForever(continueForever);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.setProperty(TestElement.GUI_CLASS, LoopControlPanel.class.getName());
        loopController.initialize();

        return loopController;
    }

    /**
     * 创建http采样器
     */
    public static HTTPSamplerProxy createHTTPSamplerProxy(String domain, Integer port, String path, String method) {
        HeaderManager headerManager = new HeaderManager();
        headerManager.add(new Header("Content-Type", "application/json"));
        //headerManager.setProperty("Content-Type", "application/json");

        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setName("http请求");
        httpSamplerProxy.setEnabled(true);
        httpSamplerProxy.setProtocol("https");
        httpSamplerProxy.setDomain(domain);
        httpSamplerProxy.setPort(port);
        httpSamplerProxy.setPath(path);
        httpSamplerProxy.setMethod(method);
        httpSamplerProxy.setConnectTimeout("5000");
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setFollowRedirects(true);
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setProperty(TestElement.GUI_CLASS, HttpTestSampleGui.class.getName());
        httpSamplerProxy.setPostBodyRaw(true);
        httpSamplerProxy.setHeaderManager(headerManager);

        httpSamplerProxy.addNonEncodedArgument(
                "", "{\"password\":\"123456789\",\"email\":\"iamyunchang1163com\"}", "");
//        Arguments arguments = new Arguments();
//        arguments.setProperty(TestElement.GUI_CLASS, ConfigTestElement.t'tlass.getName());
//        arguments.setProperty(TestElement.TEST_CLASS, Arguments.class.getName());
//        httpSamplerProxy.setArguments(arguments);
        return httpSamplerProxy;
    }
}
