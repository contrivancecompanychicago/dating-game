package rky.dating.io;

import java.util.Map;
import java.util.Map.Entry;

public class Message
{
    public static final Message ACK = new Message("OK");

    public static final Message WEIGHT_PROMPT    = new Message("WEIGHTS");
    public static final Message NOISE_PROMPT     = new Message("NOISE");
    public static final Message CANDIDATE_PROMPT = new Message("CANDIDATE");
    
    private String body;

    public Message(String body)
    {
        this.body = body;
    }

    public static Message createVector(Double[] vector)
    {
        return new Message(arrayToString(vector));
    }

    public static Message createCandidates(Map<Double[], Double> candidates)
    {
        StringBuilder builder = new StringBuilder();
        for (Entry<Double[], Double> e : candidates.entrySet())
        {
            builder.append(arrayToString(e.getKey()) + " " + e.getValue() + ", ");
        }
        String body = builder.toString();
        
        return new Message(body.substring(0, body.length()-2));
    }

    public static Message createGameOver(Double score, Integer numAttempts)
    {
        return new Message("GAMEOVER " + score + " " + numAttempts);
    }

    public static Message createError(String errorMessage)
    {
        return new Message("ERROR \"" + errorMessage + "\"");
    }

    @Override
    public String toString()
    {
        return body;
    }
   
    private static String arrayToString(Double[] a)
    {
        StringBuffer result = new StringBuffer();
        result.append("[");
        if (a.length > 0)
        {
            result.append(a[0]);
            for (int i = 1; i < a.length; i++)
            {
                result.append(", ");
                result.append(a[i]);
            }
        }
        result.append("]");
        
        return result.toString();
    }

}
