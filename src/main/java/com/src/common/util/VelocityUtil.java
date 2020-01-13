package com.src.common.util;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.util.Properties;

/**
 * Velocity工具类
 * Created by src on 2018/6/25.
 */
public class VelocityUtil {



	/**
	 * 根据模板生成文件  (读取Jar)
	 * @param inputVmFilePath 模板路径
	 * @param outputFilePath 输出文件路径
	 * @param context
	 * @throws Exception
	 */
	public static void generateJar(String inputVmFilePath, String outputFilePath, VelocityContext context) throws Exception {
		try {
			//不同方式读取 http://www.mekau.com/1282.html
			Properties properties = new Properties();
			//可选值："class"--从classpath中读取，"file"--从文件系统中读取 "jar" 从jar中读取
			properties.setProperty(VelocityEngine.RESOURCE_LOADER, "jar");
			//如果从文件系统中读取模板，那么属性值为org.apache.velocity.runtime.resource.loader.FileResourceLoader
			properties.setProperty("jar.resource.loader.class", "org.apache.velocity.runtime.resource.loader.JarResourceLoader");
			properties.setProperty("jar.resource.loader.path", "jar:"+getJarPath(inputVmFilePath));
			Velocity.init(properties);
			//VelocityEngine engine = new VelocityEngine();
			Template template = Velocity.getTemplate(getJarFile(inputVmFilePath), "utf-8");
			//如果目录不存在 就先创建
			File dir = new File(getPath(outputFilePath));
			if(!dir.exists()){
				dir.mkdirs();
			}
			File outputFile = new File(outputFilePath);
			FileWriterWithEncoding writer = new FileWriterWithEncoding(outputFile, "utf-8");
			template.merge(context, writer);
			writer.close();
		} catch (Exception ex) {
			throw ex;
		}
	}





	/**
	 * 根据模板生成文件 （读取文件）
	 * @param inputVmFilePath 模板路径
	 * @param outputFilePath 输出文件路径
	 * @param context
	 * @throws Exception
	 */
	public static void generate(String inputVmFilePath, String outputFilePath, VelocityContext context) throws Exception {
		try {
			Properties properties = new Properties();
			properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, getPath(inputVmFilePath));
			Velocity.init(properties);
			//VelocityEngine engine = new VelocityEngine();
			Template template = Velocity.getTemplate(getFile(inputVmFilePath), "utf-8");
			File outputFile = new File(outputFilePath);
			FileWriterWithEncoding writer = new FileWriterWithEncoding(outputFile, "utf-8");
			template.merge(context, writer);
			writer.close();
		} catch (Exception ex) {
			throw ex;
		}
	}

	/**
	 * 根据文件绝对路径获取目录
	 * @param filePath
	 * @return
	 */
	public static String getPath(String filePath) {
		String path = "";
		if (StringUtils.isNotBlank(filePath)) {
			path = filePath.substring(0, filePath.lastIndexOf("/") + 1);
		}
		return path;
	}

	/**
	 * 根据文件绝对路径获取目录
	 * @param filePath
	 * @return
	 */
	public static String getJarPath(String filePath) {
		String path = "";
		if (StringUtils.isNotBlank(filePath)) {
			path = filePath.substring(0, filePath.lastIndexOf("!"));
		}
		return path;
	}

	/**
	 * 根据文件绝对路径获取文件
	 * @param filePath
	 * @return
	 */
	public static String getFile(String filePath) {
		String file = "";
		if (StringUtils.isNotBlank(filePath)) {
			file = filePath.substring(filePath.lastIndexOf("/") + 1);
		}
		return file;
	}


	/**
	 * 根据文件绝对路径获取文件
	 * @param filePath
	 * @return
	 */
	public static String getJarFile(String filePath) {
		String file = "";
		if (StringUtils.isNotBlank(filePath)) {
			file = filePath.substring(filePath.lastIndexOf("!")+1);
		}
		return file;
	}

}
