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
    private static final int INSTANT_DEFEAT_FLOOR = 17;

    @Override
    public void init() throws ServletException {
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

        if (currentFloor == 1 || currentFloor == 7 || gameState.getGameFloors().get(currentFloor - 1).getTraps().isEmpty()) {
            request.setAttribute("message", "이 층에서는 정답을 제출할 수 없습니다.");
            request.getRequestDispatcher("game.jsp").forward(request, response);
            return;
        }

        Floor floor = gameState.getGameFloors().get(currentFloor - 1);
        if (userAnswer.equalsIgnoreCase(floor.getTraps().get(0).getAnswer())) {
            handleCorrectAnswer(request, response, gameState);
        } else {
            handleWrongAnswer(request, response, gameState);
        }
    }

    private void handleFloorChange(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        try {
            int newFloor = Integer.parseInt(request.getParameter("newFloor").trim());

            // ▼▼▼ 수정된 부분: 30층까지 이동할 수 있도록 범위를 명확하게 설정합니다. ▼▼▼
            if (newFloor < 1 || newFloor > 30) {
                // 오류 메시지도 30층 기준으로 고정합니다.
                request.setAttribute("message", "유효하지 않은 층입니다. 1층부터 30층 사이의 번호를 입력해주세요.");
                request.getRequestDispatcher("game.jsp").forward(request, response);
                return;
            }
            // ▲▲▲ 수정 종료 ▲▲▲

            Set<Integer> completedFloors = gameState.getCompletedFloorsByPlayer().getOrDefault(gameState.getCurrentPlayerId(), new HashSet<>());
            if (completedFloors.contains(newFloor)) {
                request.setAttribute("message", "이미 클리어한 층입니다. 다른 층으로 이동해주세요.");
                request.getRequestDispatcher("game.jsp").forward(request, response);
                return;
            }

            gameState.setCurrentFloor(newFloor);

            if (newFloor == INSTANT_DEFEAT_FLOOR) {
                request.setAttribute("trapMessage", "17층에 들어서자 불길한 기운이 당신을 덮칩니다...");
                request.getRequestDispatcher("game.jsp").forward(request, response);
                return;
            }

            request.getRequestDispatcher("game.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("message", "유효한 층 번호를 숫자로 입력하세요.");
            request.getRequestDispatcher("game.jsp").forward(request, response);
        }
    }

    private void handleCorrectAnswer(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        int currentFloor = gameState.getCurrentFloor();
        gameState.getCompletedFloorsByPlayer().computeIfAbsent(gameState.getCurrentPlayerId(), k -> new HashSet<>()).add(currentFloor);

        if (currentFloor == FINAL_FLOOR) {
            handleWin(request, response);
        } else {
            request.setAttribute("message", "정답입니다! 1층으로 돌아갑니다.");
            request.setAttribute("playReturnSound", true);
            gameState.setCurrentFloor(1);
            request.getRequestDispatcher("game.jsp").forward(request, response);
        }
    }

    private void handleWrongAnswer(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        gameState.setAttemptsLeft(gameState.getAttemptsLeft() - 1);

        if (gameState.getAttemptsLeft() > 0) {
            request.setAttribute("message", "틀렸습니다! 기회를 1회 잃고 1층으로 돌아갑니다. 남은 기회: " + gameState.getAttemptsLeft());
            request.setAttribute("playReturnSound", true);
            gameState.setCurrentFloor(1);
            request.getRequestDispatcher("game.jsp").forward(request, response);
        } else {
            handleGameOver(request, response, "기회를 모두 소진했습니다!");
        }
    }

    private void handleGameOver(HttpServletRequest request, HttpServletResponse response, String message) throws IOException, ServletException {
        request.setAttribute("message", message);
        request.getRequestDispatcher("gameover.jsp").forward(request, response);
    }

    private void handleWin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
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

