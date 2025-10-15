<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.GameState" %>
<%
    // 세션을 확인합니다. gameState이 없어도 규칙은 볼 수 있게 하되,
    // 돌아가기 버튼의 동작을 다르게 할 수 있습니다. (여기서는 간단히 리디렉션)
    GameState gameState = (GameState) session.getAttribute("gameState");
    if (gameState == null) {
        response.sendRedirect("start.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목 - 규칙</title>
    <link rel="stylesheet" href="css/page_style.css">
</head>
<body>
    <div class="container">
        <h1>게임 규칙</h1>
        <div class="rules-content">
1. 게임 구조
1.1. 이 호텔은 총 30층으로 구성되어 있습니다.
   - 1층: 안내 데스크
   - 2~30층: 랜덤 퀴즈/이벤트 층
1.2. 플레이어는 층 번호를 직접 입력하여 원하는 층으로 이동할 수 있습니다.

2. 클리어 및 탈락 조건
2.1. 클리어 조건: 1층을 제외한 특정 층에서 퀴즈의 정답을 맞추면 클리어입니다.
2.2. 실패 시: 클리어 층이 아닌 곳에서 정답을 맞추면 1층 안내 데스크로 돌아갑니다.
2.3. 첫 탈출자 혜택: 제작자에게 연락하면 커피 쿠폰을 드립니다.

3. 플레이어 기회
3.1. 모든 플레이어는 2번의 기본 기회를 가지고 있습니다.
3.2. 문제를 틀리면 1층 안내 데스크로 돌아가고 기회가 소진됩니다.
3.3. 필요 시 초대장(파일)을 받은 플레이어가 게임을 이어갈 수 있습니다.

4. 메모 시스템
4.1. 플레이어는 게임 클리어 또는 실패 후 메모를 남길 수 있습니다.
4.2. 메모는 들어온 순서대로 기록됩니다.
4.3. 메모의 내용은 자유로우며, 거짓말을 적어도 상관없습니다.
4.4. 메모 작성은 선택 사항입니다.

5. 게임 진행
5.1. 게임을 클리어 해도 게임은 멈추지 않고 계속 진행됩니다.
5.2. 게임을 클러어 하지 못해도 게임은 멈추지 않고 계속 진행됩니다.
5.3. 게임은 마지막에 초대장을 받은 사람이 다음 사람에게 전송하지 않으면 종료됩니다.
        </div>
        <div class="button-group">
            <button onclick="window.location.href='game.jsp'">게임으로 돌아가기</button>
        </div>
    </div>
</body>
</html>

