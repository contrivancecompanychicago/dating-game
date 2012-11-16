    package rky.dating;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import rky.dating.io.Message;

public class MessageTest
{

    @Test
    public void createAck()
    {
        
        Message m = Message.ACK;
        assertEquals (
            "OK", 
            m.toString()
        );
    }
    
    @Test
    public void createError()
    {
        Message m = Message.createError("You suck at math.");
        assertEquals (
            "ERROR \"You suck at math.\"", 
            m.toString()
        );
    }
    
    @Test
    public void createVector()
    {
        Double[] v = {-0.03, 0.23, 0.01};
        Message m = Message.createVector(v);
        assertEquals (
            "[-0.03, 0.23, 0.01]", 
            m.toString()
        );
    }
    
    @Test
    public void createCandidates()
    {
        Map<Double[], Double> c = new HashMap<Double[], Double>();
        Double[] k1 = {-0.03, 0.23, 0.01};
        Double v1 = 0.23;
        c.put(k1, v1);
        Double[] k2 = {-0.55, 0.23, 0.01};
        Double v2 = -0.93;
        c.put(k2, v2);
        Message m = Message.createCandidates(c);
        assertEquals (
            "[-0.55, 0.23, 0.01] -0.93, [-0.03, 0.23, 0.01] 0.23", 
            m.toString()
        );
    }
    
    @Test
    public void createGameOver()
    {
        Message m = Message.createGameOver(-0.09, 20);
        assertEquals (
          "GAMEOVER -0.09 20", 
          m.toString()
        );
    }
    
}
