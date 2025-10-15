<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Ex_mmhotel.GameState" %>
<%@ page import="Ex_mmhotel.Floor" %>
<%@ page import="Ex_mmhotel.HtmlEscaper" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Collections" %>
<%
    // --- 페이지의 모든 로직을 이 한 곳에서 처리합니다. ---
    GameState gameState = (GameState) session.getAttribute("gameState");
    if (gameState == null) {
        response.sendRedirect("start.jsp");
        return;
    }

    int currentFloor = gameState.getCurrentFloor();
    String currentPlayerId = gameState.getCurrentPlayerId();
    int attemptsLeft = gameState.getAttemptsLeft();
    boolean isCurrentFloorCleared = gameState.getCompletedFloorsByPlayer()
                                             .getOrDefault(currentPlayerId, Collections.emptySet())
                                             .contains(currentFloor);

    boolean isFirstVisit = session.getAttribute("isFirstVisit") != null && (Boolean)session.getAttribute("isFirstVisit");
    if(isFirstVisit) {
        session.setAttribute("isFirstVisit", false);
    }

    // 함정 메시지나 1층 복귀 사운드 신호를 받습니다.
    String trapMessage = (String) request.getAttribute("trapMessage");
    Boolean playReturnSound = (Boolean) request.getAttribute("playReturnSound");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>호텔 면목</title>
    <link rel="stylesheet" href="css/hotel_style.css">
</head>
<!-- 1층일 경우와 함정 메시지가 있을 경우 body에 다른 클래스를 부여합니다. -->
<body class="<%= (trapMessage != null) ? "trap-active" : (currentFloor == 1 ? "floor-1" : "") %>">

    <div class="elevator-overlay" id="elevatorOverlay">
        <div class="elevator-indicator"></div>
        <p id="overlayMessage">Moving...</p>
    </div>

    <%
        // ★★★ 버그 수정: return; 대신, 함정 메시지가 있을 때는 게임 컨테이너를 아예 그리지 않는 방식으로 변경 ★★★
        if (trapMessage == null) {
    %>
    <div class="container">
        <h1>Hotel Myunmok</h1>

        <div class="game-info">
             <p><strong>이름</strong> <%= HtmlEscaper.escape(currentPlayerId) %></p>
             <p><strong>남은 기회</strong> <%= attemptsLeft %></p>
        </div>

        <div class="quiz-box">
            <%
                if (request.getAttribute("message") != null) {
                    out.println("<p>" + HtmlEscaper.escape((String)request.getAttribute("message")) + "</p>");
                }

                if (isFirstVisit) {
                    out.println("<p>호텔 면목의 로비에 오신 것을 환영합니다.</p>");
                    out.println("<p>이곳에서는 기묘한 일들이 벌어지곤 합니다... 부디, 조심하십시오.</p>");
                } else if (currentFloor == 1) {
                    out.println("<p>1층 로비입니다. 다음 층으로 이동하세요.</p>");
                } else {
                    List<Floor> gameFloors = gameState.getGameFloors();
                    Floor floor = gameFloors.get(currentFloor - 1);

                    if (currentFloor == 7) {
                        out.println("<p>--- 7층 휴식 공간 ---</p>");
                        out.println("<p>이곳은 잠시 쉬어갈 수 있는 공간입니다. 편히 머물다 다음 층으로 이동하세요.</p>");
                    } else {
                        out.println("<p>--- " + floor.getFloorNumber() + "층입니다. ---</p>");
                        out.println("<p><b>[방송]</b> " + HtmlEscaper.escape(floor.getTraps().get(0).getDescription()) + "</p>");
                        out.println("<p><b>[문제]</b> " + HtmlEscaper.escape(floor.getTraps().get(0).getRiddle()) + "</p>");
                    }
                }
            %>
        </div>

        <form id="floorChangeForm" action="GameServlet" method="post" class="form-group">
            <input type="hidden" name="action" value="changeFloor">
            <label for="newFloor">층 이동:</label>
            <input type="text" id="newFloor" name="newFloor" placeholder="이동할 층 번호..." required <% if(!isCurrentFloorCleared && currentFloor != 1 && currentFloor != 7) { out.print("disabled"); } %>>
            <button type="submit" <% if(!isCurrentFloorCleared && currentFloor != 1 && currentFloor != 7) { out.print("disabled"); } %>>이동</button>
        </form>

        <form action="GameServlet" method="post" class="form-group">
            <input type="hidden" name="action" value="submitAnswer">
            <label for="answer">정답:</label>
            <input type="text" id="answer" name="answer" placeholder="정답 입력..." required <% if(isCurrentFloorCleared || currentFloor == 1 || currentFloor == 7) { out.print("disabled"); } %>>
            <button type="submit" <% if(isCurrentFloorCleared || currentFloor == 1 || currentFloor == 7) { out.print("disabled"); } %>>제출</button>
        </form>

        <div class="button-group">
            <button onclick="window.location.href='memos.jsp'">메모 보기</button>
            <button onclick="window.location.href='rules.jsp'">규칙 보기</button>
            <form action="GameServlet" method="post" style="display:inline;">
                <input type="hidden" name="action" value="exitGame">
                <button type="submit">체크아웃</button>
            </form>
        </div>
    </div>
    <%
        } // if (trapMessage == null) 의 끝
    %>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/tone/14.8.49/Tone.js"></script>
    <script>
        function playElevatorEffect(callback) {
            const overlay = document.getElementById('elevatorOverlay');
            document.getElementById('overlayMessage').innerText = 'Moving...';

            const synth = new Tone.Synth().toDestination();
            Tone.start().then(() => {
                synth.triggerAttackRelease("C5", "8n", Tone.now());
                synth.triggerAttackRelease("G5", "8n", Tone.now() + 0.2);
            });

            overlay.classList.add('show');

            setTimeout(function() {
                if (callback) callback();
            }, 2500);
        }

        const floorChangeForm = document.getElementById('floorChangeForm');
        if(floorChangeForm) {
            floorChangeForm.addEventListener('submit', function(event) {
                event.preventDefault();
                playElevatorEffect(() => this.submit());
            });
        }

        // 페이지 로드 시 즉시 실행
        (function() {
            <%
                if (trapMessage != null) {
            %>
                const overlay = document.getElementById('elevatorOverlay');
                const messageP = document.getElementById('overlayMessage');

                messageP.innerText = '<%= HtmlEscaper.escape(trapMessage) %>';
                overlay.classList.add('show');

                setTimeout(function() {
                    window.location.href = 'gameover.jsp';
                }, 3000);
            <%
                } else if (playReturnSound != null && playReturnSound) {
            %>
                 playElevatorEffect(() => {
                     document.getElementById('elevatorOverlay').classList.remove('show');
                 });
            <%
                }
            %>
        })();
    </script>
</body>
</html>

