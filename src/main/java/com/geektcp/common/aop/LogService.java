package com.geektcp.common.aop;

import com.alibaba.fastjson.JSONObject;
import com.geektcp.common.spring.util.HttpRequestHeadUtils;
import com.geektcp.common.aop.feign.ILogFeign;
import com.geektcp.common.aop.context.SysLogContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Objects;

/**
 * @author Mr.Tang  2021/5/10 15:34
 */
@Slf4j
@Component
@Aspect
public class LogService {
    @Autowired
    private ILogFeign logFeign;

    private static ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Pointcut("@annotation(com.geektcp.common.aop.OperatorLog)")
    public void aspect() {
        // nothing
    }

    @Before("aspect()")
    public void doBefore(final JoinPoint joinPoint) {
    }

    @AfterReturning(returning = "result", pointcut = "aspect()")
    public void doAfterReturning(final JoinPoint joinPoint, final Object result) {
        SysLogContext context = getMethodDescription(joinPoint);
        JSONObject response = (JSONObject) JSONObject.toJSON(result);
        if (!Objects.isNull(response) && !Objects.isNull(response.getBoolean("success")) && response.getBoolean("success")) {
            context.setIsSuccessed(1);
        } else {
            context.setIsSuccessed(0);
        }
        context.setOperateDate(new Date());
        context.setUserId(HttpRequestHeadUtils.getCurUID());
        context.setUsername(HttpRequestHeadUtils.getValueByKey("username"));
        context.setName(HttpRequestHeadUtils.getCurName());
        context.setTenantId(HttpRequestHeadUtils.getCurTenantId());
        context.setIp(HttpRequestHeadUtils.getCurIp());
        context.setCreateBy(HttpRequestHeadUtils.getCurUID());
        try {
            logFeign.insert(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterThrowing(throwing = "ex", pointcut = "aspect()")
    public void doAfterThrowing(final JoinPoint joinPoint, Exception ex) {
        try {
            log.info("running.....");
            log.error("exception", ex);
        } catch (Exception e) {
            log.error("exception", e);
        }
    }

    private SysLogContext getMethodDescription(final JoinPoint joinPoint) {
        SysLogContext infoVo = new SysLogContext();
        final String targetName = joinPoint.getTarget().getClass().getName();
        final String methodName = joinPoint.getSignature().getName();
        final Object[] arguments = joinPoint.getArgs();
        Class<?> targetClass = null;
        try {
            targetClass = Class.forName(targetName);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        }
        if (targetClass == null) {
            return infoVo;
        }
        final Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                final Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    String infoString = method.getAnnotation(OperatorLog.class).infoString();
                    if (StringUtils.isNotBlank(infoString)) {
                        return JSONObject.parseObject(infoString, SysLogContext.class);
                    }
                    break;
                }
            }
        }
        return infoVo;
    }
}
