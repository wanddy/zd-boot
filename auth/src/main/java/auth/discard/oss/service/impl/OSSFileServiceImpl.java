package auth.discard.oss.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import commons.util.CommonUtils;
import commons.util.oss.OssBootUtil;
import auth.discard.oss.entity.OSSFile;
import auth.discard.oss.mapper.OSSFileMapper;
import auth.discard.oss.service.IOSSFileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service("ossFileService")
public class OSSFileServiceImpl extends ServiceImpl<OSSFileMapper, OSSFile> implements IOSSFileService {

	@Override
	public void upload(MultipartFile multipartFile) throws IOException {
		String fileName = multipartFile.getOriginalFilename();
		fileName = CommonUtils.getFileName(fileName);
		OSSFile ossFile = new OSSFile();
		ossFile.setFileName(fileName);
		String url = OssBootUtil.upload(multipartFile,"upload/test");
		ossFile.setUrl(url);
		this.save(ossFile);
	}

	@Override
	public boolean delete(OSSFile ossFile) {
		try {
			this.removeById(ossFile.getId());
			OssBootUtil.deleteUrl(ossFile.getUrl());
		}
		catch (Exception ex) {
			return false;
		}
		return true;
	}

}
