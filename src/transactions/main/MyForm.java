package transactions.main;

import javax.swing.*;

public class MyForm {
    private JPanel panel1;
    private JTextArea textArea1;
    private JCheckBox runCheckBox;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Two Phase Commit");
        frame.setContentPane(new MyForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);


    }
}
