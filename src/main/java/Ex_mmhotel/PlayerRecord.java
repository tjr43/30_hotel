package Ex_mmhotel;
//플레이어의 기록(메모)
public class PlayerRecord {
    private String playerId;
    private int floorReached; //노트를 작성하는 층
    private String memo;
    private String status;

    public PlayerRecord(String platerId, int floorReached, String memo, String status){
        this.playerId = platerId;
        this.floorReached = floorReached;
        this.memo = memo;
        this.status = status;
    }
    public String getPlayerId() {
        return playerId;
    }

    // memo 값에 접근하기 위한 getter 메소드
    public String getMemo() {
        return memo;
    }
}
