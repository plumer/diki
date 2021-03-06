/**
 * 
 */
package lab02;

import java.lang.String;

/**
 * @author Diki
 *
 */

/**
 * 我做了下面这些改动:
 * 主要就是把函数使用的关键词换成了字符串
 * 例如: 
 * entry.setInformation("baidu", info)来设置百度的翻译结果, 类似的"youdao", "bing"
 * entry.getInformation("baidu")来获取百度的翻译结果, 类似的"youdao", "bing"
 * entry.clickZan("baidu", "Xiaohong")表示用户"xiaohong"对百度的翻译结果点赞
 */

class Entry {
	static final int BAIDU = 0;
	static final int YOUDAO = 1;
	static final int BING = 2;
	static String[] sourceString = {"baidu", "youdao", "bing"};
	private String keyword;

	private Information[] informations = new Information[3];
	
	public Entry(String keyword) {
		this.keyword = keyword;
	}
	
	String getKeyword() {
		return keyword;
	}
	
	public boolean setInformation(Information info) {
		if (info.getSource().equals("baidu")) {
			informations[BAIDU] = info;
			return true;
		}
		if (info.getSource().equals("youdao")){
			informations[YOUDAO] = info;
			return true;
		}
		if (info.getSource().equals("bing")){
			informations[BING] = info;
			return true;
		}
		return false;
	}
	
	public Information getInformation(String source) {
		if (source.equals("baidu")) return informations[BAIDU];
		if (source.equals("youdao")) return informations[YOUDAO];
		if (source.equals("bing")) return informations[BING];
		return null;
	}

	public Information getInformation(int sourceNumber) {
		if (sourceNumber >= 0 && sourceNumber <= 2)
			return informations[sourceNumber];
		else
			return null;
	}
//	public boolean clickZan(String source, String user) {
//		if (source.equals("baidu")) return informations[BAIDU].clickZan(user);
//		if (source.equals("youdao")) return informations[YOUDAO].clickZan(user);
//		if (source.equals("bing")) return informations[BING].clickZan(user);
//		return false;
//	}
//	
//	public boolean clickUnzan(String source, String user) {
//		if (source.equals("baidu")) return informations[BAIDU].clickUnzan(user);
//		if (source.equals("youdao")) return informations[YOUDAO].clickUnzan(user);
//		if (source.equals("bing")) return informations[BING].clickUnzan(user);
//		return false;
//	}

	public String toString() {
		return 
			keyword + "^" + informations[BAIDU].toString() +
			"^" + informations[YOUDAO].toString() +
			"^" + informations[BING].toString();
	}
	
}
