package webapi.authController;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import auth.discard.oss.entity.OSSFile;
import auth.discard.oss.service.IOSSFileService;
import commons.api.vo.Result;
import commons.util.CommonUtils;
import commons.util.MinioUtil;
import commons.util.oConvertUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * minio文件上传示例
 */
@Slf4j
@RestController
@RequestMapping("/sys/upload")
public class SysUploadController {

    private final IOSSFileService ossFileService;


    @Autowired
    public SysUploadController(IOSSFileService ossFileService) {
        this.ossFileService = ossFileService;
    }

    /**
     * 上传
     */
    @PostMapping(value = "/uploadMinio")
    public Result<?> uploadMinim(HttpServletRequest request) {
        Result<?> result = new Result<>();
        String bizPath = request.getParameter("biz");
        if (oConvertUtils.isEmpty(bizPath)) {
            bizPath = "";
        }
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("file");// 获取上传文件对象
        String orgName = file.getOriginalFilename();// 获取文件名
        orgName = CommonUtils.getFileName(orgName);
        String file_url = MinioUtil.upload(file, bizPath);
        //保存文件信息
        OSSFile minimFile = new OSSFile();
        minimFile.setFileName(orgName);
        minimFile.setUrl(file_url);
        ossFileService.save(minimFile);
        result.setMessage(file_url);
        result.setSuccess(true);
        return result;
    }
}
