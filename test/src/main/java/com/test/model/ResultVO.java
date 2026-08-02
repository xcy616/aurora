package com.test.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * 原项目路径：com.aurora.model.vo.ResultVO
 * 作用：所有接口的统一返回格式
 */
@Data
public class ResultVO<T> implements Serializable {

    private Integer code;
    private String msg;
    private T data;

    public static <T> ResultVO<T> ok(T data) {
        ResultVO<T> r = new ResultVO<>();
        r.setCode(200);
        r.setMsg("成功");
        r.setData(data);
        return r;
    }

    public static <T> ResultVO<T> fail(String msg) {
        ResultVO<T> r = new ResultVO<>();
        r.setCode(500);
        r.setMsg(msg);
        return r;
    }

    public static <T> ResultVO<T> fail(Integer code, String msg) {
        ResultVO<T> r = new ResultVO<>();
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }
}
