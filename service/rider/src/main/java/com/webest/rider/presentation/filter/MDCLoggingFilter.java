package com.webest.rider.presentation.filter;

import static com.webest.rider.common.constant.RiderStaticVariable.LOGGING_SERVICE_NAME;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

@Slf4j
public class MDCLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        try {
            MDC.put("service", LOGGING_SERVICE_NAME);
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
