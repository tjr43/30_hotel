<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_30.GameState" %>
<%@ page import="Ex_30.Floor" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.HashSet" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목</title>
    <style>
        body { font-family: sans-serif; text-align: center; }
        .container { width: 80%; margin: 0 auto; }
        .display-area { border: 1px solid black; height: 300px; overflow-y: scroll; padding: 10px; text-align: left; }
    </style>
</head>
<body>
    <div class="container">
        <h1>호텔 면목</h1>

        <%
            // 세션에서 게임 상태 가져오기
            GameState gameState = (GameState) session.getAttribute("gameState");
            if (gameState == null) {
                response.sendRedirect("start.jsp");
                return;
            }

            int currentFloor = gameState.getCurrentFloor();
            String currentPlayerId = gameState.getCurrentPlayerId();
            int attemptsLeft = gameState.getAttemptsLeft();
        %>

        <p>현재 플레이어: <%= currentPlayerId %></p>
        <p>현재 층: <%= currentFloor %></p>
        <p>남은 기회: <%= attemptsLeft %></p>

        <div class="display-area">
            <%
                // 층 이동 또는 정답 제출 후 메시지 표시
                if (request.getAttribute("message") != null) {
                    out.println("<p>" + request.getAttribute("message") + "</p>");
                }

                // GameGUI.java의 promptForRiddle() 로직을 JSP에 구현
                if (currentFloor == 1) {
                    out.println("<p>--- 호텔에 오신 것을 환영합니다! ---</p>");
                    out.println("<p>현재 계신 곳은 1층 안내데스크입니다. 아래의 '규칙'을 눌러 규칙을 확인해 주세요.</p>");
                    out.println("<p>게임을 시작하려면 아래의 \"시작\" 버튼을 눌러주세요.</p>");
                } else {
                    List<Floor> gameFloors = gameState.getGameFloors();
                    Floor floor = gameFloors.get(currentFloor - 1);
                    out.println("<p>--- " + floor.getFloorNumber() + "층입니다. ---</p>");
                    out.println("<p>함정: " + floor.getTraps().get(0).getDescription() + "</p>");
                    out.println("<p>수수께끼: " + floor.getTraps().get(0).getRiddle() + "</p>");
                }
            %>
        </div>

        <br>

        <form action="GameServlet" method="post">
            <input type="hidden" name="action" value="submitAnswer">
            정답: <input type="text" name="answer" required>
            <button type="submit">정답 제출</button>
        </form>

        <br>

        <form action="GameServlet" method="post">
            <input type="hidden" name="action" value="changeFloor">
            층 이동: <input type="text" name="newFloor" required>
            <button type="submit">층 이동</button>
        </form>

        <br>

        <button onclick="window.location.href='memos.jsp'">메모 보기</button>
        <button onclick="window.location.href='rules.jsp'">규칙 보기</button>
        <form action="GameServlet" method="post" style="display:inline;">
            <input type="hidden" name="action" value="exitGame">
            <button type="submit">게임 종료</button>
        </form>
    </div>
</body>
</html>