import static spark.Spark.get;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        GameServiceImpl rs = new GameServiceImpl();
        VotingServiceImpl vs = new VotingServiceImpl();
        logger.info(rs.addGame("Kody's awesome game", true));
        logger.info("Game ID is " + rs.findGame("Kody's awesome game"));
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(true, "Kody's awesome game");
        vs.changeVote(false, "Kody's awesome game");

        logger.info("Vote count is: " + vs.getVotes("Kody's awesome game"));
    }
}