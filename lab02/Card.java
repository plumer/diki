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
	private int userSource;

	Card(String keyword, int explanationSource, int userSource) {
		this.keyword = keyword;
		this.explanationSource = explanationSource;
		this.userSource = userSource;
	}

	public String getKeyword() {
		return keyword;
	}

	public int getExplanationSource() {
		return explanationSource;
	}

	public int getUserSource() {
		return userSource;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setExplanationSource(int explanationSource) {
		this.explanationSource = explanationSource;
	}

	public void setUserSource(int userSource) {
		this.userSource = userSource;
	}
}
