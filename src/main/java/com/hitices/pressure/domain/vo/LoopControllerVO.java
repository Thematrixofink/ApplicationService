package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoopControllerVO {

    private int id;

    private String loopControllerName;

    private String comment;

    private int loopNum;

    private boolean continueForever;

    private int threadGroupId;
}
