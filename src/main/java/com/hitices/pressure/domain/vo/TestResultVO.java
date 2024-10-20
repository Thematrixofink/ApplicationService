package com.hitices.pressure.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
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
    private boolean success;
    private String responseCode;
    private String responseData;
    private String responseMessage;
    private String responseHeaders;
    private long bytes;
    private int headersSize;
    private long bodySize;
    private long sentBytes;


//    public boolean getSuccess() {
//        return this.success;
//    }
}
