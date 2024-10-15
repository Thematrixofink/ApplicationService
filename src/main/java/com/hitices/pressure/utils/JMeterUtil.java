package com.hitices.pressure.utils;

import com.hitices.pressure.common.AggregateReport;
import com.hitices.pressure.common.MyResultCollector;
import com.hitices.pressure.entity.*;
import com.hitices.pressure.service.PressureMeasurementService;
import org.apache.commons.lang.StringUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.ConfigTestElement;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.control.TransactionController;
import org.apache.jmeter.control.gui.LoopControlPanel;
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
import org.apache.jmeter.timers.*;
import org.apache.jmeter.timers.gui.ConstantTimerGui;
import org.apache.jmeter.timers.gui.GaussianRandomTimerGui;
import org.apache.jmeter.timers.gui.PoissonRandomTimerGui;
import org.apache.jmeter.timers.gui.UniformRandomTimerGui;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jmeter.visualizers.BeanShellListener;
import org.apache.jmeter.visualizers.StatVisualizer;
import org.apache.jorphan.collections.HashTree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class JMeterUtil {

    //todo 修改为了自己的路径
    public static String WINDOWS_HOME= "D:\\aSoftWares\\apache-tomcat-10.1.6-windows-x64\\apache-jmeter-5.6.2";
    public static String WINDOWS_FILE_PATH = "D:\\aSoftWares\\apache-tomcat-10.1.6-windows-x64\\apache-jmeter-5.6.2\\bin\\jmeter.properties";
    //public static String WINDOWS_HOME= "C:\\Neil\\Software\\Apache\\apache-jmeter-4.0\\";
    //public static String WINDOWS_FILE_PATH = "C:\\Neil\\Software\\Apache\\apache-jmeter-4.0\\bin\\jmeter.properties";
    public static String LINUX_HOME= "/opt/jmeter/";
    public static String LINUX_FILE_PATH = "/opt/jmeter/bin/jmeter.properties";

    public static String JMX_PATH = "./conf";
    public static String RES_PATH = "./result";

    public static StandardJMeterEngine init(String JMeterHome, String filePath) {

        File jmeterPropertiesFile = new File(filePath);
        if (!jmeterPropertiesFile.exists()) return null;

        JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
        JMeterUtils.setJMeterHome(JMeterHome);
        JMeterUtils.initLocale();

        return new StandardJMeterEngine();
    }

    public static String saveTestPlan(TestPlanVO testPlanVO, PressureMeasurementService pressureMeasurementService) throws IOException {

        int planId = pressureMeasurementService.addTestPlan(testPlanVO);

        String system = System.getProperty("os.name");
        if(system.equals("Windows 11")) {
            init(WINDOWS_HOME, WINDOWS_FILE_PATH);
        } else {
            init(LINUX_HOME, LINUX_FILE_PATH);
        }
        HashTree testPlanTree = new HashTree();
        TestPlan testPlan = new TestPlan("Example Test Plan");
        testPlan.setProperty(TestElement.GUI_CLASS, "TestPlanGui");
        testPlan.setProperty(TestElement.TEST_CLASS, "TestPlan");
        testPlan.setEnabled(true);

        //创建线程组，并添加到测试计划中
        for (ThreadGroupVO threadGroupVO : testPlanVO.getThreadGroupList()) {
            HashTree threadGroupTree = testPlanTree.add(testPlan);
            ThreadGroup threadGroup = JMeterUtil.createThreadGroup(threadGroupVO);

            //创建循环控制器
            LoopController loopController = JMeterUtil.createLoopController(threadGroupVO.getLoopControllerVO());
            //为线程组添加循环控制器
            threadGroup.setSamplerController(loopController);
            //创建http请求取样器
            HTTPSamplerProxy httpSamplerProxy = JMeterUtil.createHTTPSamplerProxy(threadGroupVO.getHttpSamplerProxyVO());
            //创建http信息头管理器
            HeaderManager headerManager = JMeterUtil.createHeaderManager(threadGroupVO.getHeaderManagerVO());

            AggregateReport aggregateReport = JMeterUtil.createAggregateReport(planId);

            BeanShellListener beanShellListener = new BeanShellListener();
            beanShellListener.setName("BeanShell Listener");
            beanShellListener.setProperty("resetInterpreter", false);
            beanShellListener.setProperty(TestElement.GUI_CLASS, "TestBeanGUI");
            beanShellListener.setProperty(TestElement.TEST_CLASS, "BeanShellListener");
            beanShellListener.setProperty("script", "log.info(\"This is a BeanShell Listener script execution.\");");

            TransactionController transactionController = new TransactionController();
            transactionController.setName(String.format("并发线程数-%d-${thread}", planId));
            transactionController.setEnabled(true);
            transactionController.setProperty("TransactionController.includeTimers", false);
            transactionController.setProperty("TransactionController.parent", false);
            transactionController.setProperty(TestElement.GUI_CLASS, "TransactionControllerGui");
            transactionController.setProperty(TestElement.TEST_CLASS, "TransactionController");

            HashTree siblingTree = new HashTree();
            siblingTree.add(beanShellListener);
            HashTree transactionControllerHashTree = siblingTree.add(transactionController);


            transactionControllerHashTree.add(httpSamplerProxy);
            transactionControllerHashTree.add(headerManager);
            JMeterUtil.addTimers(transactionControllerHashTree, threadGroupVO.getTimers());
            transactionControllerHashTree.add(aggregateReport);

            threadGroupTree.add(threadGroup, siblingTree);
        }

        Path path = Paths.get(JMX_PATH);

        if (!Files.exists(path)) {
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                System.err.println("创建目录失败: " + e.getMessage());
            }
        }

        Path jmxPath = path.resolve(planId + ".jmx");

        SaveService.saveTree(testPlanTree, new FileOutputStream(jmxPath.toString()));

        return jmxPath.toString();
    }

    public static boolean transformJmx(TestPlanVO testPlanVO, String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(inputFile);
            NodeList threadGroupList = doc.getElementsByTagName("ThreadGroup");

            List<ThreadGroupVO> threadGroupVOList = testPlanVO.getThreadGroupList();
            // 判断线程组的个数是否一致
            if(threadGroupVOList.size() != threadGroupList.getLength()) {
                return false;
            }

            for (int i = 0; i < threadGroupList.getLength(); i++) {
                Node threadGroup = threadGroupList.item(i);
                ThreadGroupVO threadGroupVO = threadGroupVOList.get(i);
                // 替换为新的ThreadGroup节点
                Element newThreadGroup = doc.createElement("kg.apc.jmeter.threads.SteppingThreadGroup");
                newThreadGroup.setAttribute("guiclass", "kg.apc.jmeter.threads.SteppingThreadGroupGui");
                newThreadGroup.setAttribute("testclass", "kg.apc.jmeter.threads.SteppingThreadGroup");
                newThreadGroup.setAttribute("testname", "jp@gc - Stepping Thread Group");
                newThreadGroup.setAttribute("enabled", "true");

                Element numThreads = doc.createElement("stringProp");
                numThreads.setAttribute("name", "ThreadGroup.num_threads");
                numThreads.setTextContent(String.valueOf(threadGroupVO.getThreadNum()));
                newThreadGroup.appendChild(numThreads);

                Element onSampleError = doc.createElement("stringProp");
                onSampleError.setAttribute("name", "ThreadGroup.on_sample_error");
                onSampleError.setTextContent("continue");
                newThreadGroup.appendChild(onSampleError);

                Element initialDelay = doc.createElement("stringProp");
                initialDelay.setAttribute("name", "Threads initial delay");
                initialDelay.setTextContent(String.valueOf(threadGroupVO.getInitialDelay()));
                newThreadGroup.appendChild(initialDelay);

                Element startUsersCount = doc.createElement("stringProp");
                startUsersCount.setAttribute("name", "Start users count");
                startUsersCount.setTextContent(String.valueOf(threadGroupVO.getStartUsersCount()));
                newThreadGroup.appendChild(startUsersCount);

                Element startUsersCountBurst = doc.createElement("stringProp");
                startUsersCountBurst.setAttribute("name", "Start users count burst");
                startUsersCountBurst.setTextContent(String.valueOf(threadGroupVO.getStartUsersCountBurst()));
                newThreadGroup.appendChild(startUsersCountBurst);

                Element startUsersPeriod = doc.createElement("stringProp");
                startUsersPeriod.setAttribute("name", "Start users period");
                startUsersPeriod.setTextContent(String.valueOf(threadGroupVO.getStartUsersPeriod()));
                newThreadGroup.appendChild(startUsersPeriod);

                Element stopUsersCount = doc.createElement("stringProp");
                stopUsersCount.setAttribute("name", "Stop users count");
                stopUsersCount.setTextContent(String.valueOf(threadGroupVO.getStopUsersCount()));
                newThreadGroup.appendChild(stopUsersCount);

                Element stopUsersPeriod = doc.createElement("stringProp");
                stopUsersPeriod.setAttribute("name", "Stop users period");
                stopUsersPeriod.setTextContent(String.valueOf(threadGroupVO.getStopUsersPeriod()));
                newThreadGroup.appendChild(stopUsersPeriod);

                Element flighttime = doc.createElement("stringProp");
                flighttime.setAttribute("name", "flighttime");
                flighttime.setTextContent(String.valueOf(threadGroupVO.getFlighttime()));
                newThreadGroup.appendChild(flighttime);

                Element rampUp = doc.createElement("stringProp");
                rampUp.setAttribute("name", "rampUp");
                rampUp.setTextContent(String.valueOf(threadGroupVO.getRampUp()));
                newThreadGroup.appendChild(rampUp);

                Element threadGroupMainController = doc.createElement("elementProp");
                threadGroupMainController.setAttribute("name", "ThreadGroup.main_controller");
                threadGroupMainController.setAttribute("elementType", "LoopController");
                threadGroupMainController.setAttribute("guiclass", "LoopControlPanel");
                threadGroupMainController.setAttribute("testclass", "LoopController");
                threadGroupMainController.setAttribute("testname", "循环控制器");
                threadGroupMainController.setAttribute("enabled", "true");

                Element continueForever = doc.createElement("boolProp");
                continueForever.setAttribute("name", "LoopController.continue_forever");
                continueForever.setTextContent("false");
                threadGroupMainController.appendChild(continueForever);

                Element loopControllerLoops = doc.createElement("intProp");
                loopControllerLoops.setAttribute("name", "LoopController.loops");
                loopControllerLoops.setTextContent("-1");
                threadGroupMainController.appendChild(loopControllerLoops);

                newThreadGroup.appendChild(threadGroupMainController);

                threadGroup.getParentNode().replaceChild(newThreadGroup, threadGroup);
            }

            transformJmxForSteppingThread(doc, testPlanVO);

            doc.getDocumentElement().normalize();
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));
            transformer.transform(source, result);
        } catch (ParserConfigurationException | SAXException | TransformerException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void transformJmxForSteppingThread(Document doc, TestPlanVO testPlanVO) {


        List<ThreadGroupVO> threadGroupVOList = testPlanVO.getThreadGroupList();


        NodeList listeners = doc.getElementsByTagName("BeanShellListener");
        if(listeners.getLength() != threadGroupVOList.size()) {
            return;
        }

        for (int i = 0; i < listeners.getLength(); i++) {
            ThreadGroupVO threadGroupVO = threadGroupVOList.get(i);

            int gap = threadGroupVO.getStartUsersCount();
            int max_iter = threadGroupVO.getThreadNum() / gap;

            String script = String.format(
                    "import org.apache.jmeter.threads.JMeterContextService;\n" +
                            "\n" +
                            "int num = JMeterContextService.getNumberOfThreads();\n" +
                            "int gap = %d;\n" +
                            "int max_iter = %d;\n" +
                            "for(int i=1; i<=max_iter; i++) {\n" +
                            "\tif(num <= gap * i) {\n" +
                            "\t\tvars.put(\"thread\",String.valueOf(gap * i));\n" +
                            "\t\tbreak;\n" +
                            "\t}\n" +
                            "}", gap, max_iter
            );

            Element listener = (Element) listeners.item(i);
            NodeList stringProps = listener.getElementsByTagName("stringProp");
            for (int j = 0; j < stringProps.getLength(); j++) {
                Element stringProp = (Element) stringProps.item(j);
                stringProp.setTextContent(script);
            }
        }
    }

    public static TestPlan createTestPlan(TestPlanVO testPlanVO) {
        TestPlan testPlan = new TestPlan();
        testPlan.setProperty(TestElement.TEST_CLASS, TestPlan.class.getName());

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

    public static ResultCollector createResultCollector(PressureMeasurementService service, int planId) {

        Summariser summer = new Summariser();
        MyResultCollector resultCollector = new MyResultCollector(summer, planId);
        resultCollector.setName("collector");
        resultCollector.setPressureMeasurementService(service);
        resultCollector.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        resultCollector.setProperty(TestElement.GUI_CLASS, StatVisualizer.class.getName());
        resultCollector.setFilename("./test.jtl");
        resultCollector.setEnabled(true);

        return resultCollector;
    }

    public static AggregateReport createAggregateReport(int planId) {
        Summariser summer = new Summariser();
        AggregateReport aggregateReport = new AggregateReport(summer, planId);
        aggregateReport.setName("aggregate report");
//        aggregateReport.setPressureMeasurementService(service);
        aggregateReport.setProperty(TestElement.TEST_CLASS, ResultCollector.class.getName());
        aggregateReport.setProperty(TestElement.GUI_CLASS, StatVisualizer.class.getName());
        aggregateReport.setFilename("./test.jtl");
        aggregateReport.setEnabled(true);
        return aggregateReport;
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
