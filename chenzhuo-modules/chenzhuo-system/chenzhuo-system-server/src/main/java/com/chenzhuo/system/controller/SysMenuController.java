package com.chenzhuo.system.controller;

import com.chenzhuo.common.core.constant.UserConstants;
import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.StringUtils;
import com.chenzhuo.common.core.web.controller.BaseController;
import com.chenzhuo.common.log.annotation.Log;
import com.chenzhuo.common.log.enums.BusinessType;
import com.chenzhuo.common.security.annotation.RequiresPermissions;
import com.chenzhuo.common.security.utils.SecurityUtils;
import com.chenzhuo.system.domain.SysMenu;
import com.chenzhuo.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

/**
 * 菜单信息
 * 
 * @author chenzhuo
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    public Result list(SysMenu menu)
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return Result.success(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public Result getInfo(@PathVariable Long menuId)
    {
        return Result.success(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
    public Result treeselect(SysMenu menu)
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return Result.success(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public Result roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        Result result = Result.success();
        HashMap<Object, Object> dataMap = new HashMap<>();
        dataMap.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        dataMap.put("menus", menuService.buildMenuTreeSelect(menus));
        result.setData(dataMap);
        return result;
    }

    /**
     * 新增菜单
     */
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public Result add(@Validated @RequestBody SysMenu menu)
    {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return Result.error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return Result.error("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        menu.setCreateBy(SecurityUtils.getUsername());
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result edit(@Validated @RequestBody SysMenu menu)
    {
        if (UserConstants.NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return Result.error("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }
        menu.setUpdateBy(SecurityUtils.getUsername());
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public Result remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return Result.error("存在子菜单,不允许删除");
        }
        if (menuService.checkMenuExistRole(menuId))
        {
            return Result.error("菜单已分配,不允许删除");
        }
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public Result getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.success(menuService.buildMenus(menus));
    }
}