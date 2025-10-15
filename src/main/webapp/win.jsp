<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 탈출 성공</title>
    <!-- 게임오버, 체크아웃 페이지와 동일한 스타일을 적용합니다. -->
    <link rel="stylesheet" href="css/page_style.css">
</head>
<!-- body에 end-screen 클래스를 추가하여 Endingdoor.jpg 배경을 적용합니다. -->
<body class="end-screen">
    <div class="container">
        <h1>탈출 성공!</h1>
        <p>축하합니다! 당신은 이 기묘한 호텔에서 무사히 빠져나왔습니다.<br>다음에 찾아올 손님을 위해 기록을 남겨주시겠습니까?</p>

        <form action="SaveMemoServlet" method="post">
            <input type="hidden" name="status" value="success">
            <textarea name="memo" rows="4" cols="50" placeholder="이곳에 기록을 남기세요..."></textarea>
            <div class="button-group">
                <button type="submit">기록 남기기</button>
            </div>
        </form>
    </div>
</body>
</html>
