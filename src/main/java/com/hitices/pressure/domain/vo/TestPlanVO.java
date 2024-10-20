package com.hitices.pressure.domain.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TestPlanVO {

    private int id;

    private String testPlanName;

    private boolean serialized;

    private boolean functionalMode;

    private boolean tearDown;

    private String comment;

    private String status;

    private String namespace;

    private String podName;

    private boolean boundary;

    private List<ThreadGroupVO> threadGroupList;
}
