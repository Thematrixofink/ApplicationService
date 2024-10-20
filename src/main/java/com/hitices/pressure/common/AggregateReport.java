package com.hitices.pressure.common;

import com.hitices.pressure.domain.vo.AggregateReportVO;
import com.hitices.pressure.utils.JMeterUtil;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AggregateReport extends ResultCollector {

  private int planId;
  private transient HashMap<String, PercentageCalculator> calculatorMap;
  private final double boundaryThreshold = 0.2;

  public AggregateReport() {
    super();
    calculatorMap = new HashMap<>();
  }

  public AggregateReport(Summariser summariser, int planId) {
    super(summariser);
    this.planId = planId;
    calculatorMap = new HashMap<>();
  }

  @Override
  public void sampleOccurred(SampleEvent event) {
    super.sampleOccurred(event);
    SampleResult result = event.getResult();
    String label = result.getSampleLabel();
    if (label.startsWith("并发线程数-") && !label.endsWith("-${thread}")) {
      if (!calculatorMap.containsKey(label)) {
        calculatorMap.put(label, new PercentageCalculator());
      }
      calculatorMap.get(label).addSample(result);
    }
  }

  private AggregateReportVO getBound(List<AggregateReportVO> aggregateReportVOList) {
    AggregateReportVO nullReport = new AggregateReportVO(-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    if (aggregateReportVOList.size() <= 2) {
      return nullReport;
    }
    AggregateReportVO firstReport = aggregateReportVOList.get(0);
    double firstK = firstReport.getSamplesNum() / firstReport.getId();
    for (int i = 1; i < aggregateReportVOList.size(); i++) {
      AggregateReportVO previousReport = aggregateReportVOList.get(i - 1);
      AggregateReportVO currentReport = aggregateReportVOList.get(i);
      double currentK =
          (currentReport.getSamplesNum() - previousReport.getSamplesNum())
              / (currentReport.getId() - previousReport.getId());
      if (currentK / firstK < boundaryThreshold) {
        return currentReport;
      }
    }
    return nullReport;
  }

  @Override
  public void testEnded() {
    List<AggregateReportVO> aggregateReportVOList = new ArrayList<>();
    String filepath = null;
    for (Map.Entry<String, PercentageCalculator> entry : calculatorMap.entrySet()) {
      String label = entry.getKey();

      Pattern pattern = Pattern.compile("-(\\d+)-(-?\\d+)");
      Matcher matcher = pattern.matcher(label);
      if (matcher.find()) {
        String planId = matcher.group(1);
        String numThreads = matcher.group(2);
        Path resDir = Paths.get(JMeterUtil.RES_PATH);
        if (!Files.exists(resDir)) {
          try {
            Files.createDirectory(resDir);
          } catch (IOException e) {
            System.err.println("创建目录失败: " + e.getMessage());
          }
        }
        Path resPath = resDir.resolve(planId + ".csv");
        File file = new File(resPath.toString());
        if (!file.exists()) {
          try {
            file.createNewFile();
          } catch (IOException e) {
            System.err.println("创建文件失败: " + e.getMessage());
          }
        }

        if (filepath == null) {
          filepath = resPath.toString();
        }

        double[] percentileList = getPercentile(label, new double[] {0.5, 0.9, 0.95, 0.99});

        long sampleCount = getSampleCount(label);
        double meanResponseTime = getMeanResponseTime(label);
        double minResponseTime = getMinResponseTime(label);
        double maxResponseTime = getMaxResponseTime(label);
        double tps = getThroughput(label);
        double errorRate = getErrorRate(label);

        aggregateReportVOList.add(
            new AggregateReportVO(
                Integer.valueOf(numThreads),
                Integer.valueOf(planId),
                sampleCount,
                meanResponseTime,
                percentileList[0],
                minResponseTime,
                maxResponseTime,
                percentileList[1],
                percentileList[2],
                percentileList[3],
                tps,
                errorRate));
      }
    }
    Collections.sort(aggregateReportVOList, Comparator.comparingInt(AggregateReportVO::getId));

    AggregateReportVO boundaryReport = getBound(aggregateReportVOList);

    aggregateReportVOList.add(boundaryReport);

    try (FileWriter writer = new FileWriter(filepath, true)) {
      for (AggregateReportVO aggregateReportVO : aggregateReportVOList) {
        StringJoiner sj = new StringJoiner(",");
        sj.add(String.valueOf(aggregateReportVO.getId()));
        sj.add(String.valueOf(aggregateReportVO.getSamplesNum()));
        sj.add(String.valueOf(aggregateReportVO.getAverage()));
        sj.add(String.valueOf(aggregateReportVO.getMedian()));
        sj.add(String.valueOf(aggregateReportVO.getP90()));
        sj.add(String.valueOf(aggregateReportVO.getP95()));
        sj.add(String.valueOf(aggregateReportVO.getP99()));
        sj.add(String.valueOf(aggregateReportVO.getMin()));
        sj.add(String.valueOf(aggregateReportVO.getMax()));
        sj.add(String.valueOf(aggregateReportVO.getTps()));
        sj.add(String.valueOf(aggregateReportVO.getErrorRate()));

        writer.write(sj + "\n");
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
    super.testEnded();
  }

  @Override
  public void clearData() {
    super.clearData();
  }

  public long getSampleCount(String label) {
    return calculatorMap.get(label).getCount();
  }

  public double getMeanResponseTime(String label) {
    return calculatorMap.get(label).getMean();
  }

  public long getMaxResponseTime(String label) {
    return calculatorMap.get(label).getMax();
  }

  public long getMinResponseTime(String label) {
    return calculatorMap.get(label).getMin();
  }

  public double getErrorRate(String label) {
    return calculatorMap.get(label).getErrorPercentage();
  }

  public double getThroughput(String label) {
    return calculatorMap.get(label).getRate();
  }

  public double getReceivedBytesPerSecond(String label) {
    return calculatorMap.get(label).getBytesPerSecond();
  }

  public double getSentBytesPerSecond(String label) {
    return calculatorMap.get(label).getSentBytesPerSecond();
  }

  public double[] getPercentile(String label, double[] tiles) {
    return calculatorMap.get(label).getPercentile(tiles);
  }
}
