package com.atguigu.gmall.manager.controller;

import com.atguigu.gmall.manager.components.FastDFSTemplete;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.StorageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/file")
public class FileController {

    @Autowired
    FastDFSTemplete fastDFSTemplete;
    //文件上传的要点：上传成功返回文件的URL访问地址
    @RequestMapping("/upload")
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        if(!file.isEmpty()){
            String originalFilename = file.getOriginalFilename();

            long size = file.getSize();
            String fileName = file.getName();
            log.info("文件项{}；文件名{}；文件大小{}；",originalFilename,fileName,size);
            StorageClient storageClient = fastDFSTemplete.getStorageClient();
            String ext = StringUtils.substringAfterLast(originalFilename,".");
            try {
                //返回真正的访问路径
                String[] strings = storageClient.upload_file(file.getBytes(), ext, null);
                String path = fastDFSTemplete.getPath(strings);
                return path;
            } catch (MyException e) {
                log.error("文件上传出错{}:",e);
            }
        }
        return null;
    }
}
