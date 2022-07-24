package com.chenzhuo.system.controller;

import com.chenzhuo.common.core.constant.CacheConstants;
import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.StringUtils;
import com.chenzhuo.common.core.web.controller.BaseController;
import com.chenzhuo.common.core.web.page.TableDataInfo;
import com.chenzhuo.common.log.annotation.Log;
import com.chenzhuo.common.log.enums.BusinessType;
import com.chenzhuo.common.redis.service.RedisService;
import com.chenzhuo.common.security.annotation.RequiresPermissions;
import com.chenzhuo.system.domain.SysUserOnline;
import com.chenzhuo.system.domain.model.LoginUser;
import com.chenzhuo.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 * 
 * @author chenzhuo
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController extends BaseController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisService redisService;

    @RequiresPermissions("monitor:online:list")
    @GetMapping("/list")
   public Result<TableDataInfo> list(String ipaddr, String userName)
    {
        Collection<String> keys = redisService.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = redisService.getCacheObject(key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()) && StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
                }
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                if (StringUtils.equals(ipaddr, user.getIpaddr()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
                }
            }
            else if (StringUtils.isNotEmpty(userName))
            {
                if (StringUtils.equals(userName, user.getUsername()))
                {
                    userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
                }
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return getDataTable(userOnlineList);
    }

    /**
     * 强退用户
     */
    @RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public Result forceLogout(@PathVariable String tokenId)
    {
        redisService.deleteObject(CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return Result.success();
    }
}
