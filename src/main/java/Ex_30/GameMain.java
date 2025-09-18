package Ex_30;

import javax.swing.SwingUtilities;
//프로그램의 시작
public class GameMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}
