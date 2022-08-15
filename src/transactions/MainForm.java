package transactions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainForm {

    private JPanel panel1;
    private JRadioButton nameServerRadioButton;
    private JRadioButton masterRadioButton;
    private JRadioButton slaveRadioButton;
    private JTextArea textArea1;
    private JButton startButton;
    private JProgressBar progressBar1;
    private JRadioButton responseOKRadioButton;
    private JRadioButton responseFRadioButton;
    private JRadioButton noResponseRadioButton;

    private ButtonGroup typeGroup;

    private ButtonGroup responseGroup;

    public static void main(String[] args){

        JFrame frame = new JFrame("Transactions");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(200, 500);
        frame.pack();
        frame.setVisible(true);

        JFrame master = new JFrame("Transactions");
        master.setContentPane(new MainForm().panel1);
        master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        master.setLocation(500, 500);
        master.pack();
        master.setVisible(true);

        for (int i = 0; i < 3; i++) {
            JFrame slave = new JFrame("Transactions");
            slave.setContentPane(new MainForm().panel1);
            slave.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            slave.setLocation(1000, 300*i);
            slave.pack();
            slave.setVisible(true);
        }


    }

    public void handleRadioButton(){
        if(nameServerRadioButton.isSelected() || masterRadioButton.isSelected()){
            //disable response radio buttons
            responseOKRadioButton.setEnabled(false);
            responseFRadioButton.setEnabled(false);
            noResponseRadioButton.setEnabled(false);
        }
        else{
            //enable response radio buttons
            responseOKRadioButton.setEnabled(true);
            responseFRadioButton.setEnabled(true);
            noResponseRadioButton.setEnabled(true);
        }
    }



    public MainForm() {

        //make radio button group
        typeGroup = new ButtonGroup();
        typeGroup.add(nameServerRadioButton);
        typeGroup.add(masterRadioButton);
        typeGroup.add(slaveRadioButton);

        //make radio button group
        responseGroup = new ButtonGroup();
        responseGroup.add(responseOKRadioButton);
        responseGroup.add(responseFRadioButton);
        responseGroup.add(noResponseRadioButton);

        //set default values
        nameServerRadioButton.setSelected(true);
        responseOKRadioButton.setSelected(true);

        responseOKRadioButton.setEnabled(false);
        responseFRadioButton.setEnabled(false);
        noResponseRadioButton.setEnabled(false);


        //set action listener for radio button
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRadioButton();
            }
        };
        nameServerRadioButton.addActionListener(listener);
        masterRadioButton.addActionListener(listener);
        slaveRadioButton.addActionListener(listener);
        responseOKRadioButton.addActionListener(listener);
        responseFRadioButton.addActionListener(listener);
        noResponseRadioButton.addActionListener(listener);


        //set start button action
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setText("Running...");
                startButton.setEnabled(false);
                progressBar1.setVisible(true);
                progressBar1.setIndeterminate(true);

                responseOKRadioButton.setEnabled(false);
                responseFRadioButton.setEnabled(false);
                noResponseRadioButton.setEnabled(false);
                nameServerRadioButton.setEnabled(false);
                masterRadioButton.setEnabled(false);
                slaveRadioButton.setEnabled(false);
            }
        });


    }

    public static boolean TwoPhaseTester(String[] args) throws Exception {
        //print args
        for (int i = 0; i < args.length; i++) {
            System.out.println(args[i]);
        }

        String baseName = args[0];
        int myId = Integer.parseInt(args[1]);
        int numProc = Integer.parseInt(args[2]);
        Linker comm = new Linker(baseName, myId, numProc);
        if (myId == 0) {
            TwoPhaseCoord master = new TwoPhaseCoord(comm);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, master)).start();
            master.doCoordinator();
            return true;
        }
        else {
            TwoPhaseParticipant slave = new TwoPhaseParticipant(comm);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, slave)).start();
            if (args[3].equals("t")) slave.propose(true);
            else slave.propose(false);
            System.out.println("The value decided:" + slave.decide());
            return slave.decide();
        }
    }
}
