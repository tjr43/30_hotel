<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.HtmlEscaper" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 실패</title>
    <link rel="stylesheet" href="css/page_style.css">
</head>
<!-- body 태그에 'ending-page' 클래스를 추가합니다. -->
<body class="ending-page">
    <div class="container">
        <h1>Game Over</h1>
        <p>
            <% if (request.getAttribute("message") != null) { %>
                <%= HtmlEscaper.escape((String)request.getAttribute("message")) %>
            <% } %>
        </p>
        <p>다음 손님을 위해 기록을 남겨주시겠어요?</p>
        <form action="SaveMemoServlet" method="post">
            <input type="hidden" name="status" value="fail">
            <textarea name="memo" rows="4" cols="50" placeholder="이곳에 기록을 남겨주세요..."></textarea>
            <div class="button-group">
                <button type="submit">기록 남기기</button>
            </div>
        </form>
         <div class="button-group">
            <button onclick="window.location.href='start.jsp'">다시 시작하기</button>
        </div>
    </div>
</body>
</html>

