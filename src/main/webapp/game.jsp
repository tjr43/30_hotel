<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>호텔 방탈출 게임</title>
    <style>
        body {
            font-family: 'Malgun Gothic', '맑은 고딕', sans-serif;
            background-color: #333;
            color: #eee;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }
        .container {
            width: 800px;
            background-color: #444;
            border: 2px solid #555;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
            text-align: center;
        }
        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
        h1 {
            font-size: 24px;
            color: #fff;
            margin: 0;
        }
        .timer-label {
            font-size: 18px;
            font-weight: bold;
            color: #ffcc00;
        }
        .display-area {
            width: 100%;
            height: 300px;
            background-color: #222;
            color: #eee;
            border: 1px solid #666;
            padding: 10px;
            box-sizing: border-box;
            resize: none;
            overflow-y: scroll;
            margin-bottom: 20px;
        }
        .input-group {
            display: flex;
            margin-bottom: 10px;
        }
        .input-group label {
            width: 120px;
            text-align: right;
            padding-right: 10px;
            line-height: 30px;
        }
        .input-group input[type="text"] {
            flex-grow: 1;
            padding: 5px;
            font-size: 16px;
            border: 1px solid #666;
            background-color: #555;
            color: #fff;
        }
        .buttons-group {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }
        .buttons-group button {
            padding: 10px 20px;
            font-size: 16px;
            border: none;
            cursor: pointer;
            background-color: #6a05ad;
            color: white;
            border-radius: 5px;
            transition: background-color 0.3s;
        }
        .buttons-group button:hover {
            background-color: #58048e;
        }
        .info-buttons button {
            background-color: #17a2b8;
        }
        .info-buttons button:hover {
            background-color: #117a8b;
        }
        .exit-button {
            background-color: #dc3545 !important;
        }
        .exit-button:hover {
            background-color: #c82333 !important;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <div class="timer-label">남은 시간: <span id="timer">--</span>초</div>
            <h1>현재 층: 1층</h1>
            <div class="player-info">플레이어: ID01</div>
        </div>

        <textarea class="display-area" readonly></textarea>

        <form action="GameServlet" method="POST">
            <div class="input-group">
                <label for="answer-field">정답 입력:</label>
                <input type="text" id="answer-field" name="answer" placeholder="정답을 입력하세요">
            </div>
            <div class="input-group">
                <label for="floor-field">층 이동:</label>
                <input type="text" id="floor-field" name="floor" placeholder="이동할 층을 입력하세요">
            </div>

            <div class="buttons-group">
                <button type="submit" name="action" value="submitAnswer">정답 제출</button>
                <button type="submit" name="action" value="goFloor">층 이동</button>
            </div>
        </form>

        <div class="buttons-group info-buttons">
            <button id="show-memos">메모 보기</button>
            <button id="show-rules">규칙 보기</button>
            <button class="exit-button">게임 종료</button>
        </div>
    </div>

    <script>
        document.getElementById('show-memos').addEventListener('click', function() {
            // 메모 보기 기능 구현
            alert('메모를 보여주는 기능은 아직 구현되지 않았습니다.');
        });
        document.getElementById('show-rules').addEventListener('click', function() {
            // 규칙 보기 기능 구현
            alert('규칙을 보여주는 기능은 아직 구현되지 않았습니다.');
        });
        document.getElementById('exit-button').addEventListener('click', function() {
            // 게임 종료 기능 구현
            alert('게임 종료 기능은 아직 구현되지 않았습니다.');
        });
    </script>
</body>
</html>