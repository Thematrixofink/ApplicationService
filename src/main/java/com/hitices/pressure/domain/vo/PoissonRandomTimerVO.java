package com.hitices.pressure.domain.vo;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeName("PoissonRandomTimer")
public class PoissonRandomTimerVO extends TimerVO{
    private double lambda;

    private String constantDelayOffset;
}
