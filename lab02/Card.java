/**
 * 
 */
package lab02;

/**
 * @author Denny
 *
 */
class Card {
	private String keyword;
	private Information satellite;
	private User sender;
	/**
	 * @return the keyword
	 */
	String getKeyword() {
		return keyword;
	}
	/**
	 * @param keyword the keyword to set
	 */
	void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * @return the satellite
	 */
	Information getSatellite() {
		return satellite;
	}
	/**
	 * @param satellite the satellite to set
	 */
	void setSatellite(Information satellite) {
		this.satellite = satellite;
	}
	/**
	 * @return the sender
	 */
	User getSender() {
		return sender;
	}
	/**
	 * @param sender the sender to set
	 */
	void setSender(User sender) {
		this.sender = sender;
	}
}
