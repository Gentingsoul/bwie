package com.chenzhuo.auth.controller;

import javax.servlet.http.HttpServletRequest;

import com.chenzhuo.system.domain.model.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.chenzhuo.auth.form.LoginBody;
import com.chenzhuo.auth.form.RegisterBody;
import com.chenzhuo.auth.service.SysLoginService;
import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.JwtUtils;
import com.chenzhuo.common.core.utils.StringUtils;
import com.chenzhuo.common.security.auth.AuthUtil;
import com.chenzhuo.common.security.service.TokenService;
import com.chenzhuo.common.security.utils.SecurityUtils;


/**
 * token 控制
 * 
 * @author chenzhuo
 */
@RestController
public class TokenController
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysLoginService sysLoginService;

    @PostMapping("login")
    public Result<?> login(@RequestBody LoginBody form)
    {
        // 用户登录
        LoginUser userInfo = sysLoginService.login(form.getUsername(), form.getPassword());
        // 获取登录token
        return Result.success(tokenService.createToken(userInfo));
    }

    @DeleteMapping("logout")
    public Result<?> logout(HttpServletRequest request)
    {
        String token = SecurityUtils.getToken(request);
        if (StringUtils.isNotEmpty(token))
        {
            String username = JwtUtils.getUserName(token);
            // 删除用户缓存记录
            AuthUtil.logoutByToken(token);
            // 记录用户退出日志
            sysLoginService.logout(username);
        }
        return Result.success();
    }

    @PostMapping("refresh")
    public Result<?> refresh(HttpServletRequest request)
    {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser))
        {
            // 刷新令牌有效期
            tokenService.refreshToken(loginUser);
            return Result.success();
        }
        return Result.success();
    }

    @PostMapping("register")
    public Result<?> register(@RequestBody RegisterBody registerBody)
    {
        // 用户注册
        sysLoginService.register(registerBody.getUsername(), registerBody.getPassword());
        return Result.success();
    }
}
