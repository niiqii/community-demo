package com.song.community.controller.advice;

import com.song.community.utils.CommunityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Kycni
 * @date 2022/2/26 22:30
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handlerException(Exception e, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        logger.error("服务器发生异常" + e.getMessage());
        
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }

        String xRequestedWith = req.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            resp.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = resp.getWriter();
            writer.write(CommunityUtils.getJSONString(1, "服务器异常！"));
        } else {
            resp.sendRedirect(req.getContextPath() + "/error");
        }
    }
}
