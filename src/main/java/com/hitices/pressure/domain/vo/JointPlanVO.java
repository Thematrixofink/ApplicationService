package com.hitices.pressure.domain.vo;

import lombok.Data;
import java.util.List;

@Data
public class JointPlanVO {

    /**
     * 联合测试计划的名称
     */
    String name;

    /**
     * 联合测试计划的备注
     */
    String comment;

    /**
     * 要执行的多个测试计划的id集合
     */
    List<Integer> testPlanIds;
}
