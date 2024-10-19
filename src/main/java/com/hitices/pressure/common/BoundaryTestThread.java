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
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.IOException;

public class BoundaryTestThread implements Runnable {

    private final TestPlanVO testPlanVO;

    private String system;

    private String jmxPath;

    public BoundaryTestThread(TestPlanVO testPlanVO, String jmxPath) {
        this.testPlanVO = testPlanVO;
        this.system = System.getProperty("os.name");
        this.jmxPath = jmxPath;
    }

    @Override
    public void run() {
        try {
            StandardJMeterEngine standardJMeterEngine;
            if(this.system.equals("Windows 11")) {
                standardJMeterEngine = JMeterUtil.init(JMeterUtil.WINDOWS_HOME, JMeterUtil.WINDOWS_FILE_PATH);
            } else {
                standardJMeterEngine = JMeterUtil.init(JMeterUtil.LINUX_HOME, JMeterUtil.LINUX_FILE_PATH);
            }

            File jmxFile = new File(this.jmxPath);
            //记载jmx文件，创建测试计划
            HashTree testPlanTree = SaveService.loadTree(jmxFile);

            standardJMeterEngine.configure(testPlanTree);
            standardJMeterEngine.run();
            testPlanVO.setStatus("Completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
