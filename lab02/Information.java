/**
 * 
 */
package lab02;

/**
 * @author diki
 * updated on 2014-12-13: added method isZannedBy(String) and isUnzannedBy(String)
 */
public class Information {
	private String source;		// 来源
	private String phonetic;	// 音标
	private String attribute;	// 词性
	private String explanation;
	private int zan;
	private int unzan;
	
	/**
	 * @Constructor
	 */
	public Information(String source, String phonetic, String attribute, String explanation) {
		this.source = source;
		this.phonetic = phonetic;
		this.attribute = attribute;
		this.explanation = explanation;
		zan = 0;
		unzan = 0;
	}
	
	public Information(String source, String phonetic, String attribute, String explanation, int zan, int unzan) {
		this.source = source;
		this.phonetic = phonetic;
		this.attribute = attribute;
		this.explanation = explanation;
		this.zan = zan;
		this.unzan = unzan;
	}
	
	/**
	 * @return the source
	 */
	String getSource() {
		return source;
	}
	/**
	 * @return the phonetic
	 */
	String getPhonetic() {
		return phonetic;
	}
	/**
	 * @return the attribute
	 */
	String getAttribute() {
		return attribute;
	}
	/**
	 * @return the explanation
	 */
	String getExplanation() {
		return explanation;
	}
	
	void clickZan() {
		zan ++;
	}
	
	void clickUnzan() {
		unzan ++;
	}
	
	int getZan() {
		return zan;
	}
	
	int getUnzan() {
		return unzan;
	}

	public String toString() {
		return
			getSource() + "$" +
			getPhonetic() + "$" +
			getAttribute() + "$" +
			getExplanation() + "$" +
			getZan() + "$" +
			getUnzan() + "$";
	}

//	// return if a certain user has zanned this info
//	public boolean isZannedBy(String userName) {
//		return zan.polledBy(userName);
//	}
//
//	// return if a certain user has unzanned this info
//	public boolean isUnzannedBy(String userName) {
//		return unzan.polledBy(userName);
//	}
}
