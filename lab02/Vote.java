package lab02;

/* Class Vote
 * Implements zan and unzan.
 */

import java.util.*;

 
public class Vote {
	private int count;
	HashSet<String> voters;
	
	public Vote() {
		count = 0;
		voters = new HashSet<String>();
	}
	
	public boolean poll(String voter) {
		if ( voters.contains(voter) ) {
			return false;
		} else {
			voters.add(voter);
			count++;
		}
	}
	
	public int getCount() {
		return count;
	}
}