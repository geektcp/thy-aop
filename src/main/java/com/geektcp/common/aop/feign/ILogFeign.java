package com.geektcp.common.aop.feign;

import com.alibaba.fastjson.JSONObject;
import com.geektcp.common.spring.model.dto.ResponseDTO;
import com.geektcp.common.aop.context.SysLogContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(name = "thy-log")
@Service
public interface ILogFeign {

    @PostMapping("/syslog/add")
    ResponseDTO<JSONObject> insert(@RequestBody SysLogContext context);

}
