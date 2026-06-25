package com.donggua.aiagent.tools;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.donggua.aiagent.constant.FileConstant;
import lombok.ToString;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.File;

/**
 * Author: ajie
 * Date: 2026-06-18 15:20
 * Description: 资源下载工具
 */
public class ResourceDownloadTool {

    /**
     * 根据url路径和ai解析出来这个图片该叫什么名称来下载文件
     *
     * @param url
     * @param fileName
     * @return
     */
    @Tool(description = "Download a resource from a given URL")
    public String downloadResource(@ToolParam(description = "URL of the resource to download") String url,
                                   @ToolParam(description = "Name of the file to save the download resource") String fileName) {
        // 定义存储文件路径
        String fileDir = FileConstant.FILE_SAVE_DIR + "/dowmload";
        String filePath = fileDir + "/" + fileName;

        try {
            // 判断文件保存路径
            FileUtil.mkdir(fileDir);
            // 通过工具类下载文件
            HttpUtil.downloadFile(url, new File(filePath));
            // 下载成功返回
            return "Resource download successfully:" + filePath;
        } catch (Exception e) {
            return "Error download resource:" + e.getMessage();

        }


    }

}
