package com.aekc.mmall.utils;

import com.aekc.mmall.common.JsonData;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Twilight
 * @date 18-9-10 下午3:35
 */
public class ReturnUtil {

    public static void success(HttpServletResponse response, String message) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter printWriter = response.getWriter()) {
            JsonData result = JsonData.success(message);
            String resultJson = JsonUtil.objectToJson(result);
            assert resultJson != null;
            printWriter.write(resultJson);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fail(HttpServletResponse response, String message) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Headers", "token, Accept, Origin, X-Requested-With, Content-Type, Last-Modified");
        response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter printWriter = response.getWriter()) {
            JsonData result = JsonData.fail(message);
            String resultJson = JsonUtil.objectToJson(result);
            assert resultJson != null;
            printWriter.write(resultJson);
            printWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
