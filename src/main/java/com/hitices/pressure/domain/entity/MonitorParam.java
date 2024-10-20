package com.hitices.pressure.domain.entity;


import java.util.ArrayList;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonitorParam {

    private ArrayList<HardwareRecord> cpuUsage;
    private ArrayList<HardwareRecord> memoryUsage;
    private ArrayList<NetworkRecord> byteTransmitted;
    private ArrayList<NetworkRecord> byteReceived;

}
