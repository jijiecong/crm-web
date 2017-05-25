
package com.meiren.utils;

import com.meiren.vo.SessionUserVO;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RequestUtil {

    private static String sessionUserKey = "user";

    public RequestUtil() {
    }

    public static SessionUserVO getSessionUser(HttpServletRequest request) {
        return (SessionUserVO) request.getSession().getAttribute(sessionUserKey);
    }

    public static void setSessionUser(HttpServletRequest request, SessionUserVO userVO) {
        request.getSession().setAttribute(sessionUserKey, userVO);
    }

    public static String getString(HttpServletRequest request, String paramName) {
        return request.getParameter(paramName);
    }

    public static String getString(HttpServletRequest request, String paramName, String defaultVal) {
        String val = getString(request, paramName);
        if (StringUtils.isBlank(val)) {
            val = defaultVal;
        }
        return val;
    }


    public static List<String> getArray(HttpServletRequest request, String paramName) {
        List<String> list = new ArrayList<>();
        Map<String, String[]> params = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getKey().startsWith(paramName + "[") && entry.getKey().endsWith("]")
                || (entry.getKey().equals(paramName))) {
                String[] value = entry.getValue();
                list.addAll(Arrays.asList(value));
            }
        }
        return list;
    }

    public static String getStringTrans(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);

        try {
            if (!StringUtils.isBlank(value)) {
                value = new String(value.getBytes("ISO-8859-1"), "utf-8");
            }
        } catch (UnsupportedEncodingException var4) {
            var4.printStackTrace();
        }

        return value;
    }

    public static String getStringTrans(HttpServletRequest request, String paramName, String defaultVal) {
        String val = getStringTrans(request, paramName);
        if (StringUtils.isBlank(val)) {
            val = defaultVal;
        }
        return val;
    }

    public static Boolean getBoolean(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            try {
                return Boolean.valueOf(value);
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static Boolean getBoolean(HttpServletRequest request, String paramName, Boolean defaultVal) {
        Boolean val = getBoolean(request, paramName);
        if (val != null) {
            val = defaultVal;
        }
        return val;
    }


    public static Integer getInteger(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            try {
                return Integer.parseInt(value);
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static Integer getInteger(HttpServletRequest request, String paramName, Integer defaultVal) {
        Integer val = getInteger(request, paramName);
        if (val == null) {
            val = defaultVal;
        }
        return val;
    }

    public static Long getLong(HttpServletRequest request, String paramName) {
        String value = request.getParameter(paramName);
        if (StringUtils.isBlank(value)) {
            return null;
        } else {
            try {
                return Long.parseLong(value);
            } catch (Exception var4) {
                var4.printStackTrace();
                return null;
            }
        }
    }

    public static Long getLong(HttpServletRequest request, String paramName, Long defaultVal) {
        Long val = getLong(request, paramName);
        if (val != null) {
            val = defaultVal;
        }
        return val;
    }
}
