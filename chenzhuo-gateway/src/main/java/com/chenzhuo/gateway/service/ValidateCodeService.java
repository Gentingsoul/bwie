package com.chenzhuo.gateway.service;

import java.io.IOException;

import com.chenzhuo.common.core.domain.Result;
import com.chenzhuo.common.core.exception.CaptchaException;

/**
 * 验证码处理
 *
 * @author chenzhuo
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public Result createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
