package com.example.util;

import com.example.rest.filter.limit.ParameterType;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.MultivaluedMap;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 23/11/2019
 */
public class HttpHelper {

    public static boolean validateIp(final String ip) {
        String PATTERN = "^((0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)\\.){3}(0|1\\d?\\d?|2[0-4]?\\d?|25[0-5]?|[3-9]\\d?)$";
        return ip.matches(PATTERN);
    }

    public static boolean validIP(String ip) {
        try {
            if ( ip == null || ip.isEmpty() ) {
                return false;
            }

            String[] parts = ip.split( "\\." );
            if ( parts.length != 4 ) {
                return false;
            }

            for ( String s : parts ) {
                int i = Integer.parseInt( s );
                if ( (i < 0) || (i > 255) ) {
                    return false;
                }
            }
            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String geParameterValueFromHeader(ContainerRequestContext requestContext, String parameterName) {
        return geParameterValueFrom(requestContext, parameterName, ParameterType.HEADER);
    }

    public static String geParameterValueFromPathParam(ContainerRequestContext requestContext, String parameterName) {
        return geParameterValueFrom(requestContext, parameterName, ParameterType.PATH_PARAM);
    }

    public static String geParameterValueFromQueryParam(ContainerRequestContext requestContext, String parameterName) {
        return geParameterValueFrom(requestContext, parameterName, ParameterType.QUERY_PARAM);
    }

    public static String geParameterValueFrom(ContainerRequestContext requestContext, String parameterName, ParameterType parameterType) {
        String parameterValue = "";
        if (parameterType == ParameterType.PATH_PARAM) {
            List pathParameters = requestContext.getUriInfo().getPathParameters().get(parameterName);
            if (pathParameters != null && pathParameters.size() > 0)
                parameterValue = requestContext.getUriInfo().getPathParameters().get(parameterName).get(0);//Default Path Param
        }
        if (parameterType == ParameterType.HEADER)
            parameterValue = requestContext.getHeaderString(parameterName);
        if (parameterType == ParameterType.QUERY_PARAM) {
            List queryParams = requestContext.getUriInfo().getQueryParameters().get(parameterName);// "lang"
            if (queryParams != null && queryParams.size() > 0) {
                parameterValue = requestContext.getUriInfo().getQueryParameters().get(parameterName).get(0);
            }
        }
        return parameterValue;
    }

    public static Language getLanguageFromQueryParam(ContainerRequestContext requestContext) {
        Language lang = Language.FA;
        if ("en".equalsIgnoreCase(geParameterValueFromQueryParam(requestContext, HttpConstant.LANGUAGE_QUERY)))
            lang = Language.EN;
        return lang;
    }

    public static Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }

    public static String getMethodPathFromResource(ResourceInfo resourceInfo) {
        String methodStr = "";
        Path pathMethod = null;
        if (resourceInfo.getResourceMethod() != null) {
            pathMethod = resourceInfo.getResourceMethod().getAnnotation(Path.class);
        }
        if (pathMethod != null)
            methodStr = pathMethod.value() + "";

        return methodStr;
    }

    public static String getPathFromResource(ResourceInfo resourceInfo) {
        String methodStr = "";
        Path pathMethod = null;
        Path pathClass = null;
        if (resourceInfo.getResourceMethod() != null) {
            pathMethod = resourceInfo.getResourceMethod().getAnnotation(Path.class);
            pathClass = resourceInfo.getResourceMethod().getDeclaringClass().getAnnotation(Path.class);
        }
        String classdStr = "";
        if (pathMethod != null)
            methodStr = pathMethod.value() + "";
        if (pathClass != null)
            classdStr = pathClass.value() + "";
        return classdStr + "/" + methodStr;
    }

    public static String getRequestClientIp(HttpServletRequest req) {
        String addr = req.getHeader("X-FORWARDED-FOR");
        if (addr == null || addr.trim().isEmpty())
            addr = req.getRemoteAddr();
        return addr;
    }

    public static String getRequestClientIpComplete(HttpServletRequest req) {
        String client_ip = req.getHeader("X-Real-IP");
        if (client_ip == null || client_ip.isEmpty()) { // extract from forward ips
            client_ip = req.getHeader("PSU-IP-Address");
        }
        if (client_ip == null || client_ip.isEmpty()) { // extract from forward ips
            String ipForwarded = req.getHeader("X-FORWARDED-FOR");
            String[] ips = ipForwarded == null ? null : ipForwarded.split(",");
            client_ip = (ips == null || ips.length == 0) ? null : ips[0];
            // extract from remote addr
            client_ip = (client_ip == null || client_ip.isEmpty()) ? req.getRemoteAddr() : client_ip;
        }
        return client_ip;
    }

    /**
     * include override exclude
     * excludes={"A","B"} , includes={"B"}
     * result={"B",other params}
     *
     * @param m
     * @param excludes
     * @param includes
     * @return
     */
    public static Map<String, String> convertMultiToRegularMap(MultivaluedMap<String, String> m,
                                                               List<String> excludes, List<String> includes) {
        Map<String, String> map = new HashMap<String, String>();
        excludes = fillConstantExcludeParamsIntoList(excludes);

        if (m == null) {
            return map;
        }
        for (Map.Entry<String, List<String>> entry : m.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String s : entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }

            if (entry.getKey().equalsIgnoreCase("Card-No")) {
//                System.out.println("YES Card-No");
                map.put(entry.getKey(), maskCard10Char(sb.toString()));
            } else if (entry.getKey().equalsIgnoreCase("Pin2") || entry.getKey().equalsIgnoreCase("Password")) {
//                System.out.println("Pin2");
                map.put(entry.getKey(), "*****");
            }
            boolean excluded = false;
            for (String exclude : excludes) {//exclude to map for print to log
                if (entry.getKey().equalsIgnoreCase(exclude)) {
//                    System.out.println("###FIND FOR EXCLUDE !" + exclude); // todo delete
                    excluded = true;
                    break;
                }
            }
            if (!excluded && !(entry.getKey().equalsIgnoreCase("Card-No")) &&
                    !(entry.getKey().equalsIgnoreCase("Pin2") || entry.getKey().equalsIgnoreCase("Password"))) {
//                System.out.println("ELSE EXCLUDE!" + entry.getKey()); // todo delete
                map.put(entry.getKey(), sb.toString());
            }

        }
        return map;
    }

    public static Map<String, String> convertMultiToRegularMap(MultivaluedMap<String, String> m) {
        Map<String, String> map = new HashMap<String, String>();
        if (m == null) {
            return map;
        }
//        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : m.entrySet()) {
            StringBuilder sb = new StringBuilder();
            for (String s : entry.getValue()) {
                if (sb.length() > 0) {
                    sb.append(',');
                }
                sb.append(s);
            }
            if (entry.getKey().equalsIgnoreCase("Card-No"))
//            stringBuilder.append(entry.getKey()+"="+AppConstants.maskCard11Char(sb.toString()));
                map.put(entry.getKey(), maskCard10Char(sb.toString()));
            else if (entry.getKey().equalsIgnoreCase("Pin2") || entry.getKey().equalsIgnoreCase("Password")) {
//                stringBuilder.append(entry.getKey()+"="+"*****");
                map.put(entry.getKey(), "*****");
            } else if (entry.getKey().equalsIgnoreCase("headers.X-Postman-Interceptor-Id") ||
                    entry.getKey().equalsIgnoreCase("Mobile-No") ||
//                    entry.getKey().equalsIgnoreCase("User-Agent") ||
                    entry.getKey().equalsIgnoreCase("App-Token1") ||
                    entry.getKey().equalsIgnoreCase("App-Token2") ||
                    entry.getKey().equalsIgnoreCase("App-Info") ||
                    entry.getKey().equalsIgnoreCase("lat") ||
                    entry.getKey().equalsIgnoreCase("lon") ||
                    entry.getKey().equalsIgnoreCase("Device-Id") ||
                    entry.getKey().equalsIgnoreCase("Op-Code") ||
                    entry.getKey().equalsIgnoreCase("Tran-Id") ||
                    entry.getKey().equalsIgnoreCase("App-Id") ||
                    entry.getKey().equalsIgnoreCase("Session-Id") ||
                    entry.getKey().equalsIgnoreCase("Connection") ||
                    entry.getKey().equalsIgnoreCase("Cache-Control") ||
                    entry.getKey().equalsIgnoreCase("Accept") ||
                    entry.getKey().equalsIgnoreCase("Accept-Encoding") ||
                    entry.getKey().equalsIgnoreCase("Content-Length") ||
//                    entry.getKey().equalsIgnoreCase("X-Forwarded-For") ||
                    entry.getKey().equalsIgnoreCase("X-Forwarded-Proto") ||
//                    entry.getKey().equalsIgnoreCase("App-Info") ||
//                    entry.getKey().equalsIgnoreCase(HttpConstant.ATTRIBUTE_PATH_CLASS_METHOD) ||
                    entry.getKey().equalsIgnoreCase("Time-Info") ||
//                    entry.getKey().equalsIgnoreCase("App-Uuid") ||
                    entry.getKey().equalsIgnoreCase("X-Postman-Interceptor-Id") ||
                    entry.getKey().equalsIgnoreCase("Origin") ||
                    entry.getKey().equalsIgnoreCase("Authorization") ||//add by m-asvadi 970431
                    entry.getKey().equalsIgnoreCase("Postman-Token")) {

            } else
                map.put(entry.getKey(), sb.toString());
//            for (String include : includes) {//include to map for print to log
//                if (entry.getKey().equalsIgnoreCase(include))
//                    result.put(entry.getKey(), sb.toString());
//            }


//            stringBuilder.append(entry.getKey()+"="+sb.toString());
//            stringBuilder.append("\n");
        }
        return map;
    }

    public static String getRequestClientInfo(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
        sb.append("remoteAddr:").append(req.getRemoteAddr()).append("  remoteHost:").append(req.getRemoteHost());
        sb.append("   X-FORWARDED-FOR:").append(req.getHeader("X-FORWARDED-FOR"));
        sb.append("   Proxy-Client-IP:").append(req.getHeader("Proxy-Client-IP"));
        sb.append("   ReqURI:").append(req.getRequestURI());
        sb.append("   ReqURL:").append(req.getRequestURL());
        sb.append("   localPort:").append(req.getLocalPort());
        return sb.toString();
    }


    public static String getHttpHeaderRequest(HttpServletRequest req) {
        StringBuilder sb = new StringBuilder();
//        HttpSession session = req.getSession(true);
        sb.append(" remoteAddr:").append(String.format("%-15.15s", req.getRemoteAddr())).append(":").
                append(String.format("%-5.5s", req.getRemotePort())).append(" ,remoteHost:").append(String.format("%-25.25s", req.getRemoteHost()));
        sb.append(" ,X-FORWARDED-FOR:").append(String.format("%-15.15s", req.getHeader("X-FORWARDED-FOR")));
        sb.append(" ,Proxy-Client-IP:").append(req.getHeader("Proxy-Client-IP"));
        sb.append(" ,localPort:").append(req.getLocalPort());
        sb.append(" ,ReqURI:").append(req.getRequestURI());
        sb.append(" ,ReqURL:").append(req.getRequestURL());
//        sb.append(" ,sessionId: " + session.getId());
        return sb.toString();
    }

//    public static HttpHeaderRequest extractHttpHeaderFromRequest(HttpServletRequest req) {
//        HttpHeaderRequest result = null;
//        try {
//            result = new HttpHeaderRequest();
//            HttpSession session = req.getSession(true);
////            result.setSessionId(session.getId());
//            result.setRemoteAddr(req.getRemoteAddr() + ":" + req.getRemotePort());
//            result.setRemoteHost(req.getRemoteHost());
//            result.setProxyClientIP(req.getHeader("Proxy-Client-IP"));
//            result.setReqURI(req.getRequestURI());
//            result.setReqURL(req.getRequestURL().toString());
//            result.setLocalPort(req.getLocalPort());
//            result.setHost(AppConstants.getHostSpec());
//            LOGGER_HTTP_HEADER.info("Header: " + HttpHelper.getHttpHeaderRequest(req));
//        } catch (Exception ex) {
//            LOGGER.error("EXCEPTION->" + ex.getMessage());
//        }
//        return result;
//    }

    public static List<String> fillConstantExcludeParamsIntoList(List<String> excludeParamList) {
        excludeParamList.add(("headers.X-Postman-Interceptor-Id"));
        excludeParamList.add("Mobile-No");
        excludeParamList.add("User-Agent");
        excludeParamList.add("App-Token1");
        excludeParamList.add("App-Token2");
        excludeParamList.add("App-Info");
        excludeParamList.add("lat");
        excludeParamList.add("lon");
        excludeParamList.add("Device-Id");
        excludeParamList.add("Op-Code");
        excludeParamList.add("Tran-Id");
        excludeParamList.add("App-Id");
        excludeParamList.add("Session-Id");
        excludeParamList.add("Connection");
        excludeParamList.add("Cache-Control");
        excludeParamList.add("Accept");
        excludeParamList.add(("Accept-Encoding"));
        excludeParamList.add(("Content-Length"));
        excludeParamList.add("X-Forwarded-For");
        excludeParamList.add("X-Forwarded-Proto");
        excludeParamList.add("Time-Info");
        excludeParamList.add("Origin");
        excludeParamList.add("X-Postman-Interceptor-Id");
        excludeParamList.add("Origin");
        excludeParamList.add("Authorization");
        excludeParamList.add("Postman-Token");
        return excludeParamList;
    }

    public static String maskCard10Char(String source) { //OK BY NAMDCHIAN 980505
        String masked = "";//6037701012345678
        if (source != null && source.length() > 3) {
            if (source.length() > 15 && source.length() < 20) {//card
                masked = source.substring(0, 6);//603770
                for (int i = source.length() - 4 - 6; i > 0; i--)//*6
                    masked += "*";//603770******
                masked += source.substring(source.length() - 4);//45678
            }
        }
        return masked;//603770*****45678
    }
}
