package com.aurora.constant;

/**
 * 操作类型常量
 *
 * 消费方：@OptLog(optType = ...) 注解的参数
 * 记录在 t_operation_log 表的 opt_type 字段，用于后台展示操作类型
 */
public interface OptTypeConstant {

    /** 新增或修改 */
    String SAVE_OR_UPDATE = "新增或修改";

    /** 新增 */
    String SAVE = "新增";

    /** 修改 */
    String UPDATE = "修改";

    /** 删除 */
    String DELETE = "删除";

    /** 上传 */
    String UPLOAD = "上传";

    /** 导出 */
    String EXPORT = "导出";

}
