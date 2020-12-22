package commons.poi.exception.word;

import commons.poi.exception.word.enums.WordExportEnum;

/**
 * word导出异常
 *
 * @author JEECG
 * @date 2014年8月9日 下午10:32:51
 */
public class WordExportException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public WordExportException() {
		super();
	}

	public WordExportException(String msg) {
		super(msg);
	}

	public WordExportException(WordExportEnum exception) {
		super(exception.getMsg());
	}

}
