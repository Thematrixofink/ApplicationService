package com.hitices.pressure.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 *
 * @TableName joint_plan_mapper
 */
@TableName(value ="joint_plan_map")
@Data
public class JointPlanMap implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     *
     */
    @TableField("joint_plan_id")
    private Integer jointPlanId;

    /**
     *
     */
    @TableField("plan_id")
    private Integer planId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
