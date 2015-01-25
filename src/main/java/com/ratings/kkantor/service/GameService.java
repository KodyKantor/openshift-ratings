package com.ratings.kkantor.service;

public interface GameService {
    public int findGame(String title);
    public boolean addGame(String title);
    public boolean addGame(String title, boolean owned);
    public boolean setOwned(int gameId, boolean owned);
    public boolean deleteGame(String title);
}
