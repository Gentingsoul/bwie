package com.chenzhuo.system.controller;

import com.chenzhuo.common.core.constant.UserConstants;
import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.poi.ExcelUtil;
import com.chenzhuo.common.core.web.controller.BaseController;
import com.chenzhuo.common.core.web.page.TableDataInfo;
import com.chenzhuo.common.log.annotation.Log;
import com.chenzhuo.common.log.enums.BusinessType;
import com.chenzhuo.common.security.annotation.RequiresPermissions;
import com.chenzhuo.common.security.utils.SecurityUtils;
import com.chenzhuo.system.domain.SysPost;
import com.chenzhuo.system.service.ISysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 岗位信息操作处理
 * 
 * @author chenzhuo
 */
@RestController
@RequestMapping("/post")
public class SysPostController extends BaseController
{
    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @RequiresPermissions("system:post:list")
    @GetMapping("/list")
   public Result<TableDataInfo> list(SysPost post)
    {
        startPage();
        List<SysPost> list = postService.selectPostList(post);
        return getDataTable(list);
    }

    @Log(title = "岗位管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:post:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysPost post)
    {
        List<SysPost> list = postService.selectPostList(post);
        ExcelUtil<SysPost> util = new ExcelUtil<SysPost>(SysPost.class);
        util.exportExcel(response, list, "岗位数据");
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @RequiresPermissions("system:post:query")
    @GetMapping(value = "/{postId}")
    public Result getInfo(@PathVariable Long postId)
    {
        return Result.success(postService.selectPostById(postId));
    }

    /**
     * 新增岗位
     */
    @RequiresPermissions("system:post:add")
    @Log(title = "岗位管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return Result.error("新增岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return Result.error("新增岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setCreateBy(SecurityUtils.getUsername());
        return toAjax(postService.insertPost(post));
    }

    /**
     * 修改岗位
     */
    @RequiresPermissions("system:post:edit")
    @Log(title = "岗位管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysPost post)
    {
        if (UserConstants.NOT_UNIQUE.equals(postService.checkPostNameUnique(post)))
        {
            return Result.error("修改岗位'" + post.getPostName() + "'失败，岗位名称已存在");
        }
        else if (UserConstants.NOT_UNIQUE.equals(postService.checkPostCodeUnique(post)))
        {
            return Result.error("修改岗位'" + post.getPostName() + "'失败，岗位编码已存在");
        }
        post.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(postService.updatePost(post));
    }

    /**
     * 删除岗位
     */
    @RequiresPermissions("system:post:remove")
    @Log(title = "岗位管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{postIds}")
    public Result remove(@PathVariable Long[] postIds)
    {
        return toAjax(postService.deletePostByIds(postIds));
    }

    /**
     * 获取岗位选择框列表
     */
    @GetMapping("/optionselect")
    public Result optionselect()
    {
        List<SysPost> posts = postService.selectPostAll();
        return Result.success(posts);
    }
}
