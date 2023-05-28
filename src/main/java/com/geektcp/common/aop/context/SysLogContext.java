package com.geektcp.common.aop.context;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author Mr.Tang  2021/5/10 15:34
 */
@Data
public class SysLogContext {

    private String serviceName;

    private String operateType;

    private String functionType;

    private String description;

    private Integer isSuccessed = 1;

    private String userId;

    private String username;

    private String name;

    private String ip;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operateDate;

    private String tenantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;

    private String createBy;

}
