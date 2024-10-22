package com.webest.rider.presentation.interceptor;

import com.webest.rider.common.exception.RiderErrorCode;
import com.webest.rider.common.exception.RiderException;
import com.webest.web.common.CommonStaticVariable;
import com.webest.web.common.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RiderInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        // null인 경우는 gateway에서 걸러진다고 판단
        if (request.getHeader(CommonStaticVariable.X_USER_ROLE) != null) {
            UserRole requestRole = UserRole.valueOf(
                request.getHeader(CommonStaticVariable.X_USER_ROLE));

            // master나 Rider가 아니면 걸러진다.
            if (!(requestRole.equals(UserRole.MASTER) || requestRole.equals(UserRole.RIDER))) {
                throw new RiderException(RiderErrorCode.NOT_RIDER);
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
