package com.chenzhuo.system.remote;

import com.chenzhuo.system.domain.SysLogininfor;
import com.chenzhuo.system.domain.SysOperLog;
import com.chenzhuo.system.remote.factory.RemoteLogFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import com.chenzhuo.common.core.constant.SecurityConstants;
import com.chenzhuo.common.core.constant.ServiceNameConstants;
import com.chenzhuo.common.core.domain.Result;


/**
 * 日志服务
 * 
 * @author chenzhuo
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService
{
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/operlog")
    public Result<Boolean> saveLog(@RequestBody SysOperLog sysOperLog, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);

    /**
     * 保存访问记录
     *
     * @param sysLogininfor 访问实体
     * @param source 请求来源
     * @return 结果
     */
    @PostMapping("/logininfor")
    public Result<Boolean> saveLogininfor(@RequestBody SysLogininfor sysLogininfor, @RequestHeader(SecurityConstants.FROM_SOURCE) String source);
}
