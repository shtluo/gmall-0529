package com.atguigu.gmall.interceptor;

import com.atguigu.gmall.annotation.LoginRequired;
import com.atguigu.gmall.constant.CookieConstant;
import com.atguigu.gmall.utils.CookieUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component  //放到容器中，配置config使其生效（WebMvcConfigurerAdapter）
public class LoginRequireInterceptor implements HandlerInterceptor {

    //目标方法执行之前
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object o) throws Exception {
        //1、先判断这个方法是否需要登陆后才能访问，拿到我们将要执行的目标方法
        HandlerMethod handlerMethod = (HandlerMethod) o;
        LoginRequired annotation = handlerMethod.getMethodAnnotation(LoginRequired.class); //拿到注解
        if(annotation!=null){
            //标了注解

            //1、验证是否是第一次过来只是带了一个参数位置的token字符串
            String token = request.getParameter("token");
            String cookieValue = CookieUtils.getCookieValue(request, CookieConstant.SSO_COOKIE_NAME);
            //获取是否需要一定登陆
            boolean needLogin = annotation.needLogin();


            if(!StringUtils.isEmpty(token)){
                //只要这个参数有 ，说明登陆成功了我们要设置这个cookie
                Cookie cookie = new Cookie(CookieConstant.SSO_COOKIE_NAME, token);
                cookie.setPath("/");
                //直接addCookie setDomain 默认是当前项目的全域名 item.gmall.com
                //cookie在二级域名相同可以共享
                //只要一个人登录成功，其他域的子系统，都不用登陆，只需要放大cookie的作用域
                //最大只能放大到二级域名
                cookie.setDomain("gmall.com");
                //保存到浏览器客户端
                response.addCookie(cookie);

                Map<String, Object> map = CookieUtils.resolveTokenData(token);
                //解好以后将用户信息放进请求域中，当次请求就能用了；
                request.setAttribute(CookieConstant.LOGIN_USER_INFO_KEY,map);
                return true;
            }

            //2、验证是否存在登陆的cookie
            if(!StringUtils.isEmpty(cookieValue)){
                //说明之前登陆过，cookie已经放好了
                //验证令牌;远程验证的
                //1、验证令牌对不对
                //发请求
                RestTemplate restTemplate = new RestTemplate();
                String confirmTokenUrl = "http://www.gmallsso.com/confirmToken?token="+cookieValue;
                //去远程验证
                try {
                    String result = restTemplate.getForObject(confirmTokenUrl, String.class);
                    System.out.println("远程验证的结果是："+result);
                    if(result.equals("ok")){ Map<String, Object> map = CookieUtils.resolveTokenData(cookieValue);
                        //解好以后将用户信息放进请求域中，当次请求就能用了；

                        request.setAttribute(CookieConstant.LOGIN_USER_INFO_KEY,map);
                        //验证通过放行方法

                        return true;
                    }else{
                        //验证失败，重新去登陆
                        if(needLogin == true){
                            String redirectUrl = "http://www.gmallsso.com/login?originUrl="+request.getRequestURL();
                            response.sendRedirect(redirectUrl);
                            return false;
                        }
                        return true;
                    }
                }catch (Exception e){
                    //远程服务器都连不上目标方法不执行
                    return false;
                }

            }

            //3、两个都没有
            if(StringUtils.isEmpty(token) && StringUtils.isEmpty(cookieValue)){
                if(needLogin == true){
                    String redirectUrl = "http://www.gmallsso.com/login?originUrl="+request.getRequestURL();
                    response.sendRedirect(redirectUrl);
                    return  false;
                }
                return true;
            }

        }else{
            //没标注解直接放行
            return true;
        }
        return false;
    }

    //目标方法执行以后
    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    //页面渲染出来以后
    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
