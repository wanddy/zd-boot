package commons.util;

import commons.constant.CommonConstant;
import commons.util.oss.OssBootUtil;
import org.springframework.util.FileCopyUtils;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class CommonUtils {

    /**
     * 判断文件名是否带盘符，重新处理
     * @param fileName
     * @return
     */
    public static String getFileName(String fileName){
        //判断是否带有盘符信息
        // Check for Unix-style path
        int unixSep = fileName.lastIndexOf('/');
        // Check for Windows-style path
        int winSep = fileName.lastIndexOf('\\');
        // Cut off at latest possible point
        int pos = (winSep > unixSep ? winSep : unixSep);
        if (pos != -1)  {
            // Any sort of path separator found...
            fileName = fileName.substring(pos + 1);
        }
        //替换上传文件名字的特殊字符
        fileName = fileName.replace("=","").replace(",","").replace("&","").replace("#", "");
        return fileName;
    }
}
