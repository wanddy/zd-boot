package workflow.common.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 * @功能描述：扫描指定包路径下的类文件
 * @创建时间：2013-5-6
 * @作者： 陈伟
 *
 */
public class ClassPath {
	/**
	 * 类结束名
	 */
	private String classNameNnd = null;

	/**
	 * 构造函数
	 */
	public ClassPath() {
	}

	/**
	 * 构造函数
	 * 
	 * @param classNameNnd
	 *            类结束名
	 */
	public ClassPath(String classNameNnd) {
		this.classNameNnd = classNameNnd;
	}

	/**
	 * 扫描包
	 * 
	 * @param basePackage
	 *            基础包
	 * @param recursive
	 *            是否递归搜索子包
	 * @return Set
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<String> getPackageAllClasses(String basePackage) throws IOException, ClassNotFoundException {
		// 类列表
		List<String> classes = new ArrayList<String>();
		// 格式化类路径
		String packagePath = basePackage.replace('.', '/');
		// 获取类加载器
		Enumeration<URL> dirs = Thread.currentThread().getContextClassLoader().getResources(packagePath);
		
		while (dirs.hasMoreElements()) {
			URL url = dirs.nextElement();
			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
                String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                doScanPackageClassesByFile(classes, basePackage, filePath);
            } else if ("jar".equals(protocol)) {
				doScanPackageClassesByJar(packagePath, url, classes);
			}
		}

		return classes;
	}

	/**
	 * 以jar的方式扫描包下的所有Class文件<br>
	 * 
	 * @param basePackage
	 *            eg：michael.utils.
	 * @param url
	 * @param classes
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void doScanPackageClassesByJar(String basePackage, URL url, List<String> classes) throws IOException,
			ClassNotFoundException {
		JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName();
			// 校验包路径
			if (!name.startsWith(basePackage) || entry.isDirectory()) {
				continue;
			}
			
			if(!name.endsWith(".class")) {
				continue;
			}

			String className = name.replace('/', '.');
			className = className.substring(0, className.length() - 6);
			//过滤
			if (this.classNameNnd != null
					&& !className.endsWith(this.classNameNnd)) {
				continue;
			}

			classes.add(className);
		}
	}
	
	/**
     * 以文件的方式扫描包下的所有Class文件
     * 
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
	 * @throws ClassNotFoundException 
     */
    private void doScanPackageClassesByFile(List<String> classes,
            String packageName, String packagePath) throws ClassNotFoundException {
    	File file = new File(packagePath);
    	Set<String> fileNames = new LinkedHashSet<String>();
    	getFileName(fileNames, packageName, file);
    	
		for (String fileName : fileNames) {
			if(!fileName.endsWith(".class")) {
				continue;
			}
			
			String className = fileName.substring(0,fileName.length() - 6);
			//过滤
			if (this.classNameNnd != null
					&& !className.endsWith(this.classNameNnd)) {
				continue;
			}
			classes.add(className);
		}
    }
    
    /**
     * 获取目录文件名（递归方法）
     * @param fileNames 文件名列表
     * @param packagePath 目录
     * @param file 文件对象
     */
    private void getFileName(Set<String> fileNames, String packagePath, File file) {
    	if(file.isDirectory()) {
    		File[] files = file.listFiles();
    		for(File f:files) {
    			getFileName(fileNames, packagePath+"."+f.getName(), f);
    		}
    	} else {
    		fileNames.add(packagePath);
    	}
    }

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		ClassPath handler = new ClassPath("CmdHandler");
		List<String> calssList = handler.getPackageAllClasses(
				"com.king.superbees.cmdserver.command");

		for (String cla : calssList) {
			System.out.println(cla);
		}
	}
}
