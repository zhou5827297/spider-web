package com.spider.util;

import java.util.regex.Pattern;

/**
 * 正则
 * @author meitao
 * @date 2016年8月31日 下午3:27:17
 */
public interface Regs {
	Pattern COMMENT_BEGIN = Pattern.compile("<!--");
	Pattern COMMENT_END = Pattern.compile("-->");
	Pattern TAG = Pattern.compile("<(/?)(\\w+)([^>]*)(/?)>");
	Pattern A = Pattern.compile("<a [^>]*href=['\"]?([^'\">\\s]+)[^>]*>", Pattern.CASE_INSENSITIVE);
	Pattern SRC = Pattern.compile(" src=(\"[^\"]+\"|'[^']+')", Pattern.CASE_INSENSITIVE);
	Pattern SITE = Pattern.compile("^\\w+://[^/]+/?", Pattern.CASE_INSENSITIVE);
}
