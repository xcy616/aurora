package com.aurora.constant;

/**
 * Quartz 定时任务常量
 *
 * 消费方：quartz 包（JobServiceImpl、定时任务触发）
 * 作用：定义 Quartz 的任务触发策略和 JobDataMap 的 key
 */
public interface ScheduleConstant {

    /** 默认策略（立即执行一次） */
    int MISFIRE_DEFAULT = 0;

    /** 忽略错过的触发（把错过的全部补执行） */
    int MISFIRE_IGNORE_MISFIRES = 1;

    /** 立即执行一次错过的触发（只补最后一次） */
    int MISFIRE_FIRE_AND_PROCEED = 2;

    /** 忽略错过的触发（等下次正常执行） */
    int MISFIRE_DO_NOTHING = 3;

    /** JobDataMap 中任务类名的 key */
    String TASK_CLASS_NAME = "TASK_CLASS_NAME";

    /** JobDataMap 中任务属性的 key */
    String TASK_PROPERTIES = "TASK_PROPERTIES";

}
