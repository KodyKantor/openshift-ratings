package com.ratings.kkantor.common;

public class Vote {
    private int gameId;
    private int votes;

    public Vote() {}
    public Vote(int gameId, int votes) {
        this.votes = votes;
        this.gameId = gameId;
    }

    public int getVotes() {
        return votes;
    }
    public void setVotes(int votes) {
        this.votes = votes;
    }
    public int getGameId() {
        return gameId;
    }
    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
}
