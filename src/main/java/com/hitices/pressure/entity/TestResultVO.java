package com.hitices.pressure.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class TestResultVO {
    private int id;
    private int planId;
    private Date timestamp;
    private Date startTime;
    private Date endTime;
    private long idleTime;
    private long pauseTime;
    private long connectTime;
    private long latency;
    private String responseCode;
    private String responseData;
    private String responseMessage;
    private String responseHeaders;
    private long bytes;
    private int headersSize;
    private long bodySize;
    private long sentBytes;
}
