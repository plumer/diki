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
	private Vote zan;
	private Vote unzan;
	
	/**
	 * @Constructor
	 */
	public Information(String source, String phonetic, String attribute, String explanation) {
		this.source = source;
		this.phonetic = phonetic;
		this.attribute = attribute;
		this.explanation = explanation;
		zan = new Vote();
		unzan = new Vote();
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
	
	boolean clickZan(String devil) {
		return zan.poll(devil);
	}
	
	boolean clickUnzan(String devil) {
		return unzan.poll(devil);
	}
	
	int getZan() {
		return zan.getCount();
	}
	
	int getUnzan() {
		return unzan.getCount();
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

	// return if a certain user has zanned this info
	public boolean isZannedBy(String userName) {
		return zan.polledBy(userName);
	}

	// return if a certain user has unzanned this info
	public boolean isUnzannedBy(String userName) {
		return unzan.polledBy(userName);
	}
}
