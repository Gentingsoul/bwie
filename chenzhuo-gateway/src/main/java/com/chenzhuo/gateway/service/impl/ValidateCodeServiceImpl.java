package com.chenzhuo.gateway.service.impl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.chenzhuo.common.core.domain.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;
import com.google.code.kaptcha.Producer;
import com.chenzhuo.common.core.constant.Constants;
import com.chenzhuo.common.core.exception.CaptchaException;
import com.chenzhuo.common.core.utils.StringUtils;
import com.chenzhuo.common.core.utils.sign.Base64;
import com.chenzhuo.common.core.utils.uuid.IdUtils;
import com.chenzhuo.common.redis.service.RedisService;
import com.chenzhuo.gateway.config.properties.CaptchaProperties;
import com.chenzhuo.gateway.service.ValidateCodeService;

/**
 * 验证码实现处理
 *
 * @author chenzhuo
 */
@Service
public class ValidateCodeServiceImpl implements ValidateCodeService
{
    @Resource(name = "captchaProducer")
    private Producer captchaProducer;

    @Resource(name = "captchaProducerMath")
    private Producer captchaProducerMath;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CaptchaProperties captchaProperties;

    /**
     * 生成验证码
     */
    @Override
    public Result createCaptcha() throws IOException, CaptchaException
    {
        Result result = Result.success();
        boolean captchaEnabled = captchaProperties.getEnabled();
        HashMap<String, Object> ajaxMap = new HashMap<>();
        ajaxMap.put("captchaEnabled", captchaEnabled);
        if (!captchaEnabled)
        {
            result.setData(ajaxMap);
            return result;
        }

        // 保存验证码信息
        String uuid = IdUtils.simpleUUID();
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;

        String capStr = null, code = null;
        BufferedImage image = null;

        String captchaType = captchaProperties.getType();
        // 生成验证码
        if ("math".equals(captchaType))
        {
            String capText = captchaProducerMath.createText();
            capStr = capText.substring(0, capText.lastIndexOf("@"));
            code = capText.substring(capText.lastIndexOf("@") + 1);
            image = captchaProducerMath.createImage(capStr);
        }
        else if ("char".equals(captchaType))
        {
            capStr = code = captchaProducer.createText();
            image = captchaProducer.createImage(capStr);
        }

        redisService.setCacheObject(verifyKey, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);
        // 转换流信息写出
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", os);
        }
        catch (IOException e)
        {
            return Result.error(e.getMessage());
        }

        ajaxMap.put("uuid", uuid);
        ajaxMap.put("img", Base64.encode(os.toByteArray()));
        result.setData(ajaxMap);

        return result;
    }

    /**
     * 校验验证码
     */
    @Override
    public void checkCaptcha(String code, String uuid) throws CaptchaException
    {
        if (StringUtils.isEmpty(code))
        {
            throw new CaptchaException("验证码不能为空");
        }
        if (StringUtils.isEmpty(uuid))
        {
            throw new CaptchaException("验证码已失效");
        }
        String verifyKey = Constants.CAPTCHA_CODE_KEY + uuid;
        String captcha = redisService.getCacheObject(verifyKey);
        redisService.deleteObject(verifyKey);

        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException("验证码错误");
        }
    }
}
