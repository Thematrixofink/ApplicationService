package com.hitices.pressure.common;

import com.hitices.pressure.domain.vo.TestPlanVO;
import com.hitices.pressure.repository.PressureMeasurementMapper;
import com.hitices.pressure.utils.JMeterUtil;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.save.SaveService;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.IOException;

public class BoundaryTestThread implements Runnable {

    private final TestPlanVO testPlanVO;

    private String system;

    private String jmxPath;

    private final PressureMeasurementMapper pressureMeasurementMapper;

    public BoundaryTestThread(TestPlanVO testPlanVO, String jmxPath,PressureMeasurementMapper pressureMeasurementMapper) {
        this.testPlanVO = testPlanVO;
        this.system = System.getProperty("os.name");
        this.jmxPath = jmxPath;
        this.pressureMeasurementMapper = pressureMeasurementMapper;
    }

    @Override
    public void run() {
        try {
            StandardJMeterEngine standardJMeterEngine;
            if(this.system.equals("Windows 11")  || this.system.equals("Windows 10") ) {
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
            pressureMeasurementMapper.updateTestPlan(testPlanVO);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
