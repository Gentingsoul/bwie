package com.chenzhuo.system.controller;

import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.poi.ExcelUtil;
import com.chenzhuo.common.core.web.controller.BaseController;
import com.chenzhuo.common.core.web.page.TableDataInfo;
import com.chenzhuo.common.log.annotation.Log;
import com.chenzhuo.common.log.enums.BusinessType;
import com.chenzhuo.common.security.annotation.InnerAuth;
import com.chenzhuo.common.security.annotation.RequiresPermissions;
import com.chenzhuo.system.domain.SysOperLog;
import com.chenzhuo.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 操作日志记录
 * 
 * @author chenzhuo
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController extends BaseController
{
    @Autowired
    private ISysOperLogService operLogService;

    @RequiresPermissions("system:operlog:list")
    @GetMapping("/list")
   public Result<TableDataInfo> list(SysOperLog operLog)
    {
        startPage();
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        return getDataTable(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:operlog:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectOperLogList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:operlog:remove")
    @DeleteMapping("/{operIds}")
    public Result remove(@PathVariable Long[] operIds)
    {
        return toAjax(operLogService.deleteOperLogByIds(operIds));
    }

    @RequiresPermissions("system:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public Result clean()
    {
        operLogService.cleanOperLog();
        return Result.success();
    }

    @InnerAuth
    @PostMapping
    public Result add(@RequestBody SysOperLog operLog)
    {
        return toAjax(operLogService.insertOperlog(operLog));
    }
}
