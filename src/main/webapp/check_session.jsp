<%@ page import="Ex_mmhotel.GameState" %>
<%
    GameState gameState = (GameState) session.getAttribute("gameState");
    if (gameState == null) {
        response.sendRedirect("start.jsp");
        return;
    }
%>
