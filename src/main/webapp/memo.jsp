<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_30.GameState" %>
<%@ page import="Ex_30.PlayerRecord" %>
<%@ page import="java.util.List" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>이전 플레이어의 메모</title>
</head>
<body>
    <h1>이전 플레이어의 메모</h1>
    <%
        GameState gameState = (GameState) session.getAttribute("gameState");
        if (gameState == null) {
            response.sendRedirect("start.jsp");
            return;
        }
        List<PlayerRecord> playerHistory = gameState.getPlayerHistory();
        if (playerHistory.isEmpty()) {
            out.println("<p>--- 이전 플레이어의 메모가 없습니다. ---</p>");
        } else {
            for (PlayerRecord record : playerHistory) {
                out.println("<p><strong>플레이어 " + record.getPlayerId() + "</strong>: " + record.getMemo() + "</p>");
            }
        }
    %>
    <br>
    <button onclick="window.location.href='game.jsp'">게임으로 돌아가기</button>
</body>
</html>