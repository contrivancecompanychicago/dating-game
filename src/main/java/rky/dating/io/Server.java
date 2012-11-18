package rky.dating.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import rky.dating.player.Player;
import rky.dating.player.Player.Players;
import rky.dating.player.Player.Role;

public class Server
{
    private final ServerSocket serverSocket;
    private final Map<Player, Client> clients = new HashMap<Player, Client>();

    public Server(int port)
    {
        try
        {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e)
        {
            e.printStackTrace(System.err);
            throw new RuntimeException("Unable to bind to port " + port);
        }
    }

    public Players start()
    {
        try
        {
            //Accept connection from player1; matchmaker
            Socket p1Socket = serverSocket.accept();
            Client first = new Client(p1Socket);
            Player p1 = new Player(first.receive(), Role.M);
            clients.put(p1, first);
            System.out.println(p1.name + " connected as matchmaker.");

            // Accept connection from player2; person
            Socket p2Socket = serverSocket.accept();
            Client second = new Client(p2Socket);
            Player p2 = new Player(second.receive(), Role.P);
            clients.put(p2, second);
            System.out.println(p2.name + " connected as person.");

            return new Players(p1, p2);
        }
        catch (Exception e)
        {
            e.printStackTrace(System.err);
        }

        throw new RuntimeException("Unable to initialize connection with clients.");
    }
    
    public void send(Player p, String s)
    {
        clients.get(p).send(s);
    }

    public String receive(Player p)
    {
        return clients.get(p).receive();
    }
    
    public void shutdown()
    {
        for (Client c : clients.values())
        {
            // TODO: Clean-up
        }
    }

    private static class Client
    {
        private final BufferedReader in;
        private final PrintWriter    out;

        public Client(Socket s) throws Exception
        {
            out = new PrintWriter(s.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        public void send(String s)
        {
            try
            {
                out.println(s);
            }
            catch (Exception e)
            {
                e.printStackTrace(System.err);
                throw new RuntimeException("Unable to send message to client.");
            }
        }

        public String receive()
        {
            try
            {
                String message;
                while ((message = in.readLine()) != null)
                {
                    return message;
                }
            }
            catch (IOException e)
            {
                e.printStackTrace(System.err);
            }

            throw new RuntimeException("Unable to receive message from client.");
        }
    }
}
