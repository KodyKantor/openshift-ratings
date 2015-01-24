package com.ratings.kkantor;

import static spark.Spark.get;

import com.ratings.kkantor.service.GameServiceImpl;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class Bootstrap {
    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    public static void main(String[] args) {
        GameServiceImpl rs = new GameServiceImpl();
        logger.info(rs.addGame("Kody's awesome game", true));
        logger.info("Game ID is " + rs.findGame("Kody's awesome game"));
    }
}