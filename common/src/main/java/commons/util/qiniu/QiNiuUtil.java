package commons.util.qiniu;

import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 七牛文件上传工具类
 * @author lhy
 */
@Slf4j
@Data
public class QiNiuUtil {
    private static String accessKey = "";
    private static String secretKey = "";
    private static String bucket = "";
    private static String url = "";

    /**
     * 上传文件
     *
     * @param multipartFile
     * @return
     */
    public static String upload(MultipartFile multipartFile, String bizPath, String customBucket) {
        File file = null;
        // 获取文件名
        String orgName = multipartFile.getOriginalFilename();
        if ("".equals(orgName)) {
            orgName = multipartFile.getName();
        }
        // 密钥配置
        Auth auth = Auth.create(accessKey, secretKey);
        //创建上传对象
        Configuration configuration = new Configuration(Zone.zone0());
        UploadManager uploadManager = new UploadManager(configuration);
        String uuid = UUID.randomUUID().toString();
        try {
            file = File.createTempFile(bizPath, null);
            //通过MultipartFile的transferTo(File dest)这个方法来转存文件到指定的路径。MultipartFile转存后，无法再操作，会报错
            multipartFile.transferTo(file);
            //调用put方法上传
            Response res = uploadManager.put(file, uuid, auth.uploadToken(bucket));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert file != null;
        file.deleteOnExit();
        return url + uuid+"?attname="+orgName;
    }

    /**
     * 文件上传
     *
     * @param file
     * @param bizPath
     * @return
     */
    public static String upload(MultipartFile file, String bizPath) {
        return upload(file, bizPath, null);
    }
}
