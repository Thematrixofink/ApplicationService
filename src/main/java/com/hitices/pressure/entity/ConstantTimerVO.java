package com.hitices.pressure.entity;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("ConstantTimer")
public class ConstantTimerVO extends TimerVO{
    private String threadDelay;
}
