package rky.dating.player;

public class Player
{
    public enum Role
    {
        M, P
    }

    public final String name;
    public final Role role;
    
    public Player(String name, Role role)
    {
        this.name = name;
        this.role = role;
    }
    
    public static class Players
    {
        public final Player matchmaker;
        public final Player person;

        public Players(Player m, Player p)
        {
            this.matchmaker = m;
            this.person = p;
        }
    }

}
