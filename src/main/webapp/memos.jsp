<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_30.PlayerRecord" %>
<%@ page import="Ex_30.HtmlEscaper" %>
<%@ page import="java.util.List" %>
<%@ include file="check_session.jsp" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>이전 플레이어의 메모</title>
</head>
<body>
    <h1>이전 플레이어의 메모</h1>
    <%
        List<PlayerRecord> playerHistory = gameState.getPlayerHistory();
        if (playerHistory.isEmpty()) {
            out.println("<p>--- 이전 플레이어의 메모가 없습니다. ---</p>");
        } else {
            for (PlayerRecord record : playerHistory) {
                out.println("<p><strong>플레이어 " + HtmlEscaper.escape(record.getPlayerId()) + "</strong>: " + HtmlEscaper.escape(record.getMemo()) + "</p>");
            }
        }
    %>
    <br>
    <button onclick="window.location.href='game.jsp'">게임으로 돌아가기</button>
</body>
</html>