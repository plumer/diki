/**
 * 
 */
package lab02;

/**
 * @author Denny
 *
 */
class Information {
	private String source;		// ÄÄ¸ö´Êµä
	private String phonetic;	// Òô±ê
	private String attribute;	// ´ÊÐÔ
	private String explanation;
	private int zan;
	private int unzan;
	/**
	 * @return the source
	 */
	String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the phonetic
	 */
	String getPhonetic() {
		return phonetic;
	}
	/**
	 * @param phonetic the phonetic to set
	 */
	void setPhonetic(String phonetic) {
		this.phonetic = phonetic;
	}
	/**
	 * @return the attribute
	 */
	String getAttribute() {
		return attribute;
	}
	/**
	 * @param attribute the attribute to set
	 */
	void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return the explanation
	 */
	String getExplanation() {
		return explanation;
	}
	/**
	 * @param explanation the explanation to set
	 */
	void setExplanation(String explanation) {
		this.explanation = explanation;
	}
	/**
	 * @return the zan
	 */
	int getZan() {
		return zan;
	}
	/**
	 * @param zan the zan to set
	 */
	void setZan(int zan) {
		this.zan = zan;
	}
	/**
	 * @return the unzan
	 */
	int getUnzan() {
		return unzan;
	}
	/**
	 * @param unzan the unzan to set
	 */
	void setUnzan(int unzan) {
		this.unzan = unzan;
	}
	
	int clickZan(){
		zan ++;
		return zan;
	}
	
	int clickUnzan() {
		unzan ++;
		return unzan;
	}
}
