package Ex_30;
import java.util.List;
import java.util.ArrayList;

public class GameState {
    private int currentFloor;
    private String currentPlayerId;
    private List<PlayerRecord> playerHistory;
    private List<Floor> gameFloors;
    private List<String> eliminatedPlayers;
    private List<String> completedPlayers;
    private int attemptsLeft;
    private List<String> allPlayers;

    public GameState(int currentFloor, String currentPlayerId, List<PlayerRecord> playerHistory, List<Floor> gameFloors, List<String> eliminatedPlayers, List<String> completedPlayers, int attemptsLeft, List<String> allPlayers) {
        this.currentFloor = currentFloor;
        this.currentPlayerId = currentPlayerId;
        this.playerHistory = playerHistory;
        this.gameFloors = gameFloors;
        this.eliminatedPlayers = eliminatedPlayers;
        this.completedPlayers = completedPlayers;
        this.attemptsLeft = attemptsLeft;
        this.allPlayers = allPlayers;
    }

    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; }

    public String getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(String currentPlayerId) { this.currentPlayerId = currentPlayerId; }

    public List<PlayerRecord> getPlayerHistory() { return playerHistory; }
    public List<String> getEliminatedPlayers() { return eliminatedPlayers; }
    public List<String> getCompletedPlayers() { return completedPlayers; }
    public List<Floor> getGameFloors() { return gameFloors; }

    public int getAttemptsLeft() { return attemptsLeft; }
    public void setAttemptsLeft(int attemptsLeft) { this.attemptsLeft = attemptsLeft; }
    public List<String> getAllPlayers() { return allPlayers; }
}
