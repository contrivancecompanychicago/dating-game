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
    
    @Override
    public String toString()
    {
        return "Player [name=" + name + ", role=" + role + "]";
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

        @Override
        public String toString()
        {
            return "Players [matchmaker=" + matchmaker.toString() 
                    + ", person=" + person.toString() + "]";
        }
        
    }
}
