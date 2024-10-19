package com.hitices.pressure.entity;

import lombok.Data;
import java.util.List;

@Data
public class TestPlansVO {

    /**
     * 要执行的多个测试计划的id集合
     */
    List<Integer> testPlanIds;
}
