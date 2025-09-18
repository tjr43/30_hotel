package Ex_30;

import java.util.List;
//각 층에 대한 정보 담기

public class Floor {
    private int floorNumber; //층 정수저장
    private List<Trap> traps;

    public Floor(int floorNumber, List<Trap> traps) {
        this.floorNumber = floorNumber;
        this.traps  = traps;
    }

    public int getFloorNumber(){return floorNumber;}
    public List<Trap> getTraps(){return traps;}

}
