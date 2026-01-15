package me.combatsim.java.testcom;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class UnitRenderTestFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                TestBootstrap bootstrap = new TestBootstrap();
                JFrame frame = new JFrame("Unit Render Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(new UnitRenderTestPanel(bootstrap.getUnitManager()));
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
