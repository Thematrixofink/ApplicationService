package com.hitices.pressure.controller;


import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hitices.pressure.common.MResponse;
import com.hitices.pressure.domain.entity.JointPlanMap;
import com.hitices.pressure.domain.vo.AggregateReportVO;
import com.hitices.pressure.domain.vo.JointPlanVO;
import com.hitices.pressure.service.JointPlanService;
import com.hitices.pressure.service.PressureMeasurementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 多个测试计划联合执行接口
 */
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/jointMeasure")
public class JointMeasureController {

    @Autowired
    private PressureMeasurementService pressureMeasurementService;

    @Autowired
    private JointPlanService jointPlanService;


    /**
     * 根据测试计划Id创建联合测试计划
     * @param jointPlanVO
     * @return
     */
    @PostMapping("/createJointPlans")
    public MResponse<Boolean> createJointPlan(@RequestBody JointPlanVO jointPlanVO){
        //参数校验
        List<Integer> ids = jointPlanVO.getTestPlanIds();
        if(ids.size() < 2) return new MResponse<Boolean>().failedMResponse().set("msg","至少选择两个测试计划进行联合测试!").data(false);
        if(StringUtils.isBlank(jointPlanVO.getName()) || StringUtils.isEmpty(jointPlanVO.getName())) {
            return new MResponse<Boolean>().failedMResponse().set("msg","测试计划名称不可以为空!").data(false);
        }
        //添加到数据库
        jointPlanService.addJointPlan(jointPlanVO);
        return new MResponse<Boolean>().successMResponse().data(true);
    }


    /**
     * 串行执行多个测试计划
     * @param
     * @return
     */
    @PostMapping("/measurePlans")
    public MResponse<Boolean> measurePlans(@RequestParam("jointPlanId") Integer jointPlanId) {
        List<JointPlanMap> maps = jointPlanService.getPlanByJointPlanId(jointPlanId);
        AtomicBoolean isFailed = new AtomicBoolean(false);
        AtomicInteger count = new AtomicInteger();
        //串行执行多个测试计划
        CompletableFuture<Void> future = CompletableFuture.completedFuture(null);
        jointPlanService.updateJointPlanStatus(jointPlanId,"Running");
        for (JointPlanMap map : maps) {
            int testPlanId = map.getPlanId();
            future = future.thenCompose(v -> pressureMeasurementService.measureFuture(testPlanId))
                    .thenAccept(result -> {
                        if (!result) {
                            log.info("测试计划执行失败: " + testPlanId);
                            isFailed.set(true);
                        }else{
                            log.info("测试计划执行成功: " + testPlanId);
                        }
                        //如果全部执行完毕，更新联合测试计划状态
                        count.getAndIncrement();
                        if(count.intValue() == maps.size()) {
                            jointPlanService.updateJointPlanStatus(jointPlanId,"Completed");
                        }
                    });
        }
        return new MResponse<Boolean>().successMResponse().data(true).set("msg","联合测试计划开始执行!");
    }

    /**
     * 创建联合测试计划的聚合报告
     * @param jointPlanId
     * @return
     */
    @PostMapping("/createJointReportByPlanId")
    public MResponse<Boolean> createJointReportById(int jointPlanId){
        //参数校验
        List<JointPlanMap> planByJointPlanId = jointPlanService.getPlanByJointPlanId(jointPlanId);
        if(ObjectUtils.isNull(planByJointPlanId) || planByJointPlanId.size() <=1){
            return new MResponse<Boolean>().failedMResponse().set("msg","此Id不存在!").data(false);
        }
        for (JointPlanMap map : planByJointPlanId) {
            boolean b = pressureMeasurementService.addAggregateReport(map.getPlanId());
            if(!b) throw new RuntimeException("Id:"+map.getPlanId()+"  创建聚合报告失败");
        }
        return new MResponse<Boolean>().successMResponse().data(true);
    }

    /**
     * 创建联合测试计划的聚合报告
     * @param jointPlanId
     * @return
     */
    @GetMapping("/getJointReportByPlanId")
    public MResponse<List<AggregateReportVO>> getJointReportById(int jointPlanId){
        //参数校验
        List<JointPlanMap> planByJointPlanId = jointPlanService.getPlanByJointPlanId(jointPlanId);
        if(ObjectUtils.isNull(planByJointPlanId) || planByJointPlanId.size() <=1){
            return new MResponse<List<AggregateReportVO>>().failedMResponse().set("msg","此Id不存在!").data(null);
        }
        List<AggregateReportVO> reports = new ArrayList<>();
        for (JointPlanMap map : planByJointPlanId) {
            AggregateReportVO report = pressureMeasurementService.getAggregateReportByPlanId(map.getPlanId());
            if(ObjectUtils.isNotNull(report) && ObjectUtils.isNotEmpty(report)) reports.add(report);
        }
        if(reports.size() <= 0) return new MResponse<List<AggregateReportVO>>().failedMResponse().data(null).set("msg","未查询到聚合报告，请先创建!");
        return new MResponse<List<AggregateReportVO>>().successMResponse().data(reports);
    }


}
