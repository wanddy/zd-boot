package commons.poi.cache;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import commons.poi.cache.manager.POICacheManager;
import commons.poi.word.entity.MyXWPFDocument;

/**
 * word 缓存中心
 *
 * @author JEECG
 * @date 2014年7月24日 下午10:54:31
 */
public class WordCache {

	private static final Logger LOGGER = LoggerFactory.getLogger(WordCache.class);

	public static MyXWPFDocument getXWPFDocumen(String url) {
		InputStream is = null;
		try {
			is = POICacheManager.getFile(url);
			MyXWPFDocument doc = new MyXWPFDocument(is);
			return doc;
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				is.close();
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return null;
	}

}
