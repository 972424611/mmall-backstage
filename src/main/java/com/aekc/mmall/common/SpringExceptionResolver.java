package com.aekc.mmall.common;

import com.aekc.mmall.exception.CustomException;
import com.aekc.mmall.utils.JsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class SpringExceptionResolver implements HandlerExceptionResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception exception) {
        String url = httpServletRequest.getRequestURL().toString();
        String defaultMsg = "System error";
        JsonData jsonData;
        if (exception instanceof CustomException) {
            jsonData = JsonData.fail(exception.getMessage());
            LOGGER.error("自定义异常, url: {}", url, exception.getMessage());
        } else {
            jsonData = JsonData.fail(defaultMsg);
            LOGGER.error("系统异常, url: {}", url, exception.getMessage());
        }
        PrintWriter writer = null;
        try {
            httpServletResponse.setContentType("application/json;charset=UTF-8");
            writer = httpServletResponse.getWriter();
            String msg = JsonUtil.objectToJson(jsonData);
            if (StringUtils.isNotBlank(msg)) {
                writer.write(msg);
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.error("系统异常, PrintWriter写入异常");
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
        return null;
    }
}
