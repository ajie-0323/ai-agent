package com.donggua.aiagent.controller;

import com.donggua.aiagent.common.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: ajie
 * Date: 2026-04-29 17:56
 * Description: 健康检测接口
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping
    public ResponseResult<String> healthCheck() {
        return ResponseResult.success("ok");
    }
}
