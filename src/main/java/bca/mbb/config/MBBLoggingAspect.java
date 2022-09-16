package bca.mbb.config;

import bca.mbb.mbbcommonlib.logger.MBBLogger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Aspect
@Component
public class MBBLoggingAspect {

    private final MBBLogger logger;

    public MBBLoggingAspect(MBBLogger logger) {
        this.logger = logger;
    }

    @Pointcut("(bean(*Service) || bean(*Controller)) "
            + "&& execution(public * bca.mbb..*(..)) ")
    public void everyClassExecute() {
        // Do nothing because this is a pointcut
    }

    @Around("everyClassExecute()")
    public Object onClassAround(ProceedingJoinPoint joinPoint) throws Throwable {
        CodeSignature signature = (CodeSignature) joinPoint.getSignature();

        var clazz = signature.getDeclaringType();
        final long start = System.currentTimeMillis();
        final String prefix = "[" + clazz.getSimpleName() + "#" + signature.getName() + "]";

        logger.info("Starting Class " + clazz.getSimpleName() + " on Method " + signature.getName());

        HashMap<String, Object> hashMapRequestInput = new HashMap<>();
        Object[] inputs = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < parameterNames.length; i++) {
            if (inputs[i] != null) {
                if (inputs[i].getClass() == String.class) {
                    String input = String.valueOf(inputs[i]).replaceAll(System.lineSeparator(), "~~~");
                    hashMapRequestInput.put(parameterNames[i], input);
                } else {
                    hashMapRequestInput.put(parameterNames[i], inputs[i]);
                }
            }
        }
        try {
            logger.debug(prefix + " Input : " + hashMapRequestInput);
        } catch (Exception e) {
            printException(prefix, "output", e);
        }

        Object retVal;
        try {
            retVal = joinPoint.proceed();
        } catch (Throwable throwable) {
            printException(prefix, "method", throwable);
            throw throwable;
        }

        try {
            Object output;
            if (retVal == null) {
                output = "";
            } else if (retVal.getClass() == String.class) {
                output = String.valueOf(retVal).replaceAll("\n", "~~~");
            } else {
                output = retVal;
            }
            logger.debug(prefix + " Output : " + output);
        } catch (Exception e) {
            printException(prefix, "output", e);
        }
        logger.info("Finishing Class " + clazz.getSimpleName() + " on Method " + signature.getName() + " in "
                + (System.currentTimeMillis() - start) + " millis.");
        return retVal;
    }

    private void printException(String prefix, String place, Throwable e) {
        logger.error(prefix + " Exception on " + place + " : " + e.getCause());
        logger.error(prefix + " Exception message : " + e.getMessage(), e);
    }
}
