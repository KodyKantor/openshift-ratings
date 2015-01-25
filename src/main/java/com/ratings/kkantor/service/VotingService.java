package com.ratings.kkantor.service;

public interface VotingService {
    public boolean isRestrictedAccess();
    public int getVotes(int gameId);
    public int getVotes(String title);
    public int changeVote(boolean positive, int gameId);
    public int changeVote(boolean positive, String title);
    public boolean makeVoteable(int gameId);
    public boolean deleteVotes(int gameId);
}
