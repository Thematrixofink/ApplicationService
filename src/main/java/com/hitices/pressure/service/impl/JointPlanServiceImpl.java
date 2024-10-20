package com.hitices.pressure.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hitices.pressure.domain.entity.JointPlan;
import com.hitices.pressure.domain.entity.JointPlanMap;
import com.hitices.pressure.domain.vo.TestPlanVO;
import com.hitices.pressure.domain.vo.JointPlanVO;
import com.hitices.pressure.repository.JointPlanMapMapper;
import com.hitices.pressure.repository.PressureMeasurementMapper;
import com.hitices.pressure.service.JointPlanService;
import com.hitices.pressure.repository.JointPlanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author 24957
* @description 针对表【joint_plan】的数据库操作Service实现
* @createDate 2024-10-20 15:24:39
*/
@Service
public class JointPlanServiceImpl extends ServiceImpl<JointPlanMapper, JointPlan>
    implements JointPlanService{

    @Autowired
    private PressureMeasurementMapper pressureMeasurementMapper;

    @Autowired
    private JointPlanMapMapper jointPlanMapMapper;


    @Override
    @Transactional
    public boolean addJointPlan(JointPlanVO jointPlanVO) {
        String name = jointPlanVO.getName();
        String comment = jointPlanVO.getComment();
        List<Integer> testPlanIds = jointPlanVO.getTestPlanIds();

        //首先添加JointPlan
        JointPlan jointPlan = new JointPlan();
        jointPlan.setJointPlanName(name);
        jointPlan.setComment(comment);
        jointPlan.setStatus("Created");
        int insert = this.baseMapper.insert(jointPlan);
        if(insert <= 0) throw new RuntimeException("添加联合测试计划失败!");

        //依次检查每个testPlanId是否存在,并添加到数据库
        boolean isExist = testPlanIds.stream().allMatch(id -> {
            TestPlanVO testPlanById = pressureMeasurementMapper.getTestPlanById(id);
            if (ObjectUtils.isNotNull(testPlanById) && ObjectUtils.isNotEmpty(testPlanById)) {
                JointPlanMap jointPlanMap = new JointPlanMap();
                jointPlanMap.setJointPlanId(jointPlan.getId());
                jointPlanMap.setPlanId(id);
                int insert1 = jointPlanMapMapper.insert(jointPlanMap);
                if(insert1 <= 0) return false;
                return true;
            }
            return false;
        });

        //如果添加出现了问题，直接抛出异常，回滚事务
        if(!isExist) {
            throw new RuntimeException("添加联合测试计划与测试计划映射关系失败,测试计划可能不存在!");
        }
        return true;
    }

    @Override
    public List<JointPlanMap> getPlanByJointPlanId(Integer jointPlanId) {
        LambdaQueryWrapper<JointPlanMap> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(JointPlanMap::getJointPlanId,jointPlanId);
        List<JointPlanMap> planMapList = jointPlanMapMapper.selectList(wrapper);
        return planMapList;
    }

    @Override
    @Transactional
    public boolean updateJointPlanStatus(Integer jointPlanId, String status) {
        LambdaQueryWrapper<JointPlan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(JointPlan::getId,jointPlanId);
        JointPlan jointPlan = this.baseMapper.selectOne(queryWrapper);
        jointPlan.setStatus(status);
        int i = this.baseMapper.updateById(jointPlan);
        if(i <= 0){
            throw new RuntimeException("更新联合任务状态失败!");
        }
        return true;
    }
}




