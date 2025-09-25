package Ex_30;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Optional;

@WebServlet("/GameServlet")
public class GameServlet extends HttpServlet {
    private static final String FILE_NAME = "invitation.json";
    private static final int FINAL_FLOOR = 22;
    private static final int SPECIAL_TICKET_FLOOR = 7;
    private static final int INSTANT_DEFEAT_FLOOR = 17;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        GameState gameState = (GameState) session.getAttribute("gameState");
        request.setCharacterEncoding("UTF-8");

        if (gameState == null) {
            response.sendRedirect("start.jsp");
            return;
        }

        String action = request.getParameter("action");
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

    private void handleSubmit(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        String userAnswer = request.getParameter("answer").trim();
        int currentFloor = gameState.getCurrentFloor();
        Floor floor = gameState.getGameFloors().get(currentFloor - 1);

        if (userAnswer.equalsIgnoreCase(floor.getTraps().get(0).getAnswer())) {
            gameState.getCompletedFloorsByPlayer().putIfAbsent(gameState.getCurrentPlayerId(), new HashSet<>());
            gameState.getCompletedFloorsByPlayer().get(gameState.getCurrentPlayerId()).add(currentFloor);
            request.setAttribute("message", "정답입니다! 다음 층으로 이동하세요.");

            if (currentFloor == FINAL_FLOOR) {
                handleWin(request, response, gameState);
            } else {
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
                    request.setAttribute("message", "함정에 걸려 기회를 1개 잃습니다. (남은 기회: " + gameState.getAttemptsLeft() + ")");
                } else {
                    gameState.setAttemptsLeft(2);
                    request.setAttribute("message", "티켓을 획득하여 남은 기회가 2개가 됩니다. (남은 기회: " + gameState.getAttemptsLeft() + ")");
                }
            } else if (newFloor == INSTANT_DEFEAT_FLOOR) {
                request.setAttribute("message", "플레이어가 " + newFloor + "층으로 이동했습니다. 함정에 걸려 즉시 탈락합니다!");
                handleGameOver(request, response, gameState);
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

    private void handleWrongAnswer(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        gameState.setAttemptsLeft(gameState.getAttemptsLeft() - 1);
        if (gameState.getAttemptsLeft() > 0) {
            gameState.setCurrentFloor(1);
            request.setAttribute("message", "틀렸습니다! 기회를 1회 잃고 1층으로 돌아갑니다. 남은 기회: " + gameState.getAttemptsLeft());
            request.getRequestDispatcher("game.jsp").forward(request, response);
        } else {
            request.setAttribute("message", "기회를 모두 소진했습니다! 게임 오버!");
            handleGameOver(request, response, gameState);
        }
    }

    private void handleGameOver(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        // You would redirect to a new JSP page to handle the memo input here.
        // For this example, let's just show a simple alert and redirect.
        response.getWriter().println("<script>alert('게임 오버! 메모를 남겨주세요.'); window.location.href='gameover.jsp';</script>");
    }

    private void handleWin(HttpServletRequest request, HttpServletResponse response, GameState gameState) throws IOException, ServletException {
        // You would redirect to a new JSP page to handle the memo input here.
        response.getWriter().println("<script>alert('축하합니다! 방 탈출 성공! 메모를 남겨주세요.'); window.location.href='win.jsp';</script>");
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
        try (FileWriter writer = new FileWriter(getServletContext().getRealPath("/") + FILE_NAME)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}