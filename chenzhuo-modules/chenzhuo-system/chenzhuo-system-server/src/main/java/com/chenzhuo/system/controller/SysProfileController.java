package com.chenzhuo.system.controller;

import com.chenzhuo.common.core.constant.UserConstants;
import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.utils.StringUtils;
import com.chenzhuo.common.core.utils.file.FileTypeUtils;
import com.chenzhuo.common.core.utils.file.MimeTypeUtils;
import com.chenzhuo.common.core.web.controller.BaseController;
import com.chenzhuo.common.log.annotation.Log;
import com.chenzhuo.common.log.enums.BusinessType;
import com.chenzhuo.common.security.service.TokenService;
import com.chenzhuo.common.security.utils.SecurityUtils;
import com.chenzhuo.file.domain.SysFile;
import com.chenzhuo.file.remote.RemoteFileService;
import com.chenzhuo.system.domain.SysUser;
import com.chenzhuo.system.domain.model.LoginUser;
import com.chenzhuo.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 个人信息 业务处理
 * 
 * @author chenzhuo
 */
@RestController
@RequestMapping("/user/profile")
public class SysProfileController extends BaseController
{
    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private RemoteFileService remoteFileService;

    /**
     * 个人信息
     */
    @GetMapping
    public Result profile()
    {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        Result result = Result.success(user);
        HashMap<Object, Object> dataMap = new HashMap<>();
        dataMap.put("roleGroup", userService.selectUserRoleGroup(username));
        dataMap.put("postGroup", userService.selectUserPostGroup(username));
        result.setData(dataMap);
        return result;
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public Result updateProfile(@RequestBody SysUser user)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser sysUser = loginUser.getSysUser();
        user.setUserName(sysUser.getUserName());
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return Result.error("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        }
        else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return Result.error("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setUserId(sysUser.getUserId());
        user.setPassword(null);
        if (userService.updateUserProfile(user) > 0)
        {
            // 更新缓存用户信息
            loginUser.getSysUser().setNickName(user.getNickName());
            loginUser.getSysUser().setPhonenumber(user.getPhonenumber());
            loginUser.getSysUser().setEmail(user.getEmail());
            loginUser.getSysUser().setSex(user.getSex());
            tokenService.setLoginUser(loginUser);
            return Result.success();
        }
        return Result.error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PutMapping("/updatePwd")
    public Result updatePwd(String oldPassword, String newPassword)
    {
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password))
        {
            return Result.error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password))
        {
            return Result.error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(username, SecurityUtils.encryptPassword(newPassword)) > 0)
        {
            // 更新缓存用户密码
            LoginUser loginUser = SecurityUtils.getLoginUser();
            loginUser.getSysUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return Result.success();
        }
        return Result.error("修改密码异常，请联系管理员");
    }
    
    /**
     * 头像上传
     */
    @Log(title = "用户头像", businessType = BusinessType.UPDATE)
    @PostMapping("/avatar")
    public Result avatar(@RequestParam("avatarfile") MultipartFile file)
    {
        if (!file.isEmpty())
        {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String extension = FileTypeUtils.getExtension(file);
            if (!StringUtils.equalsAnyIgnoreCase(extension, MimeTypeUtils.IMAGE_EXTENSION))
            {
                return Result.error("文件格式不正确，请上传" + Arrays.toString(MimeTypeUtils.IMAGE_EXTENSION) + "格式");
            }
            Result<SysFile> fileResult = remoteFileService.upload(file);
            if (StringUtils.isNull(fileResult) || StringUtils.isNull(fileResult.getData()))
            {
                return Result.error("文件服务异常，请联系管理员");
            }
            String url = fileResult.getData().getUrl();
            if (userService.updateUserAvatar(loginUser.getUsername(), url))
            {
                Result result = Result.success();
                HashMap<Object, Object> dataMap = new HashMap<>();
                dataMap.put("imgUrl", url);
                // 更新缓存用户头像
                loginUser.getSysUser().setAvatar(url);
                tokenService.setLoginUser(loginUser);
                result.setData(dataMap);
                return result;
            }
        }
        return Result.error("上传图片异常，请联系管理员");
    }
}
