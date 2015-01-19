import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import dto.MatchHistory.PlayerHistory;
import dto.Summoner.Summoner;

/**
 * This object will hold all relevant summoner information that will then be sent to a client from the server
 * It basically serves as a wrapper API for the api
 * @author jmankhan
 *
 */
public class SummonerInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4825751205243373333L;

	private long summonerId;
	
	private String summonerName;
	private ArrayList<LeagueMatch> matches;
	
	private transient RiotApi api;
	
	/**
	 * Initializes values for the summoner and its history
	 * @param api RiotApi that contains the information
	 * @param name Summoner name
	 */
	public SummonerInfo(RiotApi api, String name) {
		summonerName = name;
		this.api=api;
		
		//guess what this does
		summonerId = generateSummonerId();
		
		//check to make sure we do not search for matches that don't exist
		if(summonerId != -1)
			matches = generateMatchHistory();
		
		//if summoner does not exist, return an empty match history to prevent crashing
		else
			matches = new ArrayList<LeagueMatch>();
	}
	
	/**
	 * gets the summoner id, if -1 is returned, id was not found
	 * @return summoner id
	 */
	private long generateSummonerId() {
		Map<String, Summoner> map=null;
		try {
			map = api.getSummonerByName(summonerName);
		} catch (RiotApiException e) {e.printStackTrace();}
		Iterator<Summoner> it = map.values().iterator();
		if(it.hasNext())
			return it.next().getId();
		
		return -1;
	}

	/**
	 * Finds and stores the past 3 solo queue ranked matches of player, from the previous 15 matches
	 * @return ArrayList<LeagueMatch>
	 */
	private ArrayList<LeagueMatch> generateMatchHistory() {
		ArrayList<LeagueMatch> matches = new ArrayList<LeagueMatch>();
		try {
			PlayerHistory history = api.getMatchHistory(summonerId);
			for(int i=history.getMatches().size()-1; i>=0; i--) {
				if(history.getMatches().get(i).getQueueType().equalsIgnoreCase("RANKED_SOLO_5x5")
						&& matches.size() < 3) {
					matches.add(new LeagueMatch(api, summonerId, i));
				}
			}
		} catch (RiotApiException e) {e.printStackTrace();}
		
		return matches;
	}
	
	/**
	 * public access method to get match history
	 * @return ArrayList<LeagueMatch> matches
	 */
	public ArrayList<LeagueMatch> getMatchHistory() {
		return matches;
	}

	/**
	 * public access method to get summoner id, if -1 is returned, the id was not found
	 * @return long summonerId
	 */
	public long getSummonerId() {
		return summonerId;
	}
	
	/**
	 * public access method to get summoner name
	 * @return String summonerName
	 */
	public String getSummonerName() {
		return summonerName;
	}
}
