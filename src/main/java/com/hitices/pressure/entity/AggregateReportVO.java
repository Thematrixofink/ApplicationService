package com.hitices.pressure.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AggregateReportVO {
    private int id;
    private int planId;
    private double samplesNum;
    private double average;
    private double median;
    private double min;
    private double max;
    private double p50;
    private double p95;
    private double p99;
    private double tps;
    private double errorRate;
}
