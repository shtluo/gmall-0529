package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.constant.CookieConstant;
import com.atguigu.gmall.passport.utils.JwtUtils;
import com.atguigu.gmall.user.UserInfo;
import com.atguigu.gmall.user.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Controller
public class LoginController {
    @Reference
    UserInfoService userInfoService;

    /**
     * 去登陆页面
     * @return
     */

    @RequestMapping("/login")
    public String login(UserInfo userInfo, String originUrl,
                        @CookieValue(name = CookieConstant.SSO_COOKIE_NAME,
                                required = false) String token,
                        HttpServletResponse response) {


        //如果cookie没有东西，userInfo也没有东西，这个人直接访问登录页
        if(StringUtils.isEmpty(token) && userInfo.getLoginName() == null){
            return "index";
        }


        //1、登录过了
        if(!StringUtils.isEmpty(token)){
            //已经登录过了就重定向到那个人请求页面
            return "redirect:"+originUrl+"?token="+token;
        }else{
            //2、没有登录了
            if(StringUtils.isEmpty(userInfo.getLoginName())){
                return "index";
            }else{
                //用户填写了用户信息
                UserInfo login = userInfoService.login(userInfo);
                Map<String,Object> body = new HashMap<>();
                body.put("id",login.getId());
                body.put("loginName",login.getLoginName());
                body.put("nickName",login.getNickName());
                body.put("headImg",login.getHeadImg());
                body.put("email",login.getEmail());
                String newToken = JwtUtils.createJwtToken(body);
                if(login != null){
                    //登录成功
                    //在本地域中设置cookie
                    Cookie cookie = new Cookie(CookieConstant.SSO_COOKIE_NAME,newToken);
                    cookie.setPath("/");
                    //保存cookie到客户端
                    response.addCookie(cookie);
                    //登录成了将你的所有信息放到redis中，
                    //reids.set(newToken,loginJson);
                    if(!StringUtils.isEmpty(originUrl)){
                        return "redirect:"+originUrl+"?token="+newToken;
                    }else{
                        //登录成了到首页
                        return "redirect:http://www.gmall.com";
                    }

                }else{
                    //登录失败继续登录
                    return "index";
                }
            }
        }

    }

    /**
     * 确认token是否合法
     * @param token
     * @return
     */
    @ResponseBody
    @RequestMapping("confirmToken")
    public String confirmToken(String token){
        boolean b = JwtUtils.confirmJwtToken(token);
        return b?"ok":"error";
    }

}




/*if(userInfo.getLoginName() == null){
            //没有用户名，密码 ，只是来到登录页
            return "index";
        }
        UserInfo login = userInfoService.login(userInfo);
        if(login != null){
            //登录成功
            log.info("登录成功：{}",userInfo.getLoginName());
            //制证
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            //用redis存起来，key就是这个证的一长串值，value就是登陆上来的用户信息的json串
            //交给那个人

            //重定向到之前的页面
            return "redirect:"+originUrl+"?token="+token;
        }else{
            //登录失败
            return "index";
        }
        //2、以后进来了，有了gmallsso的cookie
        //检查cookie合法性和时效性，这个token有没有，没有就是超时了

    }*/