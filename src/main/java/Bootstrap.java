import java.sql.Connection;

import static spark.Spark.get;

public class Bootstrap {

    public static void main(String[] args) {
        GameServiceImpl rs = new GameServiceImpl();
        VotingServiceImpl vs = new VotingServiceImpl();
        System.out.println(rs.addGame("Kody's awesome game", true));
        System.out.println("Game ID is " + rs.findGame("Kody's awesome game"));
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(false, "Kody's awesome game");

        System.out.println("Vote count is: " + vs.getVotes("Kody's awesome game"));
    }
}