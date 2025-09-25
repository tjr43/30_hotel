<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 게임 시작</title>
</head>
<body>
    <h1>호텔 면목에 오신 것을 환영합니다</h1>
    <form action="StartGameServlet" method="post">
        <p>참가할 플레이어의 이름을 입력하세요:</p>
        <input type="text" name="playerName" required>
        <button type="submit">게임 시작</button>
    </form>
</body>
</html>