package Ex_30;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// 게임 처음 시작 시 초기 데이터 생성
public class GameDataInitializer {
    private static final List<Trap> ALL_TRAPS = Arrays.asList(
            new Trap("숫자 퍼즐", "2, 4, 6, 8... 다음 숫자는?", "10"),
            new Trap("단어 퍼즐", "개구리가 가장 좋아하는 음식은?", "파리"),
            new Trap("논리 퍼즐", "손은 있지만 잡을 수 없다.", "그림자"),
            new Trap("시간 퍼즐", "오늘의 절반, 내일의 절반은?", "밤"),
            new Trap("단어 퍼즐", "늘 다른 곳을 보지만, 늘 같은 곳에 있는 것은?", "두 눈"),
            new Trap("수수께끼", "입은 있지만 말은 못하고, 몸은 있지만 움직이지 못하는 것은?", "책"),
            new Trap("수학 퍼즐", "4개의 9로 100을 만들어라.", "99+9/9"),
            new Trap("방향 퍼즐", "나는 늘 위를 보지만 늘 내려간다.", "비"),
            new Trap("생각 퍼즐", "나는 늘 너와 함께 있지만, 네가 나를 잡으려 하면 사라진다.", "그림자")
    );
    // 게임이 처음 시작될 때의 상태
    public static GameState createInitialState(){
        Floor floor1 = new Floor(1, new ArrayList<>());

        List<Floor> quizFloors = new ArrayList<>();
        for (int i = 2; i <= 30; i++){
            Collections.shuffle(ALL_TRAPS, new Random());
            List<Trap> uniqieTrap = Arrays.asList(ALL_TRAPS.get(0));
            quizFloors.add(new Floor(i, uniqieTrap));
        }

        List<Floor> gameFloors = new ArrayList<>();
        gameFloors.add(floor1);
        gameFloors.addAll(quizFloors);

        return new GameState(1, null, new ArrayList<>(), gameFloors, new ArrayList<>(), new ArrayList<>(), 2, new ArrayList<>());
    }
}
