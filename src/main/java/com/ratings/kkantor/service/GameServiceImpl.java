package com.ratings.kkantor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.ratings.kkantor.common.Game;
import org.hibernate.criterion.Restrictions;


/**
 * @author Kody Kantor
 * com.ratings.kkantor.service.GameServiceImpl is a com.ratings.kkantor.service.DatabaseService that provides methods for users
 * to add games to the DB, and set them as owned/not owned.
 *
 * TODO add delete functionality
 */
public class GameServiceImpl extends DatabaseService implements GameService {
    private static String gameTable = "Games";
    private Connection conn = getConnection();
    private static final Logger logger = LogManager.getLogger(GameServiceImpl.class);


    /**
     * findGame converts game titles to gameIds.
     * @param title name of the game
     * @return id associated with title
     */
    public int findGame(String title) {
        Session session = getSessionFactory().openSession();
        logger.debug("Opened hibernate session");
        Criteria criteria = session.createCriteria(Game.class);
        criteria.add(Restrictions.like("title", title));
        List results = criteria.list();
        session.close();
        if (results.size() > 1) {
            logger.debug("Multiple games found with same title!");
        }
        if (results.size() < 1) {
            logger.debug("No game found with that title");
            return 0;
        }
        Game game = (Game) results.get(0);
        return game.getId();
    }

    /**
     * addGame inserts a game record into the DB and makes the game voteable.
     * @param title name of the game
     * @return whether or not the game was successfully added
     */
    public boolean addGame(String title) {
        if (findGame(title) > 0) {
            //the game is already in the DB
            logger.debug("Game is already in the database");
            return false;
        }
        String sql = "insert into " + gameTable + " Values(default, ?, default, default)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            logger.error("Error preparing sql statement: " + e.getMessage());
            return false;
        }
        VotingService votingService = new VotingServiceImpl();
        votingService.makeVoteable(findGame(title));
        return true;
    }

    /**
     * addGame calls addGame() and then setOwned() to mark a game as owned or not.
     * @param title name of the game
     * @param owned if the game is owned; true=yes false=no
     * @return whether or not the game was successfully added
     */
    public boolean addGame(String title, boolean owned) {
        if (!addGame(title)) {
            //the game already exists in the db
            logger.debug("Game is already in the database");
            return false;
        }
        // else: game was inserted, now we will set it as owned
        return setOwned(title, owned);
    }

    /**
     * setOwned updates the Games table to specify if a game is/isn't owned. It covers
     * both functions
     * @param title name of the game
     * @param owned true=owned, false=not owned
     * @return whether or not the ownership changed successfully
     */
    public boolean setOwned(String title, boolean owned) {
        String sql = "update " + gameTable + " set owned = ? where title like ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setBoolean(1, owned);
            preparedStatement.setString(2, title);
            preparedStatement.executeUpdate();
            logger.debug("Set game as owned.");
        }
        catch (SQLException e) {
            logger.error("Error adding ownership to game " + e.getMessage());
            return false;
        }
        return true;
    }
}