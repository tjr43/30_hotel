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
import java.util.HashSet;
import java.io.File;

public class GameGUI extends JFrame {
    private static final String FILE_NAME = "invitation.json";
    private GameState gameState;
    private boolean isFirstVisitTo1F = true;

    private static final int FINAL_FLOOR = 22;
    private static final int SPECIAL_TICKET_FLOOR = 7;
    private static final int INSTANT_DEFEAT_FLOOR = 17;

    private JTextArea displayArea;
    private JTextField answerField;
    private JTextField floorField;
    private JButton submitButton;
    private JButton goFloorButton;
    private JButton showMemosButton;
    private JButton showRulesButton;
    private JButton exitButton;

    public GameGUI() {
        setTitle("방 탈출 게임");

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();
        setSize(screenWidth / 2, screenHeight / 2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        DefaultCaret caret = (DefaultCaret) displayArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(displayArea);

        answerField = new JTextField(20);
        floorField = new JTextField(5);

        submitButton = new JButton("시작");
        goFloorButton = new JButton("층 이동");
        showMemosButton = new JButton("메모");
        showRulesButton = new JButton("규칙");
        exitButton = new JButton("게임 종료");

        JPanel southPanel = new JPanel();
        southPanel.add(new JLabel("정답: "));
        southPanel.add(answerField);
        southPanel.add(submitButton);

        southPanel.add(new JLabel("층 이동: "));
        southPanel.add(floorField);
        southPanel.add(goFloorButton);

        southPanel.add(showMemosButton);
        southPanel.add(showRulesButton);
        southPanel.add(exitButton);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(southPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(this::handleSubmit);
        goFloorButton.addActionListener(this::handleFloorChange);
        showMemosButton.addActionListener(this::handleShowMemos);
        showRulesButton.addActionListener(this::handleShowRules);
        exitButton.addActionListener(this::handleExit);

        loadGame();

        setVisible(true);
    }

    private void loadGame() {
        File file = new File(FILE_NAME);
        Gson gson = new Gson();

        if (file.exists() && file.length() > 0) {
            try (FileReader reader = new FileReader(file)) {
                gameState = gson.fromJson(reader, GameState.class);
                startNewGame(true);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "게임 상태를 불러오는 데 실패했습니다. 새로운 게임을 시작합니다.", "오류", JOptionPane.ERROR_MESSAGE);
                startNewGame(false);
            }
        } else {
            startNewGame(false);
        }
    }

    private void startNewGame(boolean isLoaded) {
        if (!isLoaded) {
            gameState = GameDataInitializer.createInitialState();
        }

        String playerName = JOptionPane.showInputDialog(this, "참가할 플레이어의 이름을 입력하세요:");
        if (playerName == null || playerName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "이름을 입력해야 게임을 시작할 수 있습니다. 게임을 종료합니다.", "알림", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        gameState.getAllPlayers().add(playerName.trim());
        gameState.setCurrentPlayerId(playerName.trim());
        gameState.setCurrentFloor(1);
        gameState.setAttemptsLeft(2);

        JOptionPane.showMessageDialog(this, gameState.getCurrentPlayerId() + "님, 호텔 면목에 오신 것을 환영합니다.", "환영합니다", JOptionPane.INFORMATION_MESSAGE);

        submitButton.setText("시작");
        isFirstVisitTo1F = true;

        promptForRiddle();
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
        if (gameState.getCurrentFloor() == 1) {
            if (isFirstVisitTo1F) {
                displayArea.append("--- 호텔 면목에 오신 것을 환영합니다! ---");
                displayArea.append("\n현재 계신 곳은 1층 안내데스크입니다.");
                displayArea.append("\n아래의 '규칙'을 눌러 규칙을 확인해 주시길 바랍니다");
                isFirstVisitTo1F = false;
            } else {
                displayArea.append("\n\n현재 계신 곳은 1층 안내데스크입니다.\n");
            }
        } else {
            Floor currentFloor = gameState.getGameFloors().get(gameState.getCurrentFloor() - 1);

            if (currentFloor.getFloorNumber() == SPECIAL_TICKET_FLOOR) {
                displayArea.append("\n--- " + currentFloor.getFloorNumber() + "층입니다. ---\n");
                if (gameState.getAttemptsLeft() == 2) {
                    gameState.setAttemptsLeft(1);
                    displayArea.append("함정에 걸려 기회를 1개 잃습니다. (남은 기회: " + gameState.getAttemptsLeft() + ")\n");
                } else {
                    gameState.setAttemptsLeft(2);
                    displayArea.append("티켓을 획득하여 남은 기회가 2개가 됩니다. (남은 기회: " + gameState.getAttemptsLeft() + ")\n");
                }
                return;
            }

            displayArea.append("\n--- " + currentFloor.getFloorNumber() + "층입니다. ---\n");
            displayArea.append("함정: " + currentFloor.getTraps().get(0).getDescription() + "\n");
            displayArea.append("수수께끼: " + currentFloor.getTraps().get(0).getRiddle() + "\n");
        }
    }

    private void handleSubmit(ActionEvent e) {
        String userAnswer = answerField.getText().trim();
        answerField.setText("");

        if (submitButton.getText().equals("시작")) {
            submitButton.setText("정답 제출");
            displayArea.append("\n\n게임을 시작하려면 아래의 \"시작\" 버튼을 눌러주세요.");
            displayArea.append("\n층을 입력하고 '층 이동' 버튼을 누르세요.\n");
            return;
        }

        if (gameState.getCurrentFloor() == 1) {
            displayArea.append("\n층을 입력하고 '층 이동' 버튼을 누르세요.\n");
            return;
        }

        if (userAnswer.equalsIgnoreCase("리셋")) {
            gameState.setAttemptsLeft(2);
            if (gameState.getEliminatedPlayers().contains(gameState.getCurrentPlayerId())) {
                gameState.getEliminatedPlayers().remove(gameState.getCurrentPlayerId());
            }
            displayArea.append("\n[관리자 모드] 남은 기회가 2회로 초기화되었고, 탈락자 명단에서 제외되었습니다.\n");
            displayArea.append("층을 입력하고 '층 이동' 버튼을 누르세요.\n");
            return;
        }

        Floor currentFloor = gameState.getGameFloors().get(gameState.getCurrentFloor() - 1);
        if (userAnswer.equalsIgnoreCase(currentFloor.getTraps().get(0).getAnswer())) {
            displayArea.append("정답입니다! 다음 층으로 이동하세요.\n");
            gameState.getCompletedFloors().add(gameState.getCurrentFloor());

            if (gameState.getCurrentFloor() == FINAL_FLOOR) {
                handleWin();
            }
        } else {
            gameState.setAttemptsLeft(gameState.getAttemptsLeft() - 1);
            if (gameState.getAttemptsLeft() > 0) {
                displayArea.append("틀렸습니다! 기회를 1회 잃고 1층으로 돌아갑니다. 남은 기회: " + gameState.getAttemptsLeft());
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
            } else if (newFloor != 1 && gameState.getCompletedFloors().contains(newFloor)) {
                JOptionPane.showMessageDialog(this, "이 층은 이미 통과했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            } else {
                if (newFloor == INSTANT_DEFEAT_FLOOR) {
                    displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다. 함정에 걸려 즉시 탈락합니다!\n");
                    handleGameOver();
                    return;
                }

                gameState.setCurrentFloor(newFloor);
                displayArea.append("플레이어가 " + newFloor + "층으로 이동했습니다.\n");

                promptForRiddle();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "유효한 층 번호를 숫자로 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            floorField.setText("");
        }
    }

    private void handleGameOver() {
        String memo = JOptionPane.showInputDialog(this, "다음 플레이어를 위한 메모를 남겨주세요:");

        PlayerRecord newRecord = new PlayerRecord(gameState.getCurrentPlayerId(), gameState.getCurrentFloor(), memo, "fail");
        gameState.getPlayerHistory().add(newRecord);

        displayArea.append("\n" + gameState.getCurrentPlayerId() + "님, 게임 오버! 메모가 기록되었습니다.\n");
        displayArea.append("게임을 다시 시작하려면 창을 닫고 다시 실행해 주세요.\n");

        saveGame();

        JOptionPane.showMessageDialog(this, "게임 오버! 게임을 종료합니다.");
        System.exit(0);
    }

    private void handleShowRules(ActionEvent e) {
        JDialog rulesDialog = new JDialog(this, "게임 규칙", true);
        rulesDialog.setSize(1100, 900);
        rulesDialog.setLocationRelativeTo(this);

        JTextArea rulesArea = new JTextArea();
        rulesArea.setEditable(false);
        rulesArea.setMargin(new Insets(10, 10, 10, 10));
        rulesArea.setText(
                        "1️ 게임 구조\n" +
                        "1.1 이 호텔은 총 30층으로 구성되어 있습니다.\n" +
                        "   - 1층: 안내 데스크\n" +
                        "   - 2~30층: 랜덤 퀴즈/이벤트 층\n" +
                        "1.2 플레이어는 층 번호를 직접 입력하여 원하는 층으로 이동할 수 있습니다.\n\n" +
                        "2 클리어 및 탈락 조건\n" +
                        "2.1 클리어 조건: 1층을 제외한 특정 층에서 퀴즈의 정답을 맞추면 클리어입니다.\n" +
                        "2.2 방의 문제를 맞추더라도 탈출 층이 아닐 수 있습니다.\n" +
                        "2.3 첫 탈출자 혜택: 제작자에게 연락하면 커피 쿠폰을 드립니다.\n\n" +
                        "2.4 모든 플레이어는 2번의 기회를 가지고 있습니다.\n" +
                        "2.5 문제를 틀리면 1층 안내 데스크로 돌아가고 기회가 소진됩니다.\n\n" +
                        "4️ 메모\n" +
                        "4.1 플레이어는 게임 클리어 또는 실패 후 메모를 남길 수 있습니다.\n" +
                        "4.2 메모는 들어온 순서대로 기록됩니다.\n" +
                        "4.3 메모의 내용은 자유로우며, 거짓말을 적어도 상관없습니다.\n" +
                        "4.4 메모 작성은 선택 사항입니다.\n\n" +
                        "5️ 게임 진행\n" +
                        "5.1 게임을 클리어 해도 게임은 멈추지 않고 계속 진행됩니다.\n" +
                        "5.2 게임을 클러어 하지 못해도 게임은 멈추지 않고 계속 진행됩니다.\n" +
                        "5.3 게임은 초대장(파일)을 받은 플레이거가 게임을 이어갈 수 있습니다.\n"+
                        "5.3 게임은 마지막에 초대장(파일)을 받은 사람이 다음 사람에게 전송하지 않으면 종료됩니다.\n\n" +
                        "게임을 시작하려면 아래의 '시작' 버튼을 눌러주세요."
        );
        rulesArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        rulesDialog.add(new JScrollPane(rulesArea));
        rulesDialog.setVisible(true);
    }

    private void handleShowMemos(ActionEvent e) {
        displayMemos();
    }

    private void displayMemos() {
        JDialog memoDialog = new JDialog(this, "이전 플레이어의 메모", true);
        memoDialog.setSize(500, 400);
        memoDialog.setLocationRelativeTo(this);

        JTextArea memoArea = new JTextArea();
        memoArea.setEditable(false);
        memoArea.setMargin(new Insets(10, 10, 10, 10));
        memoArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        if (gameState.getPlayerHistory().isEmpty()) {
            memoArea.setText("--- 이전 플레이어의 메모가 없습니다. ---");
        } else {
            StringBuilder memos = new StringBuilder();
            memos.append("--- 이전 플레이어들의 메모 ---\n\n");
            for (PlayerRecord record : gameState.getPlayerHistory()) {
                memos.append("플레이어 " + record.getPlayerId() + ": " + record.getMemo() + "\n\n");
            }
            memoArea.setText(memos.toString());
        }

        memoDialog.add(new JScrollPane(memoArea));
        memoDialog.setVisible(true);
    }

    private void handleWin() {
        String memo = JOptionPane.showInputDialog(this, "다음 플레이어를 위한 메모를 남겨주세요:");

        PlayerRecord newRecord = new PlayerRecord(gameState.getCurrentPlayerId(), FINAL_FLOOR, memo, "success");
        gameState.getPlayerHistory().add(newRecord);
        gameState.getCompletedPlayers().add(gameState.getCurrentPlayerId());

        displayArea.append("축하합니다! 방 탈출에 성공했습니다. 메모가 기록되었습니다.\n");
        displayArea.append("게임을 다시 시작하려면 창을 닫고 다시 실행해 주세요.\n");

        saveGame();

        JOptionPane.showMessageDialog(this, "탈출 성공! 게임을 종료합니다.");
        System.exit(0);
    }

    private void handleExit(ActionEvent e) {
        saveGame();
        System.exit(0);
    }
}
