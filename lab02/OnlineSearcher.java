package lab02;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * get到神器jsoup 可以百度到jar包然后import一下嗯
 * 
 * 注意： '#'来分开两种发音、词性和解释 例如：查 hello 音标: 美 [heˈləʊ]#英 [hə'ləʊ] 词性: int.#n. 解释:
 * 你好；喂；您好；哈喽#你好；嘿；（表示惊讶）嘿 保证词性个数和解释个数一一对应
 * */
class OnlineSearcher {
	private ExecutorService executor;

	public OnlineSearcher() {
		executor = Executors.newFixedThreadPool(3);
	}

	private class searchBaidu implements Runnable {
		private String baidu;
		private String keyword;
		private Information info;

		public searchBaidu(String keyword) {
			this.keyword = keyword;
			info = new Information("baidu", null, null, null);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			baidu = "http://cidian.baidu.com/s?wd=" + keyword;
			String src = null, phonetic = null, attribute = null, explanation = null;

			src = info.getSource();
			Document baidudoc;
			try {
				baidudoc = Jsoup.connect(baidu).timeout(5000).get();
				/* 获取音标 */
				Elements temp = baidudoc.getElementsByTag("b");
				for (Element i : temp) {
					if (i.hasAttr("lang")) {
						if (phonetic == null)
							phonetic = i.text();
						else
							phonetic = phonetic + '#' + i.text();
					}
				}
				/* 获取词性 */
				temp = baidudoc.getElementById("en-simple-means")
						.getElementsByTag("strong");
				for (Element i : temp) {
					if (attribute == null)
						attribute = i.text();
					else
						attribute = attribute + '#' + i.text();
				}
				/* 获取解释 */
				temp = baidudoc.getElementById("en-simple-means")
						.getElementsByTag("span");
				for (int i = 0; i < attribute.split("#").length; i++) {
					if (explanation == null)
						explanation = temp.get(i).text();
					else
						explanation = explanation + '#' + temp.get(i).text();
				}
				/*
				 * System.out.println(phonetic); System.out.println(attribute);
				 * System.out.println(explanation);
				 */
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connect time out");
			}
			// System.out.println("baidu");
			info = new Information(src, phonetic, attribute, explanation);
		}

		public Information getInformation() {
			// System.out.println(info.toString());
			return info;
		}

	}

	private class searchYoudao implements Runnable {
		private String youdao;
		private String keyword;
		private Information info;

		public searchYoudao(String keyword) {
			this.keyword = keyword;
			info = new Information("youdao", null, null, null);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			youdao = "http://dict.youdao.com/search?le=eng&q=" + keyword
					+ "&keyfrom=dict.top";
			String src = info.getSource(), phonetic = null, attribute = null, explanation = null;
			// System.out.println("youdao");
			Document youdaodoc;
			try {
				youdaodoc = Jsoup.connect(youdao).timeout(5000).get();
				/* 获取音标 */
				Elements temp = youdaodoc.getElementsByClass("phonetic");
				for (int i = 0; i < temp.size() && i < 2; i++) {
					if (phonetic == null)
						phonetic = temp.get(i).text();
					else
						phonetic = phonetic + '#' + temp.get(i).text();
				}
				/* 获取词性和解释 */
				temp = youdaodoc.getElementsByClass("trans-container").get(0)
						.getElementsByTag("li");
				for (Element i : temp) {
					if (attribute == null) {
						if (i.text().contains(". ")) {
							attribute = i.text().split(". ")[0];
							explanation = i.text().split(". ")[1];
						} else {
							explanation = i.text();
						}
					} else {
						if (i.text().contains(". ")) {
							attribute = attribute + '#'
									+ i.text().split(". ")[0];
							explanation = explanation + '#'
									+ i.text().split(". ")[1];
						} else {
							explanation = explanation + '#' + i.text();
						}
					}
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connect time out");
			}
			/*
			 * System.out.println(phonetic); System.out.println(attribute);
			 * System.out.println(explanation);
			 */
			info = new Information(src, phonetic, attribute, explanation);

		}

		public Information getInformation() {
			// System.out.println(info.toString());
			return info;
		}

	}

	private class searchBing implements Runnable {
		private String bing;
		private String keyword;
		private Information info;

		public searchBing(String keyword) {
			this.keyword = keyword;
			info = new Information("bing", null, null, null);
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			bing = "http://cn.bing.com/dict/search?q=" + keyword
					+ "&go=&qs=bs&form=CM";
			String src = info.getSource(), phonetic = null, attribute = null, explanation = null;
			// System.out.println("bing");
			Document bingdoc;
			try {
				bingdoc = Jsoup.connect(bing).timeout(5000).get();
				/* 获取音标 */
				Elements temp = bingdoc.getElementsByClass("hd_prUS");
				if (temp.size() > 0)
					phonetic = temp.text();
				temp = bingdoc.getElementsByClass("hd_pr");
				if (temp.size() > 0)
					phonetic = phonetic + '#' + temp.text();
				/* 获取词性和解释 */
				temp = bingdoc.getElementsByClass("qdef").get(0)
						.getElementsByTag("ul").get(0).getElementsByTag("li");
				for (Element i : temp) {
					Element j = i.getElementsByClass("pos").get(0);
					if (j.className().equals("pos")) {
						if (attribute == null)
							attribute = j.text();
						else
							attribute = attribute + '#' + j.text();
						Element k = i.getElementsByClass("def").get(0);
						if (explanation == null)
							explanation = k.text();
						else
							explanation = explanation + '#' + k.text();
					}
				}
				/*
				 * System.out.println(phonetic); System.out.println(attribute);
				 * System.out.println(explanation);
				 */
				info = new Information(src, phonetic, attribute, explanation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("connect time out");
			}
		}

		public Information getInformation() {
			// System.out.println(info.toString());
			return info;
		}

	}

	Entry search(String keyword) {
		Entry result = new Entry(keyword);

		 executor = Executors.newFixedThreadPool(3);

		searchBaidu baidu = new searchBaidu(keyword);
		searchYoudao youdao = new searchYoudao(keyword);
		searchBing bing = new searchBing(keyword);
		
		//Thread baidu = new Thread(new searchBaidu(keyword), "baidu");
		//Thread youdao = new Thread(new searchYoudao(keyword), "youdao");
		//Thread bing = new Thread(new searchBing(keyword), "bing");
		executor.execute(baidu);
		executor.execute(youdao);
		executor.execute(bing);

		executor.shutdown();
		
		try {
		//	baidu.join();
		//	youdao.join();
		//	bing.join();
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("thread error");
		}

		result.setInformation(baidu.getInformation());
		result.setInformation(youdao.getInformation());
		result.setInformation(bing.getInformation());
		/*System.out.println(result.getInformation("baidu"));
		System.out.println(result.getInformation("youdao"));
		System.out.println(result.getInformation("bing"));*/
		return result;
	}

	/*
	 * public static void main(String[] args) { OnlineSearcher oser = new
	 * OnlineSearcher(); oser.search("hello"); oser.search("Amy");
	 * oser.search("Amily"); }
	 */
}
