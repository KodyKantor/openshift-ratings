package com.ratings.kkantor.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import com.ratings.kkantor.common.Game;

/**
 * @author Kody Kantor
 * com.ratings.kkantor.service.GameServiceImpl is a com.ratings.kkantor.service.DatabaseService
 * that provides methods for users
 * to add games to the DB, and set them as owned/not owned.
 *
 * TODO add delete functionality
 */
public class GameServiceImpl extends DatabaseService implements GameService {
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
        //if there are multiple games with the same title
        // (there shouldn't be), we will just return the first
        // in the list that was returned.
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
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        Integer gameId;
        try {
            tx = session.beginTransaction();
            Game game = new Game();
            game.setTitle(title);
            gameId = (Integer) session.save(game);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error adding game to database");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        logger.debug("Added game to database");
        VotingService votingService = new VotingServiceImpl();
        votingService.makeVoteable(gameId); //autoboxing
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
        return setOwned(findGame(title), owned);
    }

    /**
     * setOwned updates the Games table to specify if a game is/isn't owned. It covers
     * both functions
     * @param gameId unique identifier of the game
     * @param owned true=owned, false=not owned
     * @return whether or not the ownership changed successfully
     */
    public boolean setOwned(int gameId, boolean owned) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Game game = (Game) session.get(Game.class, gameId);
            game.setOwned(owned);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error changing 'owned' value");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        logger.debug("Changed owned value");
        return true;
    }

    /**
     * deleteGame deletes a game from the database
     *
     * @param title name of the game
     * @return whether or not game was deleted successfully
     */
    public boolean deleteGame(String title) {
        int gameId = findGame(title);
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Game game = (Game) session.get(Game.class, gameId);
            session.delete(game);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error deleting game");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        logger.debug("Deleted game");
        VotingService votingService = new VotingServiceImpl();
        votingService.deleteVotes(gameId);
        return true;
    }
}
