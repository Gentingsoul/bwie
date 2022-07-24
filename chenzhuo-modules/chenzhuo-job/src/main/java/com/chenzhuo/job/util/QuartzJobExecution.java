package com.chenzhuo.job.util;

import org.quartz.JobExecutionContext;

import com.chenzhuo.job.domain.SysJob;

/**
 * 定时任务处理（允许并发执行）
 * 
 * @author chenzhuo
 *
 */
public class QuartzJobExecution extends AbstractQuartzJob
{
    @Override
    protected void doExecute(JobExecutionContext context, SysJob sysJob) throws Exception
    {
        JobInvokeUtil.invokeMethod(sysJob);
    }
}
