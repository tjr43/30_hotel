<%@ page import="Ex_30.GameState" %>
<%
    GameState gameState = (GameState) session.getAttribute("gameState");
    if (gameState == null) {
        response.sendRedirect("start.jsp");
        return;
    }
%>