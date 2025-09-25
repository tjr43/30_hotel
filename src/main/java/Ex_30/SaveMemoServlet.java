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
import java.io.FileWriter;
import java.io.IOException;

@WebServlet("/SaveMemoServlet")
public class SaveMemoServlet extends HttpServlet {
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
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(getServletContext().getRealPath("/") + FILE_NAME)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}