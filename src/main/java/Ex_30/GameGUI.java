package Ex_30;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameGUI extends JFrame {
    private static final String FILE_NAME = "game_state.json";
    private GameState gameState;

    private static final int FINAL_FLOOR = 22;

    private static final int SPECIAL_TICKET_FLOOR = 7;
    private static final int INSTANT_DEFEAT_FLOOR = 17;

    // 게임 시작 상태를 추적하는 플래그
    private boolean isGameStarted = false;

    private JTextArea displayArea;
    private JTextField answerField;
    private JTextField floorField;
    private JButton submitButton;
    private JButton goFloorButton;
    private JButton showMemosButton;
    private JButton exitButton;

    public GameGUI() {
        setTitle("방 탈출 게임");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        // 자동 스크롤 기능 추가
        DefaultCaret caret = (DefaultCaret) displayArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        answerField = new JTextField(20);
        floorField = new JTextField(5);

        submitButton = new JButton("정답 제출");
        goFloorButton = new JButton("층 이동");
        showMemosButton = new JButton("메모 확인");
        exitButton = new JButton("게임 종료");

        JPanel southPanel = new JPanel();
        southPanel.add(new JLabel("정답: "));
        southPanel.add(answerField);
        southPanel.add(submitButton);

        southPanel.add(new JLabel("층 이동: "));
        southPanel.add(floorField);
        southPanel.add(goFloorButton);

        southPanel.add(showMemosButton);
        southPanel.add(exitButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(southPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(this::handleSubmit);
        goFloorButton.addActionListener(this::handleFloorChange);
        showMemosButton.addActionListener(this::handleShowMemos);
        exitButton.addActionListener(this::handleExit);

        loadGame();

        setVisible(true);
    }

    private void loadGame() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(FILE_NAME)) {
            gameState = gson.fromJson(reader, GameState.class);
            displayArea.append("이전 게임을 불러왔습니다. 현재 층: " + gameState.getCurrentFloor() + "\n");
            displayMemos();
            promptForRiddle();
        } catch (IOException e) {
            displayArea.append("새로운 게임을 시작합니다.\n");
            gameState = GameDataInitializer.createInitialState();

            String input = JOptionPane.showInputDialog(this, "참가할 플레이어들의 이름을 쉼표(,)로 구분하여 입력하세요:");
            List<String> players = new ArrayList<>();
            if (input != null && !input.trim().isEmpty()) {
                String[] playerNames = input.split(",");
                for (String name : playerNames) {
                    String trimmedName = name.trim();
                    if (!trimmedName.isEmpty()) {
                        players.add(trimmedName);
                    }
                }
            }
            if (players.isEmpty()) {
                JOptionPane.showMessageDialog(this, "최소 한 명의 플레이어 이름을 입력해야 합니다. 게임을 종료합니다.", "알림", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
            gameState.getAllPlayers().addAll(players);
            gameState.setCurrentPlayerId(players.get(0));
            gameState.setAttemptsLeft(2);
            isGameStarted = false; // 새로운 게임 시작 플래그

            promptForRiddle();
        }
    }

    private void saveGame() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter(FILE_NAME)) {
            gson.toJson(gameState, writer);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "게임 상태 저장 실패!", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void promptForRiddle() {
        if (gameState.getCurrentFloor() > gameState.getGameFloors().size()) {
            displayArea.append("\n모든 층을 통과했습니다! 게임 클리어!");
            submitButton.setEnabled(false);
            goFloorButton.setEnabled(false);
            answerField.setEnabled(false);
            floorField.setEnabled(false);
            return;
        }

        if (gameState.getCurrentFloor() == 1) {
            if (!isGameStarted) {
                displayArea.append("\n--- 호텔에 오신 것을 환영합니다! ---\n");
                displayArea.append("규칙을 잘 읽고 게임을 플레이 해 주시길 바랍니다.\n");
                displayArea.append("1. 게임 전체적으로 2번의 기회가 주어집니다. 퀴즈를 틀리면 기회를 1회 잃고 1층으로 돌아갑니다.\n");
                displayArea.append("2. " + FINAL_FLOOR + "층이 최종 탈출 층이며, 17층은 즉시 탈락하는 함정입니다.\n");
                displayArea.append("3. 7층으로 이동하면 기회 상태에 따라 티켓을 얻거나 잃을 수 있습니다.\n");
                displayArea.append("4. 게임을 시작하려면 아래 '정답 제출' 버튼을 누르세요. 원하는 층으로 바로 이동하려면 번호를 입력하고 '층 이동' 버튼을 누르세요.\n");
            } else {
                displayArea.append("\n규칙을 다시 한 번 확인해 주십시오.\n");
                displayArea.append("1. 게임 전체적으로 2번의 기회가 주어집니다. 퀴즈를 틀리면 기회를 1회 잃고 1층으로 돌아갑니다.\n");
                displayArea.append("2. " + FINAL_FLOOR + "층이 최종 탈출 층이며, 17층은 즉시 탈락하는 함정입니다.\n");
                displayArea.append("3. 7층으로 이동하면 기회 상태에 따라 티켓을 얻거나 잃을 수 있습니다.\n");
                displayArea.append("4. 원하는 층 번호를 입력하고 '층 이동' 버튼을 누르세요.\n");
            }
            submitButton.setText("시작");
        } else {
            submitButton.setText("정답 제출");

            Floor currentFloor = gameState.getGameFloors().get(gameState.getCurrentFloor() - 1);

            displayArea.append("\n--- " + currentFloor.getFloorNumber() + "층입니다. 현재 플레이어: " + gameState.getCurrentPlayerId() + " ---\n");
            displayArea.append("남은 기회: " + gameState.getAttemptsLeft() + "\n");

            if (currentFloor.getFloorNumber() == FINAL_FLOOR) {
                displayArea.append("함정 1: " + currentFloor.getTraps().get(0).getDescription() + "\n");
                displayArea.append("최종 수수께끼: " + currentFloor.getTraps().get(0).getRiddle() + "\n");
            } else {
                displayArea.append("함정 1: " + currentFloor.getTraps().get(0).getDescription() + "\n");
                displayArea.append("수수께끼: " + currentFloor.getTraps().get(0).getRiddle() + "\n");
            }
        }
    }

    private void handleSubmit(ActionEvent e) {
        String userAnswer = answerField.getText().trim();
        answerField.setText("");

        if (gameState.getCurrentFloor() == 1) {
            if (userAnswer.equalsIgnoreCase("시작")) {
                isGameStarted = true;
                displayArea.append("게임이 시작되었습니다! 원하는 층으로 이동하세요.\n");
            } else {
                displayArea.append("게임을 시작하려면 '시작'을 입력하세요.\n");
            }
            return;
        }

        if (userAnswer.equalsIgnoreCase("리셋")) {
            gameState.setAttemptsLeft(2);
            if (gameState.getEliminatedPlayers().contains(gameState.getCurrentPlayerId())) {
                gameState.getEliminatedPlayers().remove(gameState.getCurrentPlayerId());
            }
            displayArea.append("\n[관리자 모드] 남은 기회가 2회로 초기화되었고, 탈락자 명단에서 제외되었습니다.\n");
            return;
        }

        if (gameState.getCurrentFloor() == FINAL_FLOOR) {
            Trap finalTrap = gameState.getGameFloors().get(FINAL_FLOOR - 1).getTraps().get(0);
            if (userAnswer.equalsIgnoreCase(finalTrap.getAnswer())) {
                handleWin();
            } else {
                displayArea.append("최종 수수께끼를 틀렸습니다! 게임 오버!\n");
                handleGameOver();
            }
            return;
        }

        Floor currentFloor = gameState.getGameFloors().get(gameState.getCurrentFloor() - 1);

        if (userAnswer.equalsIgnoreCase(currentFloor.getTraps().get(0).getAnswer())) {
            displayArea.append("정답입니다! 다음 층으로 이동하세요.\n");
        } else {
            gameState.setAttemptsLeft(gameState.getAttemptsLeft() - 1);
            if (gameState.getAttemptsLeft() > 0) {
                displayArea.append("틀렸습니다! 기회를 1회 잃고 1층으로 돌아갑니다. 남은 기회: " + gameState.getAttemptsLeft() + "\n");
                gameState.setCurrentFloor(1);
                promptForRiddle();
            } else {
                displayArea.append("기회를 모두 소진했습니다! 게임 오버!\n");
                handleGameOver();
            }
        }
    }

    private void handleFloorChange(ActionEvent e) {
        try {
            int newFloor = Integer.parseInt(floorField.getText().trim());
            floorField.setText("");
            int totalFloors = gameState.getGameFloors().size();

            if (newFloor < 1 || newFloor > totalFloors) {
                JOptionPane.showMessageDialog(this, "유효하지 않은 층 번호입니다. 1에서 " + totalFloors + " 사이의 숫자를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            } else {
                if (newFloor == INSTANT_DEFEAT_FLOOR) {
                    displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다. 함정에 걸려 즉시 탈락합니다!\n");
                    handleGameOver();
                    return;
                }

                gameState.setCurrentFloor(newFloor);

                if (newFloor == SPECIAL_TICKET_FLOOR) {
                    if (gameState.getAttemptsLeft() == 2) {
                        gameState.setAttemptsLeft(1);
                        displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다. 함정으로 인해 티켓을 1개 잃습니다. (남은 기회: " + gameState.getAttemptsLeft() + ")\n");
                    } else {
                        gameState.setAttemptsLeft(2);
                        displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다. 티켓을 획득하여 남은 기회가 2개가 됩니다. (남은 기회: " + gameState.getAttemptsLeft() + ")\n");
                    }
                } else {
                    displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다.\n");
                }

                promptForRiddle();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 층 번호를 숫자로 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            floorField.setText("");
        }
    }

    private void handleGameOver() {
        String userName = JOptionPane.showInputDialog(this, "당신의 이름을 입력하세요:");
        String memo = JOptionPane.showInputDialog(this, "다음 플레이어를 위한 메모를 남겨주세요:");

        PlayerRecord newRecord = new PlayerRecord(userName, 1, memo, "fail");
        gameState.getPlayerHistory().add(newRecord);

        gameState.getEliminatedPlayers().add(gameState.getCurrentPlayerId());

        String nextPlayerId = getNextPlayerId(gameState.getCurrentPlayerId());

        if (nextPlayerId != null) {
            gameState.setCurrentPlayerId(nextPlayerId);
            gameState.setAttemptsLeft(2);
            displayArea.append("게임 오버! 다음 플레이어: " + gameState.getCurrentPlayerId() + "\n");
            promptForRiddle();
        } else {
            displayArea.append("모든 플레이어가 탈락했습니다. 게임 종료!");
            submitButton.setEnabled(false);
            goFloorButton.setEnabled(false);
            answerField.setEnabled(false);
            floorField.setEnabled(false);
        }

        saveGame();
    }

    private String getNextPlayerId(String currentId) {
        int currentIndex = gameState.getAllPlayers().indexOf(currentId);
        int totalPlayers = gameState.getAllPlayers().size();

        for (int i = 1; i <= totalPlayers; i++) {
            String nextId = gameState.getAllPlayers().get((currentIndex + i) % totalPlayers);
            if (!gameState.getEliminatedPlayers().contains(nextId) && !gameState.getCompletedPlayers().contains(nextId)) {
                return nextId;
            }
        }
        return null;
    }

    private void handleShowMemos(ActionEvent e) {
        displayMemos();
    }

    private void displayMemos() {
        if (!gameState.getPlayerHistory().isEmpty()) {
            displayArea.append("\n--- 이전 플레이어들의 메모 ---\n");
            for (PlayerRecord record : gameState.getPlayerHistory()) {
                displayArea.append("플레이어 " + record.getPlayerId() + ": " + record.getMemo() + "\n");
            }
            displayArea.append("---------------------------\n");
        }
    }

    private void handleWin() {
        String userName = JOptionPane.showInputDialog(this, "탈출 성공! 당신의 이름을 입력하세요:");
        String memo = JOptionPane.showInputDialog(this, "다음 플레이어를 위한 메모를 남겨주세요:");

        PlayerRecord newRecord = new PlayerRecord(userName, FINAL_FLOOR, memo, "success");
        gameState.getPlayerHistory().add(newRecord);

        gameState.getCompletedPlayers().add(gameState.getCurrentPlayerId());

        displayArea.append("축하합니다! 방 탈출에 성공했습니다. 메모가 기록되었습니다.\n");

        String nextPlayerId = getNextPlayerId(gameState.getCurrentPlayerId());

        if (nextPlayerId != null) {
            gameState.setCurrentPlayerId(nextPlayerId);
            gameState.setAttemptsLeft(2);
            displayArea.append("게임이 계속됩니다. 다음 플레이어: " + gameState.getCurrentPlayerId() + "\n");
            promptForRiddle();
        } else {
            displayArea.append("모든 플레이어가 게임을 완료했거나 탈락했습니다. 게임 종료!");
            submitButton.setEnabled(false);
            goFloorButton.setEnabled(false);
            answerField.setEnabled(false);
            floorField.setEnabled(false);
        }

        saveGame();
    }

    private void handleExit(ActionEvent e) {
        saveGame();
        System.exit(0);
    }
}