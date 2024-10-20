package com.hitices.pressure.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AggregateReportVO {
    private int id;
    private int planId;
    private double samplesNum;
    private double average;
    private double median;
    private double min;
    private double max;
    private double p90;
    private double p95;
    private double p99;
    private double tps;
    private double errorRate;
}
