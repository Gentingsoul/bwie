package com.chenzhuo.system.remote.factory;

import com.chenzhuo.system.domain.SysLogininfor;
import com.chenzhuo.system.domain.SysOperLog;
import com.chenzhuo.system.remote.RemoteLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import com.chenzhuo.common.core.domain.Result;


/**
 * 日志服务降级处理
 * 
 * @author chenzhuo
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable)
    {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService()
        {
            @Override
            public Result<Boolean> saveLog(SysOperLog sysOperLog, String source)
            {
                return null;
            }

            @Override
            public Result<Boolean> saveLogininfor(SysLogininfor sysLogininfor, String source)
            {
                return null;
            }
        };

    }
}
