package com.konchalovmaxim.creditconveyorms.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Profile("aspect_logs")
@Component
@Aspect
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.konchalovmaxim.creditconveyorms.*.*.*(..))")
    public Object aroundAllPublicMethodsAdvice(
            ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        //до исполнения метода
        StringBuilder logMessage = new StringBuilder("Begin of ");
        logMessage.append(methodSignature.getName());
        logMessage.append(" with args: ");
        logMessage.append(Arrays.toString(proceedingJoinPoint.getArgs()));
        //до исполнения метода
        LOGGER.info(logMessage.toString());

        Object targetMethodResult = proceedingJoinPoint.proceed();

        //после исполнения метода
        logMessage = new StringBuilder("End of ");
        logMessage.append(methodSignature.getName());
        logMessage.append(" with result: ");
        logMessage.append(targetMethodResult);
        //после исполнения метода
        LOGGER.info(logMessage.toString());

        return targetMethodResult;
    }
}
