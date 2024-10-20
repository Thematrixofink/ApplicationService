package com.hitices.pressure.service;

import com.hitices.pressure.domain.entity.JointPlan;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hitices.pressure.domain.entity.JointPlanMap;
import com.hitices.pressure.domain.vo.JointPlanVO;

import java.util.List;

/**
* @author 24957
* @description 针对表【joint_plan】的数据库操作Service
* @createDate 2024-10-20 15:24:39
*/
public interface JointPlanService extends IService<JointPlan> {

    /**
     * 添加一个联合测试计划
     * @param jointPlanVO
     * @return
     */
    boolean addJointPlan(JointPlanVO jointPlanVO);

    /**
     * 根据联合测试计划的Id得到所包含的所有测试计划的Id
     * @param jointPlanId
     * @return
     */
    List<JointPlanMap> getPlanByJointPlanId(Integer jointPlanId);

    /**
     * 更新联合测试计划的任务状态
     * @param jointPlanId
     * @param status
     */
    boolean updateJointPlanStatus(Integer jointPlanId, String status);
}
