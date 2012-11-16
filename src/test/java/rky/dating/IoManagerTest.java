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

public class IoManagerTest
{

    private static final int PORT = 54321;
    
    @Test
    public void test() throws Exception
    {
        IoManager io = new IoManager(PORT);
        TestServer server = new TestServer(io);
        server.start();
        TestClient matchmaker = new TestClient(1);
        matchmaker.send("team1");
        TestClient person = new TestClient(2);
        person.send("team2");
        Thread.sleep(1000);
        Players players = io.getPlayers();
        System.out.println(players);
        String initMsg = matchmaker.receive();
        assertEquals(initMsg, "M 10");
        initMsg = person.receive();
        assertEquals(initMsg, "P 10");
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
            io.start(10);
            while (active) { }
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
}
