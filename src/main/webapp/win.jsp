<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>게임 승리</title>
</head>
<body>
    <h1>탈출 성공!</h1>
    <p>축하합니다! 다음 플레이어를 위한 메모를 남겨주세요.</p>
    <form action="SaveMemoServlet" method="post">
        <input type="hidden" name="status" value="success">
        <textarea name="memo" rows="4" cols="50" placeholder="메모를 입력하세요"></textarea>
        <br><br>
        <button type="submit">메모 저장</button>
    </form>
</body>
</html>