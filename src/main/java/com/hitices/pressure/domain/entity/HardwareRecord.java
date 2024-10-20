package com.hitices.pressure.domain.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HardwareRecord {
    @JsonProperty("name")
    private long name;
    @JsonProperty("usage")
    private double usage;
}
