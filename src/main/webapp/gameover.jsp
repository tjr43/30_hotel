<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.HtmlEscaper" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>실패</title>
</head>
<body>
    <h1>게임 오버!</h1>
    <p>
        <%-- 서블릿에서 전달된 동적 게임오버 메시지를 표시합니다. --%>
        <% if (request.getAttribute("message") != null) { %>
            <%= HtmlEscaper.escape((String)request.getAttribute("message")) %>
            <br>
        <% } %>
        다음 플레이어를 위한 메모를 남겨주세요.
    </p>
    <form action="SaveMemoServlet" method="post">
        <input type="hidden" name="status" value="fail">
        <textarea name="memo" rows="4" cols="50" placeholder="메모를 입력하세요"></textarea>
        <br><br>
        <button type="submit">메모 저장</button>
    </form>
</body>
</html>
