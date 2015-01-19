import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Client extends Thread {
	private final int PORT = 5555;
	private InetAddress ipAddress;
	
	SummonerInfo summonerInfo;
	DatagramSocket socket;
	
	public Client() {
		try {
			socket = new DatagramSocket();
		} catch (SocketException e) {e.printStackTrace();}
		
		try {
			ipAddress = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {e.printStackTrace();}
		
	}
	
	public void run() {
		String summonerName = "DaTissueBox";
		sendData(summonerName.getBytes());
		
		while(true) {
			
			byte[] data = new byte[6400];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			
			try {
				socket.receive(packet);
				
		        ByteArrayInputStream baos = new ByteArrayInputStream(data);
		        ObjectInputStream oos = new ObjectInputStream(baos);
		        try {
					summonerInfo = (SummonerInfo) oos.readObject();
					System.out.println(summonerInfo.getSummonerName());
					System.out.println(summonerInfo.getMatchHistory().get(0).getCsDiffFromZeroToTenMinutes());
					
				} catch (ClassNotFoundException e) {e.printStackTrace();}
		        
			} catch (IOException e) {e.printStackTrace();}
			
		}
	}
	
	public void sendData(byte[] data) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, PORT);
		
		try {
			socket.send(packet);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void main(String args[]) {
		new Client().start();
	}
}