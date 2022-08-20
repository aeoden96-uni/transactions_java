package transactions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
    private JTextField textField1;

    private boolean returnValue = true;

    private static int procId = 1;
    private static int numProc;

    private ButtonGroup typeGroup;

    private ButtonGroup responseGroup;

    public void setProgressBar(int value) {
        progressBar1.setValue(value);
    }

    public static void main(String[] args){

        numProc = 3;
        if(args.length > 0) {
            numProc = Integer.parseInt(args[0]);
        }

        JFrame frame = new JFrame("NameServer");
        frame.setContentPane(new MainForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(200, 500);
        frame.pack();
        frame.setVisible(true);

        JFrame master = new JFrame("Master");
        master.setContentPane(new MainForm().panel1);
        master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        master.setLocation(500, 500);
        master.pack();
        master.setVisible(true);

        for (int i = 0; i < numProc; i++) {
            JFrame slave = new JFrame("Slave " + (i + 1));
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


    public void startMaster() throws Exception {
        TwoPhaseTester("name",0,4,true);
    }

    public void startSlave() throws Exception {
        if(responseOKRadioButton.isSelected()){
            TwoPhaseTester("name",procId++,4,true);
        }
        else if(responseFRadioButton.isSelected()){
            TwoPhaseTester("name",procId++,4,false);
        }
        else{
            //TwoPhaseTester("name",procId++,4,false);
        }
    }

    public void start() throws Exception {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(nameServerRadioButton.isSelected()){
                        startNameServer();
                    }
                    else if(masterRadioButton.isSelected()){
                        startMaster();
                    }
                    else{
                        startSlave();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

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

                try {
                    start();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


    }

    public void startNameServer() {
        NameServer ns = new NameServer();
        textArea1.append("NameServer started:");
        try {
            ServerSocket listener = new ServerSocket(Symbols.ServerPort);
            while (true) {
                Socket aClient = listener.accept();
                ns.handleclient(aClient);
                aClient.close();
            }
        } catch (IOException e) {
            System.err.println("Server aborted:" + e);
        }
    }




    public void TwoPhaseTester(String baseName, int myId, int numProc, boolean t) throws Exception {
        //print args
//        for (int i = 0; i < args.length; i++) {
//            System.out.println(args[i]);
//        }

        Linker comm = new Linker(baseName, myId, numProc, textArea1);
        if (myId == 0) {
            TwoPhaseCoord master = new TwoPhaseCoord(comm,textArea1);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, master)).start();

            textArea1.append(master.doCoordinator()+"\n");
        }
        else {
            TwoPhaseParticipant slave = new TwoPhaseParticipant(comm);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, slave)).start();

            slave.propose(t);
            textArea1.append("The value decided:" + slave.decide());
        }
    }
}
