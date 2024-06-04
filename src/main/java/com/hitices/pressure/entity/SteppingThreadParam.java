package com.hitices.pressure.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("SteppingThreadParam")
public class SteppingThreadParam {
    private int numThreads;
    private double initialDelay;
    private double startUsersCount;
    private double startUsersCountBurst;
    private double startUsersPeriod;
    private double stopUsersCount;
    private double stopUsersPeriod;
    private double flighttime;
    private double rampUp;
}