package com.chenzhuo.system.remote.factory;

import com.chenzhuo.system.domain.SysUser;
import com.chenzhuo.system.domain.model.LoginUser;
import com.chenzhuo.system.remote.RemoteUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.chenzhuo.common.core.domain.Result;


/**
 * 用户服务降级处理
 * 
 * @author chenzhuo
 */
@Component
public class RemoteUserFallbackFactory implements FallbackFactory<RemoteUserService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteUserFallbackFactory.class);

    @Override
    public RemoteUserService create(Throwable throwable)
    {
        log.error("用户服务调用失败:{}", throwable.getMessage());
        return new RemoteUserService()
        {
            @Override
            public Result<LoginUser> getUserInfo(String username, String source)
            {
                return Result.error("获取用户失败:" + throwable.getMessage());
            }

            @Override
            public Result<Boolean> registerUserInfo(SysUser sysUser, String source)
            {
                return Result.error("注册用户失败:" + throwable.getMessage());
            }
        };
    }
}
