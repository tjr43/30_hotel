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

@WebServlet("/SaveMemoServlet")
public class SaveMemoServlet extends HttpServlet {
    // 데이터 저장 경로를 외부 경로로 변경
    private static final String SAVE_DIRECTORY = "C:/hotel_game_data/";
    private static final String FILE_NAME = "invitation.json";
    private static final int FINAL_FLOOR = 22;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        GameState gameState = (GameState) session.getAttribute("gameState");

        if (gameState == null) {
            response.sendRedirect("start.jsp");
            return;
        }

        String memo = request.getParameter("memo");
        String status = request.getParameter("status");

        int floorReached = (status.equals("success")) ? FINAL_FLOOR : gameState.getCurrentFloor();
        PlayerRecord newRecord = new PlayerRecord(gameState.getCurrentPlayerId(), floorReached, memo, status);
        gameState.getPlayerHistory().add(newRecord);

        saveGame(gameState);
        session.invalidate(); // 게임 종료 후 세션 무효화

        response.sendRedirect("goodbye.jsp");
    }

    private void saveGame(GameState gameState) {
        // 저장 디렉토리가 없으면 생성
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(SAVE_DIRECTORY + FILE_NAME)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
