package com.ratings.kkantor;

import static spark.Spark.get;

import com.ratings.kkantor.service.GameServiceImpl;
import com.ratings.kkantor.service.VotingServiceImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        get("/test", (request, response) -> doStuff());
    }
    public static String doStuff() {
        logger.debug("Doing stuff");
        GameServiceImpl rs = new GameServiceImpl();
        VotingServiceImpl vs = new VotingServiceImpl();
        logger.info("Game added? " + rs.addGame("Kody's awesome game", true));
        logger.info("Voted. New score is " + vs.changeVote(true, "Kody's Awesome Game"));
        logger.info("Game ID is " + rs.findGame("Kody's awesome game"));
        logger.info("Deleted game and votes? " + rs.deleteGame("Kody's awesome game"));
        return "Hello!";
    }
}
