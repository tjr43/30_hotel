package Ex_mmhotel;
import java.util.List;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class GameState {
    private int currentFloor;
    private String currentPlayerId;
    private List<PlayerRecord> playerHistory;
    private List<Floor> gameFloors;
    private List<String> eliminatedPlayers;
    private List<String> completedPlayers;
    private int attemptsLeft;
    private List<String> allPlayers;
    private Map<String, Set<Integer>> completedFloorsByPlayer;


    public GameState(int currentFloor, String currentPlayerId, List<PlayerRecord> playerHistory, List<Floor> gameFloors, List<String> eliminatedPlayers, List<String> completedPlayers, int attemptsLeft, List<String> allPlayers) {
        this.currentFloor = currentFloor;
        this.currentPlayerId = currentPlayerId;
        this.playerHistory = playerHistory;
        this.gameFloors = gameFloors;
        this.eliminatedPlayers = eliminatedPlayers;
        this.completedPlayers = completedPlayers;
        this.attemptsLeft = attemptsLeft;
        this.allPlayers = allPlayers;
        this.completedFloorsByPlayer = new HashMap<>();
    }

    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int currentFloor) { this.currentFloor = currentFloor; } // setter 추가

    public String getCurrentPlayerId() { return currentPlayerId; }
    public void setCurrentPlayerId(String currentPlayerId) { this.currentPlayerId = currentPlayerId; } // setter 추가

    public List<PlayerRecord> getPlayerHistory() { return playerHistory; }
    public List<String> getEliminatedPlayers() { return eliminatedPlayers; }
    public List<String> getCompletedPlayers() { return completedPlayers; }
    public List<Floor> getGameFloors() { return gameFloors; }

    public int getAttemptsLeft() { return attemptsLeft; }
    public void setAttemptsLeft(int attemptsLeft) { this.attemptsLeft = attemptsLeft; }
    public List<String> getAllPlayers() { return allPlayers; }

    public Map<String, Set<Integer>> getCompletedFloorsByPlayer() {
        if (completedFloorsByPlayer == null) {
            completedFloorsByPlayer = new HashMap<>();
        }
        return completedFloorsByPlayer;
    }
}
