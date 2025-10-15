<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 입장</title>
    <link rel="stylesheet" href="css/start_style.css">
</head>
<body>
    <div class="start-container">
        <h1>Hotel Myunmok</h1>
        <p>호텔에 입장하시려면 성함을 알려주십시오.</p>
        <form action="StartGameServlet" method="post">
            <input type="text" name="playerName" placeholder="이름 입력..." required>
            <button type="submit">입장하기</button>
        </form>
    </div>
</body>
</html>

