package com.game.utils.sessionUtils;

import com.game.utils.logUtils.LogUtil;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Component
public class SessionUtil {

    public static void setSession(JSONObject data) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //我们有的时候会在别的层获取request填充一些诸如用户名和IP地址等信息，这个时候如果不想从Controller层传request，可以在service直接使用
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            //HttpServletRequest对象代表客户端的请求，当客户端通过HTTP协议访问服务器时，
            //HTTP请求头中的所有信息都封装在这个对象中，通过这个对象提供的方法，可以获得客户端请求的所有信息。
            HttpSession session = request.getSession();
            for (Object thisKey : data.keySet()) {
                String key = (String) thisKey;
                Object val = data.get(key);
                session.setAttribute(key, val);
            }
            //当客户端第一次访问服务器的时候，此时客户端的请求中不携带任何标识给服务器，所以此时服务器无法找到与之对应的
            //session，所以会新建session对象，当服务器进行响应的时候，服务器会将session标识放到响应头的Set-Cookie中，会以
            //key-value的形式返回给客户端，例：JSESSIONID=7F149950097E7B5B41B390436497CD21；其中JSESSIONID是固定的，
            //而后面的value值对应的则是给该客户端新创建的session的ID，之后浏览器再次进行服务器访问的时候，客户端会将此key-value
            //放到cookie中一并请求服务器，服务器就会根据此ID寻找对应的session对象了；
            //session的key值为“username”value值就是username真实的值，或者引用值.
            // 这样以后你可以通过session.getAttribute("username")的方法获得这个对象.
            // 比如说,当用户已登录系统后你就在session中存储了一个用户信息对象,
            // 此后你可以随时从session中将这个对象取出来进行一些操作,比如进行身份验证等等.
        }
    }

    public static boolean removeSession() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (servletRequestAttributes != null) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            HttpSession session = request.getSession(false);
            //request.getSession(default:true) 如果获取不到Session, 会给你创建一个Session给你
            //request.getSession(false) 如果获取不到Session,则不会给你创建一个Session给你
            if (session != null) {
                session.invalidate();
                //下次要再使用Session，得再重新创建
                return true;   //确实移除了Session
            }
        }
        return false;
    }

    public static Boolean checkAuthority() {
        //检查Session中绑定的userIdentity是否合法
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            HttpSession session = request.getSession(false);
            if (session != null) {
                Integer userIdentity = (Integer) session.getAttribute("userIdentity");
                return userIdentity != null && userIdentity >= 0;
            }
            LogUtil.print("session为空");
        }
        else {
            LogUtil.print("requestAttributes为空");
        }
        return false;
    }

    public static Boolean checkToken() {
        //检查Session中绑定的userIdentity是否合法
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            String jwt_token = request.getHeader("Authorization");
            if (jwt_token == null) {
                LogUtil.print("jwt_token为空");
                return false;
            }

            LogUtil.print("session为空");
        }
        else {
            LogUtil.print("requestAttributes为空");
        }
        return false;
    }

    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            return request.getSession(false);
        }
        return null;
    }

//    public static Integer getUserId() {
//        HttpSession session = getSession();
//        if (session != null) {
//            return (Integer) session.getAttribute("userId");
//        }
//        return -1;
//    }

    public static Integer getUserIdentity() {
        HttpSession session = getSession();
        if (session != null) {
            return (Integer) session.getAttribute("userIdentity");
        }
        return -1;
    }

//    public static String getUsername() {
//        HttpSession session = getSession();
//        if (session != null) {
//            return (String) session.getAttribute("username");
//        }
//        return "no HttpSession";
//    }

    public static String getVCode() {
        HttpSession session = getSession();
        if (session != null) {
            return (String) session.getAttribute("vcodeTime");
        }
        return "no HttpSession";
    }

//    public static JSONObject getAuthority() {
//        //获取此session中包含的授权信息
//        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//        if (requestAttributes != null) {
//            HttpServletRequest request = requestAttributes.getRequest();
//            HttpSession session = request.getSession(false);
//            if (session != null) {
//                JSONObject authorityObject = new JSONObject();
//                authorityObject.put("userId", session.getAttribute("userId"));
//                authorityObject.put("username", session.getAttribute("username"));
//                authorityObject.put("userIdentity", session.getAttribute("userIdentity"));
//                return authorityObject;
//            }
//        }
//        return null;
//    }
}
