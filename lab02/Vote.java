package lab02;

/* Class Vote
 * Implements zan and unzan.
 * update on 2014-12-13: added method polledBy(String)
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
			return true;
		}
	}
	
	// return if a given voter has polled this vote
	public boolean polledBy(String voter) {
		return voters.contains(voter);
	}
	
	public int getCount() {
		return count;
	}
}
