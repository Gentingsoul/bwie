package com.chenzhuo.file.remote;

import com.chenzhuo.file.domain.SysFile;
import com.chenzhuo.file.remote.factory.RemoteFileFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import com.chenzhuo.common.core.constant.ServiceNameConstants;
import com.chenzhuo.common.core.domain.Result;

/**
 * 文件服务
 * 
 * @author chenzhuo
 */
@FeignClient(contextId = "remoteFileService", value = ServiceNameConstants.FILE_SERVICE, fallbackFactory = RemoteFileFallbackFactory.class)
public interface RemoteFileService
{
    /**
     * 上传文件
     *
     * @param file 文件信息
     * @return 结果
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<SysFile> upload(@RequestPart(value = "file") MultipartFile file);
}
