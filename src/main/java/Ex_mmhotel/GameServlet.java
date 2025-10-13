package Ex_mmhotel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
    private static final String SAVE_DIRECTORY = System.getProperty("user.home") + File.separator + ".hotel_game_data";
    private static final String FILE_NAME = "invitation.json";
    private static final int FINAL_FLOOR = 22;
    private static final int SPECIAL_TICKET_FLOOR = 7;
    private static final int INSTANT_DEFEAT_FLOOR = 17;

    @Override
    public void init() throws ServletException {
        // 애플리케이션 시작 시 데이터 저장 폴더 생성
        try {
            Path path = Paths.get(SAVE_DIRECTORY);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new ServletException("Failed to create save directory", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        GameState gameState = (GameState) session.getAttribute("gameState");
        request.setCharacterEncoding("UTF-8");

        if (gameState == null) {
            response.sendRedirect("start.jsp");
            return;
        }

        String action = request.getParameter("action");
        if (action != null) {
            switch (action) {
                case "submitAnswer":
                    handleSubmit(request, response, gameState);
                    break;
                case "changeFloor":
                    handleFloorChange(request, response, gameState);
                    break;
                case "exitGame":
                    handleExit(request, response, gameState);
                    break;
            }
        }
    }

    private void handleSubmit(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        String userAnswer = request.getParameter("answer").trim();
        int currentFloor = gameState.getCurrentFloor();

        // 1층에서는 정답 제출을 처리하지 않음
        if (currentFloor == 1 || gameState.getGameFloors().get(currentFloor - 1).getTraps().isEmpty()) {
            request.setAttribute("message", "이 층에는 수수께끼가 없습니다.");
            request.getRequestDispatcher("game.jsp").forward(request, response);
            return;
        }

        Floor floor = gameState.getGameFloors().get(currentFloor - 1);
        if (userAnswer.equalsIgnoreCase(floor.getTraps().get(0).getAnswer())) {
            gameState.getCompletedFloorsByPlayer().computeIfAbsent(gameState.getCurrentPlayerId(), k -> new HashSet<>()).add(currentFloor);

            if (currentFloor == FINAL_FLOOR) {
                handleWin(request, response, gameState);
            } else {
                request.setAttribute("message", "정답입니다! 다음 층으로 이동하세요.");
                request.getRequestDispatcher("game.jsp").forward(request, response);
            }
        } else {
            handleWrongAnswer(request, response, gameState);
        }
    }

    private void handleFloorChange(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        try {
            int newFloor = Integer.parseInt(request.getParameter("newFloor").trim());
            int totalFloors = gameState.getGameFloors().size();

            if (newFloor < 1 || newFloor > totalFloors) {
                request.setAttribute("message", "유효하지 않은 층 번호입니다. 1에서 " + totalFloors + " 사이의 숫자를 입력하세요.");
                request.getRequestDispatcher("game.jsp").forward(request, response);
                return;
            }

            Set<Integer> completedFloors = gameState.getCompletedFloorsByPlayer().getOrDefault(gameState.getCurrentPlayerId(), new HashSet<>());
            if (newFloor != 1 && completedFloors.contains(newFloor)) {
                request.setAttribute("message", "이 층은 이미 통과했습니다.");
                request.getRequestDispatcher("game.jsp").forward(request, response);
                return;
            }

            gameState.setCurrentFloor(newFloor);

            if (newFloor == SPECIAL_TICKET_FLOOR) {
                if (gameState.getAttemptsLeft() == 2) {
                    gameState.setAttemptsLeft(1);
                    request.setAttribute("message", "함정에 걸려 기회를 1개 잃습니다. (남은 기회: 1)");
                } else {
                    gameState.setAttemptsLeft(2);
                    request.setAttribute("message", "티켓을 획득하여 남은 기회가 2개가 됩니다. (남은 기회: 2)");
                }
            } else if (newFloor == INSTANT_DEFEAT_FLOOR) {
                handleGameOver(request, response, gameState, "함정에 걸려 즉시 탈락합니다!");
                return;
            } else {
                request.setAttribute("message", "플레이어가 " + newFloor + "층으로 이동했습니다.");
            }
            request.getRequestDispatcher("game.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("message", "유효한 층 번호를 숫자로 입력하세요.");
            request.getRequestDispatcher("game.jsp").forward(request, response);
        }
    }

    // ⭐️ 수정된 메소드: 기회 차감 로직을 명확하게 변경했습니다.
    private void handleWrongAnswer(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        int attempts = gameState.getAttemptsLeft();
        if (attempts > 1) {
            // 기회가 2개 이상 남았을 때: 1개 차감하고 1층으로
            gameState.setAttemptsLeft(attempts - 1);
            gameState.setCurrentFloor(1);
            request.setAttribute("message", "틀렸습니다! 기회를 1회 잃고 1층으로 돌아갑니다. 남은 기회: " + gameState.getAttemptsLeft());
            request.getRequestDispatcher("game.jsp").forward(request, response);
        } else {
            // 기회가 1개 남았을 때: 0으로 만들고 게임 오버
            gameState.setAttemptsLeft(0);
            handleGameOver(request, response, gameState, "기회를 모두 소진했습니다! 게임 오버!");
        }
    }

    private void handleGameOver(HttpServletRequest request, HttpServletResponse response, GameState gameState, String message) throws IOException, ServletException {
        request.setAttribute("message", message);
        request.getRequestDispatcher("gameover.jsp").forward(request, response);
    }

    private void handleWin(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        request.getRequestDispatcher("win.jsp").forward(request, response);
    }

    private void handleExit(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException {
        saveGame(gameState);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect("goodbye.jsp");
    }

    private void saveGame(GameState gameState) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(SAVE_DIRECTORY, FILE_NAME);
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

