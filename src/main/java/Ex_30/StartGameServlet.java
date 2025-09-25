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
import java.io.IOException;

@WebServlet("/StartGameServlet")
public class StartGameServlet extends HttpServlet {
    private static final String FILE_NAME = "invitation.json";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String playerName = request.getParameter("playerName").trim();
        HttpSession session = request.getSession();
        request.setCharacterEncoding("UTF-8");

        GameState gameState = loadGame();

        if (gameState.getAllPlayers().contains(playerName)) {
            response.getWriter().println("<script>alert('한번 방문하신 분은 재입장하실 수 없습니다.'); window.history.back();</script>");
            return;
        }

        gameState.getAllPlayers().add(playerName);
        gameState.setCurrentPlayerId(playerName);
        gameState.setCurrentFloor(1);
        gameState.setAttemptsLeft(2);

        session.setAttribute("gameState", gameState);

        response.sendRedirect("game.jsp");
    }

    private GameState loadGame() {
        String filePath = getServletContext().getRealPath("/") + FILE_NAME;
        File file = new File(filePath);
        Gson gson = new Gson();
        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                return gson.fromJson(reader, GameState.class);
            } catch (IOException e) {
                e.printStackTrace();
                return GameDataInitializer.createInitialState();
            }
        } else {
            return GameDataInitializer.createInitialState();
        }
    }
}