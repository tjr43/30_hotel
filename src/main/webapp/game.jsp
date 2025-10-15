<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.Floor" %>
<%@ page import="Ex_mmhotel.HtmlEscaper" %>
<%@ page import="java.util.List" %>
<%@ include file="check_session.jsp" %>
<%
    int currentFloor = gameState.getCurrentFloor();
    String currentPlayerId = gameState.getCurrentPlayerId();
    int attemptsLeft = gameState.getAttemptsLeft();
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목</title>
    <link rel="stylesheet" href="css/hotel_style.css">
</head>
<body>
    <div class="container">
        <h1>Hotel Myunmok</h1>

        <div class="game-info">
            <p><strong>GUEST</strong><br><%= HtmlEscaper.escape(currentPlayerId) %></p>
            <%-- 현재 층 정보는 제거 --%>
            <p><strong>KEYS LEFT</strong><br><%= attemptsLeft %></p>
        </div>

        <div class="quiz-box">
            <%
                if (request.getAttribute("message") != null) {
                    // "안내데스크"를 "방송"으로 변경
                    out.println("<p><strong>[방송]</strong> " + HtmlEscaper.escape((String)request.getAttribute("message")) + "</p>");
                }
                if (currentFloor == 1) {
                    out.println("<p>호텔 면목의 로비에 오신 것을 환영합니다.</p>");
                    out.println("<p>이곳에서는 기묘한 일들이 벌어지곤 합니다... 부디, 조심하십시오.</p>");
                } else {
                    List<Floor> gameFloors = gameState.getGameFloors();
                    Floor floor = gameFloors.get(currentFloor - 1);
                    out.println("<p><strong>[" + floor.getFloorNumber() + "호실]</strong></p>");
                    out.println("<p><i>" + HtmlEscaper.escape(floor.getTraps().get(0).getDescription()) + "</i></p>");
                    out.println("<p>" + HtmlEscaper.escape(floor.getTraps().get(0).getRiddle()) + "</p>");
                }
            %>
        </div>
        <form action="GameServlet" method="post" class="form-group">
            <input type="hidden" name="action" value="changeFloor">
            <label for="newFloorInput">층 이동:</label>
            <input type="text" id="newFloorInput" name="newFloor" placeholder="이동할 층 번호..." required>
            <button type="submit">이동</button>
        </form>
        <form action="GameServlet" method="post" class="form-group">
            <input type="hidden" name="action" value="submitAnswer">
             <label for="answerInput">정답:</label>
            <input type="text" id="answerInput" name="answer" placeholder="정답 입력..." required>
            <button type="submit">제출</button>
        </form>
        <div class="button-group">
            <button onclick="window.location.href='memos.jsp'">메모 보기</button>
            <button onclick="window.location.href='rules.jsp'">규칙 보기</button>
            <form action="GameServlet" method="post" style="display:inline;">
                <input type="hidden" name="action" value="exitGame">
                <button type="submit">체크아웃</button>
            </form>
        </div>
    </div>
</body>
</html>

