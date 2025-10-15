<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.GameState" %>
<%@ page import="Ex_mmhotel.PlayerRecord" %>
<%@ page import="Ex_mmhotel.HtmlEscaper" %>
<%@ page import="java.util.List" %>
<%
    // 세션을 확인하고 gameState 변수를 가져옵니다.
    GameState gameState = (GameState) session.getAttribute("gameState");
    if (gameState == null) {
        response.sendRedirect("start.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 이전 기록</title>
    <link rel="stylesheet" href="css/page_style.css">
</head>
<body>
    <div class="container">
        <h1>이전 손님들의 기록</h1>
        <div class="memo-list">
            <%
                List<PlayerRecord> playerHistory = gameState.getPlayerHistory();
                if (playerHistory == null || playerHistory.isEmpty()) {
                    out.println("<p>--- 남겨진 기록이 없습니다. ---</p>");
                } else {
                    // 메모를 최신순으로 보여주기 위해 리스트를 역순으로 순회합니다.
                    for (int i = playerHistory.size() - 1; i >= 0; i--) {
                        PlayerRecord record = playerHistory.get(i);
                        if (record != null && record.getMemo() != null && !record.getMemo().trim().isEmpty()) {
                            out.println("<div class='memo-item'>");
                            out.println("    <span class='memo-player'>" + HtmlEscaper.escape(record.getPlayerId()) + "</span>");
                            out.println("    <p class='memo-content'>\"" + HtmlEscaper.escape(record.getMemo()) + "\"</p>");
                            out.println("</div>");
                        }
                    }
                }
            %>
        </div>
        <div class="button-group">
            <button onclick="window.location.href='game.jsp'">게임으로 돌아가기</button>
        </div>
    </div>
</body>
</html>

