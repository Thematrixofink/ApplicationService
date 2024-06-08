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

    public BoundaryTestThread(TestPlanVO testPlanVO) {
        this.testPlanVO = testPlanVO;
        this.system = System.getProperty("os.name");
    }

    @Override
    public void run() {
        try {
            File jmxFile = new File("C:\\Users\\91512\\Desktop\\jp@gc - Stepping Thread Group.jmx");
            HashTree testPlanTree = SaveService.loadTree(jmxFile);

            StandardJMeterEngine standardJMeterEngine;
            if(this.system.equals("Windows 11")) {
                standardJMeterEngine = JMeterUtil.init(JMeterUtil.WINDOWS_HOME, JMeterUtil.WINDOWS_FILE_PATH);
            } else {
                standardJMeterEngine = JMeterUtil.init(JMeterUtil.LINUX_HOME, JMeterUtil.LINUX_FILE_PATH);
            }

            standardJMeterEngine.configure(testPlanTree);
            standardJMeterEngine.run();
            testPlanVO.setStatus("Completed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
