package com.hitices.pressure.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName joint_plan
 */
@TableName(value ="joint_plan")
@Data
public class JointPlan implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField(value = "joint_plan_name")
    private String jointPlanName;

    /**
     *
     */
    @TableField(value = "comment")
    private String comment;

    /**
     * 测试计划状态，已创建，运行中，执行完毕
     */
    @TableField(value = "status")
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
