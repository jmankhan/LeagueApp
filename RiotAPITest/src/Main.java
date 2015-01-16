import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import main.java.riotapi.RiotApi;
import main.java.riotapi.RiotApiException;
import constant.Region;
import dto.Match.MatchDetail;
import dto.MatchHistory.MatchSummary;
import dto.MatchHistory.Participant;
import dto.MatchHistory.ParticipantIdentity;
import dto.Summoner.Summoner;


public class Main {
	private static final String API_KEY = "56296469-3ccd-472c-a492-229c805c956f";
	
	public static void main(String[] args) throws RiotApiException {
		JFrame f = new JFrame();
        JTextField t = new JTextField(20);
        JButton b = new JButton("Search");
        JTextPane l = new JTextPane();

        f.setLayout(new FlowLayout());
        f.add(t);
        f.add(b);
        f.add(l);
        f.setVisible(true);
        f.setSize(500,500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        
        b.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		RiotApi api = new RiotApi(API_KEY);
                api.setRegion(Region.NA);

                Map<String, Summoner> me=null;
        		try {
                	me = api.getSummonerByName(t.getText());
                } catch(RiotApiException re) { re.printStackTrace(); }
        		
                
                Iterator<Summoner> it = me.values().iterator();
                Summoner summoner = it.next();
                String playerName = summoner.getName();
                long kills = 0;
                long deaths = 0;
                long assists = 0;
                long goldEarned = 0;
                long cs = 0;
                long[] items = new long[6];
                
                long id = summoner.getId();
                try {
					List<MatchSummary> matches = api.getMatchHistory(id).getMatches();
					MatchSummary summary = matches.get(matches.size()-1);
					
					long time=summary.getMatchCreation();
					long duration=summary.getMatchDuration(); //long of seconds in match
					
					Date date = new Date(time);
				    Format format = new SimpleDateFormat("MM/dd hh:mm");
				    
				    //get participant id
				    int pId = 0;
				    for(ParticipantIdentity p:matches.get(matches.size()-1).getParticipantIdentities()) {
				    	if(id == p.getPlayer().getSummonerId())
				    		pId = p.getParticipantId();
				    }
				    
				    //user participant id to get stats
				    //why cant i just use my summoner id .-.
				    for(Participant p:matches.get(matches.size()-1).getParticipants()) {
				    	if(p.getParticipantId() == pId) {
				    		kills = p.getStats().getKills();
				    		deaths = p.getStats().getDeaths();
				    		assists = p.getStats().getAssists();
				    		goldEarned = p.getStats().getGoldEarned();
				    		cs = p.getStats().getMinionsKilled();
				    		items[0] = p.getStats().getItem0();
				    		items[1] = p.getStats().getItem1();
				    		items[2] = p.getStats().getItem2();
				    		items[3] = p.getStats().getItem3();
				    		items[4] = p.getStats().getItem4();
				    		items[5] = p.getStats().getItem5();
				    	}
				    }
				    
				    l.setText("Name: " + playerName + "\n" + "Most recent match: " + format.format(date) + "\n" + "Match duration: " + (duration/60 + ":" + ((1000 - duration/60)/60))
				    		+ "\n" + "kda: " + kills + "/" + deaths + "/" + assists  + "\n" + "Gold earned: " + goldEarned + "\n" + "Creep score: " + cs + "\n");
				} catch (RiotApiException e1) {
					e1.printStackTrace();
				}
        	}
        });
	}
}
