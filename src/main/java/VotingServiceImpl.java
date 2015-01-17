import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Kody Kantor
 * VotingServiceImpl is a DatabaseService that provides methods for users
 * to vote on game titles. Convenience methods are provided where necessary
 * so users don't need to know gameIds to use the methods.
 *
 * If votes should increase/decrease by more than 1, change the voteIncrement/voteDecrement
 * values. This could allow special users to be given privileges that make
 * their votes 'worth' more.
 */
public class VotingServiceImpl extends DatabaseService implements VotingService {
    private static String voteTable = "Votes";
    private Connection conn = getConnection();
    private static GameService gameService = new GameServiceImpl();
    private int voteIncrement = 1;
    private int voteDecrement = -1;

    /**
     * changeVote is a convenience method when the caller does not
     * know the gameId.
     *
     * @param positive whether or not the vote is an up-vote
     * @param title name of the game
     * @return vote count after update
     */
    public int changeVote(boolean positive, String title) {
        int gameId = gameService.findGame(title);
        if (gameId < 1) {
            //the game doesn't exist
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
        int voteVal = positive ? voteIncrement : voteDecrement;
        String sql = "update " + voteTable + " set votes = votes + ? where gameId = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, voteVal);
            preparedStatement.setInt(2, gameId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error adding a vote for title: " + e.getMessage());
            return 0;
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
        int gameId = gameService.findGame(title);
        if (gameId < 1) {
            //games that don't exist have 0 votes
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
        String sql = "select votes from " + voteTable + " where gameId = ?";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, gameId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("No votes were found.");
                return 0;
            }
            return resultSet.getInt("votes");
        }
        catch (SQLException e) {
            System.out.println("Error getting votes for title: " + e.getMessage());
            return 0;
        }
    }

    /**
     * makeVoteable adds a line in the DB for the newly added title. The DB currently
     * automatically sets the 'votes' value to 1 (one) when an item is added.
     * @param gameId id associated with a game
     * @return whether or not the game was successfully added
     */
    public boolean makeVoteable(int gameId) {
        String sql = "insert into " + voteTable + " Values(?, default)";
        try {
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, gameId);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println("Error making title voteable: " + e.getMessage());
            return false;
        }
        return true;
    }
}