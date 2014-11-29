/**
 * 
 */
package lab02;

import java.lang.String;

/**
 * @author Diki
 *
 */
class Entry {
	private static final int BAIDU = 0;
	private static final int YOUDAO = 1;
	private static final int BING = 2;

	private String keyword;
	private class Information{	//内部类，保存来自不同来源的解释
		private String source;
		private String attribute;
		private String phonetic;
		private String explanation;
		private int zan;
		private int unzan;
		private String[] zanList;
		private String[] unzanList;

		public Information(String source, String attribute, String phonetic, String explanation) {
			this.source = source;
			this.attribute = attribute;
			this.phonetic = phonetic;
			this.explanation = explanation;
		}

		public String getSource() {
			return source;
		}

		public String getAttribute() {
			return attribute;
		}

		public String getPhonetic() {
			return phonetic;
		}

		public String getExplanation() {
			return explanation;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public void setAttribute(String attribute) {
			this.attribute = attribute;
		}

		public void setPhonetic(String phonetic) {
			this.phonetic = phonetic;
		}

		public void setExplanation(String explanation) {
			this.explanation = explanation;
		}
	}
	private Information[] informations = new Information[3];
}
