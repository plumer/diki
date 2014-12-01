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

	private Information[] informations = new Information[3];
	
	public Entry(String keyword) {
		this.keyword = keyword;
	}
	
	public boolean setInformation(int source, Information info) {
		if (source < 0 || source > 2)
			return false;
		if (informations[source] == null) {
			informations[source] = new Information (
				String source, String phonetic, String attribute, String explanation
			);
			return true;
		}
		return false;
	}
	
	public Information getInformation(int source) {
		if (source < 0 || source > 2) 
			return null;
		else
			return informations[source];
	}
}
