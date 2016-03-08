package com.dianping.cat.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.*;
import ch.qos.logback.classic.util.LoggerNameUtil;
import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.OutputStreamAppender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
import ch.qos.logback.core.joran.spi.ConsoleTarget;
import ch.qos.logback.core.pattern.PatternLayoutBase;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.EnvUtil;
import ch.qos.logback.core.util.OptionHelper;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Trace;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by sunyujia@aliyun.com on 2016/3/7.
 */
public class CatAppender<E> extends UnsynchronizedAppenderBase<E> {
    public CatAppender() {
    }

    @Override
    protected void append(E event) {
        if (event instanceof ILoggingEvent) {
            ILoggingEvent ile = (ILoggingEvent) event;
            if (Level.TRACE.equals(ile.getLevel())) {
                logTrace(ile);
            } else if (Level.ERROR.equals(ile.getLevel())) {
                logError(ile);
            }
        }
    }

    private void logTrace(ILoggingEvent iLoggingEvent) {
        String data = iLoggingEvent.getFormattedMessage();
        buildExceptionStack(iLoggingEvent.getThrowableProxy());
        Cat.logTrace("Logback", name, Trace.SUCCESS, data);
    }

    private void logError(ILoggingEvent iLoggingEvent) {
        String data = iLoggingEvent.getFormattedMessage();
        if (iLoggingEvent.getThrowableProxy() != null) {
            if(iLoggingEvent instanceof ThrowableProxy){
                ThrowableProxy iThrowableProxy = (ThrowableProxy) iLoggingEvent.getThrowableProxy();
                Cat.logError(data, iThrowableProxy.getThrowable());
            }else{
                Cat.logError(data, null);
            }
        }
    }

    private String buildExceptionStack(IThrowableProxy iThrowableProxy) {
        if (iThrowableProxy == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder(2048);
        for (StackTraceElementProxy step : iThrowableProxy.getStackTraceElementProxyArray()) {
            String string = step.toString();
            builder.append(CoreConstants.TAB).append(string);
            ThrowableProxyUtil.subjoinPackagingData(builder, step);
            builder.append(CoreConstants.LINE_SEPARATOR);
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        org.slf4j.Logger log = LoggerFactory.getLogger("abc");
        log.error("TEST ERROR", new RuntimeException());
    }
}

