package com.hitices.pressure.common;

import com.hitices.pressure.entity.TestPlanVO;
import com.hitices.pressure.entity.ThreadGroupVO;
import com.hitices.pressure.service.PressureMeasurementService;
import com.hitices.pressure.utils.JMeterUtil;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

public class MeasureThread implements Runnable {

    private final TestPlanVO testPlanVO;

    private String system;

    private final PressureMeasurementService pressureMeasurementService;

    public MeasureThread(TestPlanVO testPlanVO, PressureMeasurementService pressureMeasurementService) {
        this.testPlanVO = testPlanVO;
        this.pressureMeasurementService = pressureMeasurementService;
        this.system = System.getProperty("os.name");
    }

    @Override
    public void run() {
        StandardJMeterEngine standardJMeterEngine;
        if(this.system.equals("Windows 11")) {
            standardJMeterEngine = JMeterUtil.init("C:\\Neil\\Software\\Apache\\apache-jmeter-4.0\\","C:\\Neil\\Software\\Apache\\apache-jmeter-4.0\\bin\\jmeter.properties");
        } else {
            standardJMeterEngine = JMeterUtil.init("/opt/jmeter/","/opt/jmeter/bin/jmeter.properties");
        }

        if (standardJMeterEngine != null) {

            //创建测试计划
            TestPlan testPlan = JMeterUtil.createTestPlan(testPlanVO);

            HashTree tree = new HashTree();
            HashTree testPlanTree = new HashTree();

            //创建线程组，并添加到测试计划中
            for (ThreadGroupVO threadGroupVO : testPlanVO.getThreadGroupList()) {
                HashTree threadGroupTree = new HashTree();
                ThreadGroup threadGroup = JMeterUtil.createThreadGroup(threadGroupVO);

                //创建循环控制器
                LoopController loopController = JMeterUtil.createLoopController(threadGroupVO.getLoopControllerVO());
                //为线程组添加循环控制器
                threadGroup.setSamplerController(loopController);
                //创建http请求取样器
                HTTPSamplerProxy httpSamplerProxy = JMeterUtil.createHTTPSamplerProxy(threadGroupVO.getHttpSamplerProxyVO());
                //创建http信息头管理器
                HeaderManager headerManager = JMeterUtil.createHeaderManager(threadGroupVO.getHeaderManagerVO());

                ResultCollector resultCollector = JMeterUtil.createResultCollector(pressureMeasurementService);
//                Summariser summer = new Summariser();
//                SummaryReport summaryReport = new SummaryReport();
//                ViewResultsFullVisualizer v = new ViewResultsFullVisualizer();
//                ResultCollector resultCollector = new ResultCollector(summer);
//                resultCollector.setName("collector");
//                resultCollector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
//                resultCollector.setProperty(TestElement.GUI_CLASS, ViewResultsFullVisualizer.class.getName());
//                resultCollector.setEnabled(true);
//                resultCollector.setFilename("result\\result.jtl");


                threadGroupTree.add(httpSamplerProxy);
                threadGroupTree.add(headerManager);
                JMeterUtil.addTimers(threadGroupTree, threadGroupVO.getTimers());
                threadGroupTree.add(resultCollector);

                testPlanTree.add(threadGroup, threadGroupTree);
            }

            tree.add(testPlan, testPlanTree);
//            try {
//                SaveService.saveTree(tree, new FileOutputStream("result\\test.jmx"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            standardJMeterEngine.configure(tree);
            standardJMeterEngine.run();

            // Todo 将pressureMeasureService中的List<SampleResult>各自存入数据库
            //更新test plan 状态
            //这里的testPlanVo可能存在多线程问题，还没仔细想过，应该没啥事吧
            testPlanVO.setStatus("Completed");
            pressureMeasurementService.updateTestPlan(testPlanVO);
        }
    }
}
