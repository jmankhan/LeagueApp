import java.io.Serializable;
import java.util.List;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import dto.Match.MatchDetail;
import dto.MatchHistory.Participant;
import dto.MatchHistory.ParticipantIdentity;
import dto.MatchHistory.ParticipantStats;
import dto.MatchHistory.ParticipantTimeline;
import dto.MatchHistory.PlayerHistory;

/**
 * This is going to be REALLY ANNOYING
 * @author jmankhan
 *
 */
public class LeagueMatch implements Serializable {
	private long summonerId, matchId, startTime, duration, cs, kills, deaths, assists, goldEarned, sightWards, visionWards;
	private double cs010, cs1020, cs2030, cs30end;
	private double csd010, csd1020, csd2030, csd30end;
	private double gold010, gold1020, gold2030, gold30end;
	private boolean wonGame, gotFirstBlood;
	
	private int index;
	private transient RiotApi api;
	
	
	/**
	 * Stores match info of particular summoner
	 * @param api the RiotApi
	 * @param index place of game in Match History
	 */
	public LeagueMatch(RiotApi api, long summonerId, int index) {
		this.api = api;
		this.summonerId = summonerId;
		
		PlayerHistory history = null;
		try {
			history = api.getMatchHistory(summonerId);
		} catch (RiotApiException e) {e.printStackTrace();}
		this.index=index;
		
		matchId = history.getMatches().get(index).getMatchId();
		startTime = generateStartTime();
		duration  = generateDuration();
		
		Participant participant = generateParticipantBySummonerId();
		ParticipantStats stats = participant.getStats();
		
		cs = stats.getMinionsKilled() + stats.getNeutralMinionsKilled();
		kills = stats.getKills();
		deaths = stats.getDeaths();
		assists = stats.getAssists();
		goldEarned = stats.getGoldEarned();
		sightWards = stats.getSightWardsBoughtInGame();
		visionWards = stats.getVisionWardsBoughtInGame();

		ParticipantTimeline tl = participant.getTimeline();
		cs010 = tl.getCreepsPerMinDeltas().getZeroToTen();
		cs1020 = tl.getCreepsPerMinDeltas().getTenToTwenty();
		cs2030 = tl.getCreepsPerMinDeltas().getTwentyToThirty();
		cs30end = tl.getCreepsPerMinDeltas().getThirtyToEnd();
		
		//not sure why this doesn't work, perhaps its deprecated?
		//this measures the cs difference between you and your lane opponent
		//idk how this would work in a 5v5... maybe it is intended for teambuilder only
		//will not use for now
//		csd010 = tl.getCsDiffPerMinDeltas().getZeroToTen();
//		csd1020 = tl.getCsDiffPerMinDeltas().getTenToTwenty();
//		csd2030 = tl.getCsDiffPerMinDeltas().getTwentyToThirty();
//		csd30end = tl.getCsDiffPerMinDeltas().getThirtyToEnd();
		
		gold010 = tl.getGoldPerMinDeltas().getZeroToTen();
		gold1020 = tl.getGoldPerMinDeltas().getTenToTwenty();
		gold2030 = tl.getGoldPerMinDeltas().getTwentyToThirty();
		gold30end = tl.getGoldPerMinDeltas().getThirtyToEnd();
		
		
		gotFirstBlood = stats.isFirstBloodKill();
		wonGame = stats.isWinner();
	}
	
	private long generateStartTime() {
		MatchDetail detail = null;
		try {
			detail = api.getMatch(matchId);
		} catch(RiotApiException rae) {rae.printStackTrace();}
		return detail.getMatchCreation();
	}
	
	private long generateDuration() {
		MatchDetail detail = null;
		try {
			detail = api.getMatch(matchId);
		} catch(RiotApiException rae) {rae.printStackTrace();}
		return detail.getMatchDuration();
	}
	
	private Participant generateParticipantBySummonerId() {
		PlayerHistory history = null;
			
		try {
			history = api.getMatchHistory(summonerId);
		} catch (RiotApiException e) {e.printStackTrace();}
		
		int pId=0;
		List<ParticipantIdentity> pIdList = history.getMatches().get(index).getParticipantIdentities();
		for(ParticipantIdentity p:pIdList) {
			if(summonerId == p.getPlayer().getSummonerId()) {
				pId = p.getParticipantId();
			}
		}
		
		for(Participant p:history.getMatches().get(index).getParticipants()) {
			if(p.getParticipantId() == pId) {
				return p;
			}
		}
		
		return null;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getDuration() {
		return duration;
	}
	
	public long getCs() {
		return cs;
	}
	
	public long getKills() {
		return kills;
	}
	
	public long getDeaths() {
		return deaths;
	}
	
	public long getAssists() {
		return assists;
	}
	
	public long getGoldEarned() {
		return goldEarned;
	}
	
	public long getSightWardsBought() {
		return sightWards;
	}
	
	public long getVisionWardsBought() {
		return visionWards;
	}
	
	public boolean gotFirstBlood() {
		return gotFirstBlood;
	}
	
	public boolean wonGame() {
		return wonGame;
	}
	
	public double getCsFromZeroToTenMinutes() {
		return cs010;
	}
	
	public double getCsFromTenToTwentyMinutes() {
		return cs1020;
	}
	
	public double getCsFromTwentyToThirtyMinutes() {
		return cs2030;
	}
	
	public double getCsFromThirtyToEndMinutes() {
		return cs30end;
	}
	
	public double getCsDiffFromZeroToTenMinutes() {
		return csd010;
	}
	
	public double getCsDiffFromTenToTwentyMinutes() {
		return csd1020;
	}
	
	public double getCsDiffFromTwentyToThirtyMinutes() {
		return csd2030;
	}
	
	public double getCsDiffFromThirtyToEndMinutes() {
		return csd30end;
	}
	
	public double getGoldFromZeroToTenMinutes() {
		return gold010;
	}
	
	public double getGoldFromTenToTwentyMinutes() {
		return gold1020;
	}
	
	public double getGoldFromTwentyToThirtyMinutes() {
		return gold2030;
	}
	
	public double getGoldFromThirtyToEndMinutes() {
		return gold30end;
	}
	
	public long getMatchId() {
		return matchId;
	}
}
