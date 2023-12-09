package com.hitices.pressure.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("GaussianRandomTimer")
public class GaussianRandomTimerVO extends TimerVO{
    private double deviation;

    private String constantDelayOffset;
}
