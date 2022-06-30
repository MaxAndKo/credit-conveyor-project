package com.konchalovmaxim.creditconveyorms.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile("aspect_logs")
@Component
@Aspect
@Slf4j
public class LoggingAspect {

    @Around("execution(* com.konchalovmaxim.creditconveyorms.*.*.*(..))")
    public Object aroundAllPublicMethodsAdvice(
            ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        //до исполнения метода
        StringBuilder logMessage = new StringBuilder("Begin of ");
        logMessage.append(methodSignature.getName());
        logMessage.append(" with args: ");
        logMessage.append(Arrays.toString(proceedingJoinPoint.getArgs()));
        //до исполнения метода
        log.info(logMessage.toString());

        Object targetMethodResult = proceedingJoinPoint.proceed();

        //после исполнения метода
        logMessage = new StringBuilder("End of ");
        logMessage.append(methodSignature.getName());
        logMessage.append(" with result: ");
        logMessage.append(targetMethodResult);
        //после исполнения метода
        log.info(logMessage.toString());

        return targetMethodResult;
    }
}
