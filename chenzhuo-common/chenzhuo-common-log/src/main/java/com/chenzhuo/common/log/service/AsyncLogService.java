package com.chenzhuo.common.log.service;

import com.chenzhuo.system.domain.SysOperLog;
import com.chenzhuo.system.remote.RemoteLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.chenzhuo.common.core.constant.SecurityConstants;


/**
 * 异步调用日志服务
 * 
 * @author chenzhuo
 */
@Service
public class AsyncLogService
{
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog)
    {
        remoteLogService.saveLog(sysOperLog, SecurityConstants.INNER);
    }
}
