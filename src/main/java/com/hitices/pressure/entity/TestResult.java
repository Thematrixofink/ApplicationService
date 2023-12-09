package com.hitices.pressure.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestResult {
    private int id;
    private int httpSamplerProxyId;
    private long timestamp;
    private long startTime;
    private long endTime;
    private long idleTime;
    private long pauseTime;
    private long connectTime;
    private long latency;
    private int responseCode;
    private String responseData;
    private String responseHeaders;
    private long bytes;
    private int headersSize;
    private long bodySize;
    private long sentBytes;
}
