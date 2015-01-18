import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Kody Kantor
 * GameServiceImpl is a DatabaseService that provides methods for users
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
        String sql = "select id from " + gameTable + " where title like ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                logger.debug("Game does not exist");
                return 0; //game title does not exist
            }
            return resultSet.getInt("id");
        } catch (SQLException e) {
            logger.error("Error preparing sql statement: " + e.getMessage());
            return 0;
        }
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