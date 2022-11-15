package com.kkb.common.mybatis.base.log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * 项目名称：kkb-srm-plugin-server
 * 类名称：LogUtils
 * 类描述：日志工具类
 * 创建人：YuanGL
 * 创建时间：2019年3月25日11:04:24
 * version 2.0
 */
public class LogUtils {
	
	public static void error(Logger logger, String message){
		logger.error( message);
	}
	
	public static void error(Logger logger, Exception ex){
		logger.error( estacktack2Str(ex));
	}
	
	public static String estacktack2Str(Exception ex){
		PrintStream ps = null;
		try {
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			ps = new PrintStream(bao);
			ex.printStackTrace(ps);
			return bao.toString("utf-8");
		} catch (UnsupportedEncodingException e) {
			return e.getMessage();
		} finally {
			if(ps!=null){
				ps.close();
			}
		}
	}
	
	public static void trace(Logger logger, String message){
		logger.trace( message);
	}
	
	public static void timeUsed(Logger logger, String point, long start){
		logger.info(point + " time used: "+(System.currentTimeMillis()-start));
	}
}
