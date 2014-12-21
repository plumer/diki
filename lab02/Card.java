package lab02;

import java.lang.String;

/**
 * 单词卡类，已经废弃
 *
 */
class Card {
	private String keyword;
	private String explanationSource;
	private String sender;

	Card(String keyword, String explanationSource, String sender) {
		this.keyword = keyword;
		this.explanationSource = explanationSource;
		this.sender = sender;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getExplanationSource() {
		return explanationSource;
	}

	public String getSender() {
		return sender;
	}

	public void setExplanationSource(String explanationSource) {
		this.explanationSource = explanationSource;
	}
}
