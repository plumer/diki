package lab02;

import java.io.*;
import java.net.*;

class OnlineSearcher {
	private URL baidu;
	private URL youdao;
	private URL bing;
	
	Entry search(String keyword) {
		try {
			baidu = new URL("http://cidian.baidu.com/s?wd=" + keyword);
			youdao = new URL("http://dict.youdao.com/search?le=eng&q=" + keyword + "&keyfrom=dict.top");
			bing = new URL("http://cn.bing.com/dict/search?q=" + keyword + "&go=&qs=bs&form=CM");
			
			InputStream input = baidu.openStream();
			
		} catch (IOException e) {
			System.out.println("url wrong or url open wrong!");
		}
		Entry result = new Entry();
		return result;
	}
	
	public static void main(String[] args) {
		OnlineSearcher oser = new OnlineSearcher();
		oser.search("hello");
	}
}
