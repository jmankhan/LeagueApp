import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import main.java.riotapi.RiotApi;
import constant.Region;

/**
 * This server class will take incoming connections, determine their request, and send an object with the necessary information back to the client
 * @author jmankhan
 *
 */
public class Server extends Thread {
	private final int PORT = 5555;
	private final String API_KEY = "56296469-3ccd-472c-a492-229c805c956f";
	
	private DatagramSocket socket;
	private transient RiotApi api;
	private SummonerInfo summonerInfo;
	
	public Server () throws SocketException, UnknownHostException {
		
		//create socket for internet communication
		socket = new DatagramSocket(PORT);
		
		//create api
		api = new RiotApi(API_KEY);
		api.setRegion(Region.NA);
	}
	
	public void run() {
		while(true) {
			//create array to store incoming data
			byte[] data = new byte[6400];
			DatagramPacket packet = new DatagramPacket(data, data.length);

			try {
				//retrieve incoming data
				socket.receive(packet);
	
				//store incoming data
				String summonerName = new String(data).trim();
				
				//create object to hold info
				summonerInfo = new SummonerInfo(api, summonerName);
				
				//write object to byte array
				ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(summonerInfo);

				//send array back to client
				data = baos.toByteArray();
				sendData(data, packet.getAddress(), packet.getPort());
				
			} catch (IOException e) {e.printStackTrace();}
			
		}
	}
	
	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			socket.send(packet);
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void main(String args[]) {
		try {
			new Server().start();
		} catch (SocketException | UnknownHostException e) {e.printStackTrace();}
	}
}
