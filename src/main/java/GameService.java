
public interface GameService {
    public int findGame(String title);
    public boolean addGame(String title);
    public boolean addGame(String title, boolean owned);
    public boolean setOwned(String title, boolean owned);
}
