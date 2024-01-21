package com.hitices.pressure.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
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
