package com.chenzhuo.common.core.web.controller;

import java.beans.PropertyEditorSupport;
import java.util.Date;
import java.util.List;

import com.chenzhuo.common.core.domain.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import com.github.pagehelper.PageInfo;
import com.chenzhuo.common.core.utils.DateUtils;
import com.chenzhuo.common.core.utils.PageUtils;
import com.chenzhuo.common.core.web.page.TableDataInfo;

/**
 * web层通用数据处理
 * 
 * @author chenzhuo
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageUtils.startPage();
    }

    /**
     * 清理分页的线程变量
     */
    protected void clearPage()
    {
        PageUtils.clearPage();
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Result<TableDataInfo> getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setRows(list);
        rspData.setTotal(new PageInfo(list).getTotal());
        return Result.success(rspData,"查询成功");
    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected Result toAjax(int rows)
    {
        return rows > 0 ? Result.success() : Result.error();
    }

    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected Result toAjax(boolean result)
    {

        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public Result success()
    {
        return Result.success();
    }

    /**
     * 返回失败消息
     */
    public Result error()
    {
        return Result.error();
    }

    /**
     * 返回成功消息
     */
    public Result success(String message)
    {
        return Result.success(message);
    }

    /**
     * 返回失败消息
     */
    public Result error(String message)
    {
        return Result.error(message);
    }
}
