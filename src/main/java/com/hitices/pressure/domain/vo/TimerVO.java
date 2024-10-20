package com.hitices.pressure.domain.vo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ConstantTimerVO.class, name = "ConstantTimer"),
        @JsonSubTypes.Type(value = UniformRandomTimerVO.class, name = "UniformRandomTimer"),
        @JsonSubTypes.Type(value = GaussianRandomTimerVO.class, name = "GaussianRandomTimer"),
        @JsonSubTypes.Type(value = PoissonRandomTimerVO.class, name = "PoissonRandomTimer")
})
public abstract class TimerVO {
    private int id;

    private String timerName;

    private String comment;

    //使用TimerType表示
    private int timerType;

    private int threadGroupId;

}
