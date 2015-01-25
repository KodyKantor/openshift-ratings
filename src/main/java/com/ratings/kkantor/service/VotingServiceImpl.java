package com.ratings.kkantor.service;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.ratings.kkantor.common.Vote;

/**
 * @author Kody Kantor
 * com.ratings.kkantor.service.VotingServiceImpl is a com.ratings.kkantor.service.DatabaseService that provides methods for users
 * to vote on game titles. Convenience methods are provided where necessary
 * so users don't need to know gameIds to use the methods.
 *
 * If votes should increase/decrease by more than 1, change the voteIncrement/voteDecrement
 * values. This could allow special users to be given privileges that make
 * their votes 'worth' more.
 */
public class VotingServiceImpl extends DatabaseService implements VotingService {
    private static final Logger logger = LogManager.getLogger(VotingServiceImpl.class);

    /**
     * changeVote is a convenience method when the caller does not
     * know the gameId.
     *
     * @param positive whether or not the vote is an up-vote
     * @param title name of the game
     * @return vote count after update
     */
    public int changeVote(boolean positive, String title) {
        GameService gameService = new GameServiceImpl();
        int gameId = gameService.findGame(title);
        if (gameId < 1) {
            //the game doesn't exist
            logger.debug("Game does not exist");
            return 0;
        }
        return changeVote(positive, gameId);
    }

    /**
     * changeVote adds or subtracts from game ratings based on
     * the provided boolean. Addition/subtraction rate is defined
     * as an object variable.
     *
     * @param positive whether or not the vote is an up-vote
     * @param gameId numeric key of the game in the DB
     * @return vote count after update
     */
    public int changeVote(boolean positive, int gameId) {
        int voteDecrement = -1;
        int voteIncrement = 1;
        int voteVal = positive ? voteIncrement : voteDecrement;

        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Vote vote = (Vote) session.get(Vote.class, gameId);
            vote.setVotes(vote.getVotes() + voteVal);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error changing 'vote' value");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return 0;
        } finally {
            session.close();
        }
        return getVotes(gameId);
    }

    /**
     * isRestrictedAccess checks if the user is trying to change votes during restricted times.
     * @return whether or not the time is restricted
     */
    @Override
    public boolean isRestrictedAccess() {
        return false;
    }

    /**
     * getVotes is a convenience method when the caller does not know the gameId.
     * @param title name of the game
     * @return total vote count
     */
    @Override
    public int getVotes(String title) {
        GameService gameService = new GameServiceImpl();
        int gameId = gameService.findGame(title);
        if (gameId < 1) {
            //games that don't exist have 0 votes
            logger.debug("Game does not exist");
            return 0;
        }
        return getVotes(gameId);
    }

    /**
     * getVotes finds the number of votes the game has.
     * @param gameId id associated with a game
     * @return total vote count
     */
    public int getVotes(int gameId) {
        Session session = getSessionFactory().openSession();
        Vote vote = (Vote) session.get(Vote.class, gameId);
        Integer voteCount = vote.getVotes();
        session.close();
        return voteCount; //autoboxing
    }

    /**
     * makeVoteable adds a line in the DB for the newly added title. The DB currently
     * automatically sets the 'votes' value to 1 (one) when an item is added.
     * @param gameId id associated with a game
     * @return whether or not the game was successfully added
     */
    public boolean makeVoteable(int gameId) {
        int startingVotes = 1;
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Vote vote = new Vote(gameId, startingVotes);
            session.save(vote);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error making game voteable");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        logger.debug("Made game voteable");
        return true;
    }

    public boolean deleteVotes(int gameId) {
        Session session = getSessionFactory().openSession();
        Transaction tx = null;
        try {
            tx = session.beginTransaction();
            Vote vote = (Vote) session.get(Vote.class, gameId);
            session.delete(vote);
            tx.commit();
        } catch (HibernateException e) {
            logger.error("Error deleting votes");
            if (tx != null) {
                tx.rollback(); //undo the unfinished transaction
            }
            e.printStackTrace();
            return false;
        } finally {
            session.close();
        }
        logger.debug("Deleted votes");
        return true;
    }
}
