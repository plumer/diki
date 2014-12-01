/**
 *
 */
package lab02;

import java.lang.String;

/**
 * @author Diki
 *
 */
class Card {
	private String keyword;
	private int explanationSource;
	private String sender;

	Card(String keyword, int explanationSource, String sender) {
		this.keyword = keyword;
		this.explanationSource = explanationSource;
		this.sender = sender;
	}

	public String getKeyword() {
		return keyword;
	}

	public int getExplanationSource() {
		return explanationSource;
	}

	public String getSender() {
		return sender;
	}

	public void setExplanationSource(int explanationSource) {
		this.explanationSource = explanationSource;
	}
}
