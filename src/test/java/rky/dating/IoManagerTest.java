package rky.dating;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.junit.Test;

import rky.dating.io.IoManager;
import rky.dating.player.Player.Players;
import rky.dating.primitives.Candidate;
import rky.dating.primitives.Noise;
import rky.dating.primitives.Preferences;

public class IoManagerTest
{
	protected static int N = 20;

    private static final int PORT = 54321;
    
    @Test
    public void test() throws Exception
    {
        IoManager io = new IoManager(PORT);
        TestServer server = new TestServer(io);
        server.start();
        
        Thread.sleep(1000);
        
        TestClient matchmaker = new TestMatchmakerClient(1);
//        matchmaker.send("team1");
        TestClient person = new TestPersonClient(2);
//        person.send("team2");
        
        /*
        Players players = io.getPlayers();
        System.out.println(players);
        String initMsg = matchmaker.receive();
        assertEquals(initMsg, "M 10");
        initMsg = person.receive();
        assertEquals(initMsg, "P 10");
        */
    }
    
    static class TestServer extends Thread 
    {
        private IoManager io;
        public volatile boolean active = true;

        public TestServer(IoManager io)
        {
            this.io = io;
        }
        
        public void run()
        {
            System.out.println("TEST: About to start server...");
            io.start(N);
            
            Dating.setIO(io);
            
            //initiate players
            Players players = io.getPlayers();
            
            Dating.setMatchmaker(players.matchmaker);
            Dating.setPerson(players.person);
            
            
            Dating.runGame(N);
            
//			while (active) {
//           }
        }
    }
    
    static class TestClient 
    {
        private Socket           socket;
        private PrintWriter      out;
        private BufferedReader   in;
        private int              id;

        public TestClient(int id) throws Exception
        {
            try
            {
                socket = new Socket("localhost", PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.id = id;
                System.out.println("Client " + id + " connected.");
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
                System.exit(1);
            }
        }

        public void send(String m)
        {
            out.println(m);
        }

        public String receive() throws IOException
        {
            String message;
            while ((message = in.readLine()) != null)
            {
                System.out.println("Client " + id + " received: " + message);
                return message;
            }
            
            return null;
        }
    }
    
    static class TestPersonClient extends TestClient
    {
    	private String lastReceive = null;
    	private String lastSend     = null;
    	
    	private Preferences prefs = null;

		public TestPersonClient(int id) throws Exception
		{
			super(id);
			send( "team1" );
		}
    	
		@Override
		public void send(String m)
		{
			lastSend = m;
			super.send(m);
		}
		
		@Override
		public String receive() throws IOException
		{
			lastReceive = super.receive();
			
			String msg = null;
			if( lastReceive.equals("WEIGHTS") ) {
				prefs = Preferences.generateRandomPreferences(N);
				msg = prefs.toString();
				send( msg );
			} 
			else if( lastReceive.equals("NOISE") ) {
				msg = Noise.generateRandomNoise( prefs ).toString();
				send( msg );
			}
			else {
				msg = "I don't know what to say.  The last thing the server told me was: " + lastReceive;
			}
			
			return lastReceive;
		}
    }
    
    static class TestMatchmakerClient extends TestClient
    {
    	private String lastReceive = null;
    	private String lastSend     = null;

		public TestMatchmakerClient(int id) throws Exception
		{
			super(id);
			send( "team2" );
		}
    	
		@Override
		public void send(String m)
		{
			lastSend = m;
			super.send(m);
		}
		
		@Override
		public String receive() throws IOException
		{
			String msg = null;
			
			lastReceive = super.receive();
			
			if( lastReceive.equals( "CANDIDATE" ) ) {
				msg = Candidate.generateRandomCandidate(N).toString();
				send( msg );
			}
			else {
				msg = "I don't know what to say. The last thing the server told me was: " + lastReceive;
			}
			
//			lastReceive = super.receive();
			
			return lastReceive; 
		}
    }
}
