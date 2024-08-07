package com.modutaxi.api.common.log;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class IODebugLogAop {
    @AfterThrowing(pointcut = "within(com.modutaxi.api..*) && !within(com.modutaxi.api.common.auth.jwt..*) && !within(com.modutaxi.api.common.constants..*)", throwing = "exception")
    public void afterThrowingLog(JoinPoint joinPoint, Exception exception) {
        Method method = getMethod(joinPoint);
        log.debug(String.format("AFTER THROWING[%d] %s.%s => %s", Thread.currentThread().getId(), joinPoint.getTarget().getClass().getCanonicalName(), method.getName(), exception));
    }

    @Around("within(com.modutaxi.api..*) && !within(com.modutaxi.api.common.auth.jwt..*) && !within(com.modutaxi.api.common.constants..*)")
    public Object controllerLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Method method = getMethod(joinPoint);
        StringBuilder parameters = new StringBuilder();
        parameters.append(String.format("BEFORE[%d] %s.%s => ", Thread.currentThread().getId(), joinPoint.getTarget().getClass().getCanonicalName(), method.getName()));
        Arrays.stream(joinPoint.getArgs()).forEach(arg -> parameters.append(arg).append(", "));
        log.debug(parameters.toString());
        Object returnObj = joinPoint.proceed();
        log.debug(String.format("AFTER[%d] %s.%s => %s", Thread.currentThread().getId(), joinPoint.getTarget().getClass().getCanonicalName(), method.getName(), returnObj));
        return returnObj;
    }

    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }
}
