/**
 * 
 */
package lab02;

/**
 * @author diki
 *
 */
class Entry implements Comparable{
	private String keyword;
	Information[] satellite;
	
	Entry(String keyword){
		this.keyword = keyword;
	}
	
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
	Information[] getSatellite() {
		return satellite;
	}

	/**
	 * @param satellite the satellite to set
	 */
	void setSatellite(Information[] satellite) {
		this.satellite = satellite;
	}
}
