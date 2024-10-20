package com.hitices.pressure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hitices.pressure.domain.vo.TestPlanVO;
import com.hitices.pressure.domain.vo.ThreadGroupVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

  @Autowired private PressureMeasurementService pressureMeasurementService;

  @Test
  public void testAddAggregateReport() {
    pressureMeasurementService.addAggregateReport(17);
  }

  @Test
  public void updateAggregateReport() {
    pressureMeasurementService.updateAggregateReport(17);
  }

  @Test
  public void createAggregateReport() {
    pressureMeasurementService.addAggregateReport(17);
  }

  @Test
  public void testForSaveConfig() throws IOException, ParserConfigurationException, SAXException {
    TestPlanVO testPlanVO = pressureMeasurementService.getTestPlanById(29);
    testPlanVO.setBoundary(true);
    List<ThreadGroupVO> threadGroupVOList = testPlanVO.getThreadGroupList();

    for (ThreadGroupVO threadGroupVO : threadGroupVOList) {
      threadGroupVO.setStepping(true);
      threadGroupVO.setInitialDelay(0);
      threadGroupVO.setStartUsersCount(10);
      threadGroupVO.setStartUsersCountBurst(0);
      threadGroupVO.setStartUsersPeriod(10);
      threadGroupVO.setThreadNum(100);
      threadGroupVO.setStopUsersCount(100);
      threadGroupVO.setStopUsersPeriod(0);
      threadGroupVO.setFlighttime(10);
      threadGroupVO.setRampUp(0);
    }

    pressureMeasurementService.addBoundaryTestPlan(testPlanVO);
  }

  @Test
  public void testForReadXml()
      throws ParserConfigurationException, IOException, SAXException, TransformerException {
    File inputFile = new File("example1.jmx");
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(inputFile);

    // 获取根元素
    //        Element root = doc.getDocumentElement();
    NodeList threadGroupList = doc.getElementsByTagName("ThreadGroup");
    for (int i = 0; i < threadGroupList.getLength(); i++) {
      Node threadGroup = threadGroupList.item(i);
      // 替换为新的ThreadGroup节点
      Element newThreadGroup = doc.createElement("kg.apc.jmeter.threads.SteppingThreadGroup");
      newThreadGroup.setAttribute("guiclass", "kg.apc.jmeter.threads.SteppingThreadGroupGui");
      newThreadGroup.setAttribute("testclass", "kg.apc.jmeter.threads.SteppingThreadGroup");
      newThreadGroup.setAttribute("testname", "jp@gc - Stepping Thread Group");
      newThreadGroup.setAttribute("enabled", "true");

      Element numThreads = doc.createElement("stringProp");
      numThreads.setAttribute("name", "ThreadGroup.num_threads");
      numThreads.setTextContent("40");
      newThreadGroup.appendChild(numThreads);

      Element onSampleError = doc.createElement("stringProp");
      onSampleError.setAttribute("name", "ThreadGroup.on_sample_error");
      onSampleError.setTextContent("continue");
      newThreadGroup.appendChild(onSampleError);

      Element initialDelay = doc.createElement("stringProp");
      initialDelay.setAttribute("name", "Threads initial delay");
      initialDelay.setTextContent("0");
      newThreadGroup.appendChild(initialDelay);

      Element startUsersCount = doc.createElement("stringProp");
      startUsersCount.setAttribute("name", "Start users count");
      startUsersCount.setTextContent("10");
      newThreadGroup.appendChild(startUsersCount);

      Element startUsersCountBurst = doc.createElement("stringProp");
      startUsersCountBurst.setAttribute("name", "Start users count burst");
      startUsersCountBurst.setTextContent("0");
      newThreadGroup.appendChild(startUsersCountBurst);

      Element startUsersPeriod = doc.createElement("stringProp");
      startUsersPeriod.setAttribute("name", "Start users period");
      startUsersPeriod.setTextContent("3");
      newThreadGroup.appendChild(startUsersPeriod);

      Element stopUsersCount = doc.createElement("stringProp");
      stopUsersCount.setAttribute("name", "Stop users count");
      stopUsersCount.setTextContent("30");
      newThreadGroup.appendChild(stopUsersCount);

      Element stopUsersPeriod = doc.createElement("stringProp");
      stopUsersPeriod.setAttribute("name", "Stop users period");
      stopUsersPeriod.setTextContent("0");
      newThreadGroup.appendChild(stopUsersPeriod);

      Element flighttime = doc.createElement("stringProp");
      flighttime.setAttribute("name", "flighttime");
      flighttime.setTextContent("3");
      newThreadGroup.appendChild(flighttime);

      Element rampUp = doc.createElement("stringProp");
      rampUp.setAttribute("name", "rampUp");
      rampUp.setTextContent("0");
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

    NodeList listeners = doc.getElementsByTagName("BeanShellListener");
    for (int i = 0; i < listeners.getLength(); i++) {
      Element listener = (Element) listeners.item(i);
      NodeList stringProps = listener.getElementsByTagName("stringProp");
      for (int j = 0; j < stringProps.getLength(); j++) {
        Element stringProp = (Element) stringProps.item(j);
        String propName = stringProp.getAttribute("name");
        if (propName.equals("script")) {
          stringProp.setTextContent("haha");
        }
      }
    }

    doc.getDocumentElement().normalize();
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    DOMSource source = new DOMSource(doc);
    StreamResult result = new StreamResult(new File("output.jmx"));
    transformer.transform(source, result);
  }

  @Test
  public void testGetStartAndEnd() {
    System.out.println(pressureMeasurementService.getStartAndEndOfTest(17)[0]);
  }

  @Test
  public void testForBoundaryTest() throws JsonProcessingException {
    pressureMeasurementService.measure(41);
  }

  @Test
  public void seeee() {
    System.out.println(pressureMeasurementService.getBoundaryTestResult(41));

  }
}
