package Ex_mmhotel;

import com.google.gson.Gson;
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
    // SaveMemoServlet과 동일한 안전한 사용자 홈 폴더 경로를 사용합니다.
    private static final String SAVE_DIRECTORY = System.getProperty("user.home") + File.separator + ".hotel_game_data";
    private static final String FILE_NAME = "invitation.json";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String playerName = request.getParameter("playerName").trim();
        HttpSession session = request.getSession();

        GameState gameState = loadGame();

        // 이미 플레이한 적 있는 이름인지 확인합니다.
        if (gameState.getAllPlayers().contains(playerName)) {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().println("<script>alert('한번 방문하신 분은 재입장하실 수 없습니다.'); window.history.back();</script>");
            return;
        }

        // 새로운 플레이어 정보를 게임 상태에 추가합니다.
        gameState.getAllPlayers().add(playerName);
        gameState.setCurrentPlayerId(playerName);
        gameState.setCurrentFloor(1);
        gameState.setAttemptsLeft(2);

        // 세션에 게임 상태를 저장합니다.
        session.setAttribute("gameState", gameState);
        // 첫 방문 환영 메시지를 띄우기 위한 플래그를 세션에 저장합니다.
        session.setAttribute("isFirstVisit", true);

        // 메인 게임 페이지로 이동시킵니다.
        response.sendRedirect("game.jsp");
    }

    /**
     * 파일에서 기존 게임 데이터를 불러옵니다. 파일이 없으면 새로운 게임을 시작합니다.
     * @return 불러오거나 새로 생성한 GameState 객체
     */
    private GameState loadGame() {
        // 데이터 저장 폴더가 없으면 생성합니다.
        File directory = new File(SAVE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, FILE_NAME);
        Gson gson = new Gson();

        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                GameState gameState = gson.fromJson(reader, GameState.class);
                // 파일이 손상되었거나 내용이 비어있을 경우를 대비합니다.
                if (gameState != null) {
                    return gameState;
                }
                return GameDataInitializer.createInitialState();
            } catch (IOException e) {
                e.printStackTrace(); // 오류 로그 출력
                return GameDataInitializer.createInitialState();
            }
        } else {
            // 저장된 파일이 없으면 완전히 새로운 게임 상태를 생성합니다.
            return GameDataInitializer.createInitialState();
        }
    }
}

