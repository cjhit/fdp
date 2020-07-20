package com.github.cjhit.fdp.config;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Path;

/**
 * 文件名：VideoResourceHttpRequestHandler.java
 * 说明：视频资源（用于视频播放）
 * 作者：水哥
 * 创建时间：2020-06-30
 */
@Component
public class VideoResourceHttpRequestHandler extends ResourceHttpRequestHandler {
    public final static String ATTR_FILE = "NON-STATIC-FILE";

    @Override
    protected Resource getResource(HttpServletRequest request) {
        final Path filePath = (Path) request.getAttribute(ATTR_FILE);
        return new FileSystemResource(filePath);
    }
}
