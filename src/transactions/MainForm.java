package transactions;

import javax.swing.*;
import java.awt.*;
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
    private JTextField processId;
    private JTextField numOfProcesess;
    private JProgressBar statusCheck;
    private JLabel labelID;
    private JLabel labelNum;


    private ButtonGroup typeGroup;

    private ButtonGroup responseGroup;

    public void setProgressBar(int value,String text) {

        //set text in progress bar
        progressBar1.setString(text);
        progressBar1.setStringPainted(true);

        progressBar1.setIndeterminate(false);
        progressBar1.setValue(value);
    }

    public static void main(String[] args){


        if(args.length > 0) {
            int numProc = Integer.parseInt(args[0]);

            JFrame frame = new JFrame("NameServer");
            frame.setContentPane(new MainForm().panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocation(200, 500);
            frame.setResizable(false);
            frame.setPreferredSize(new Dimension(330, 320));
            frame.pack();
            frame.setVisible(true);


            JFrame master = new JFrame("Master");
            master.setContentPane(new MainForm().panel1);
            master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            master.setLocation(500, 500);
            master.setResizable(false);
            master.setPreferredSize(new Dimension(330, 320));
            master.pack();
            master.setVisible(true);

            for (int i = 0; i < numProc; i++) {
                JFrame slave = new JFrame("Slave " + (i + 1));
                slave.setContentPane(new MainForm().panel1);
                slave.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                slave.setLocation(1000, 200*i);
                slave.setResizable(false);
                slave.setPreferredSize(new Dimension(370, 320));
                slave.setSize(new Dimension(330, 320));
                slave.pack();
                slave.setVisible(true);

            }
        }
        else {
            JFrame master = new JFrame("TwoPhaseCoord");
            master.setContentPane(new MainForm().panel1);
            master.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            master.setResizable(false);
            master.setPreferredSize(new Dimension(330, 320));
            master.setLocation(500, 500);
            master.pack();
            master.setVisible(true);
        }


    }

    public void handleRadioButton(){
        if(masterRadioButton.isSelected()){

            //disable response radio buttons
            responseOKRadioButton.setVisible(false);
            responseFRadioButton.setVisible(false);
            noResponseRadioButton.setVisible(false);

            processId.setVisible(true);
            processId.setEnabled(false);
            processId.setText("0");

            numOfProcesess.setVisible(true);

            labelID.setVisible(true);

            labelNum.setVisible(true);
        }
        else if(nameServerRadioButton.isSelected()){
            responseOKRadioButton.setVisible(false);
            responseFRadioButton.setVisible(false);
            noResponseRadioButton.setVisible(false);

            processId.setVisible(false);
            numOfProcesess.setVisible(false);

            labelID.setVisible(false);
            labelNum.setVisible(false);
        }
        else{
            //enable response radio buttons
            responseOKRadioButton.setVisible(true);
            responseFRadioButton.setVisible(true);
            noResponseRadioButton.setVisible(true);
            processId.setVisible(true);
            processId.setEnabled(true);
            numOfProcesess.setVisible(true);

            labelID.setVisible(true);
            labelNum.setVisible(true);
        }
    }


    public void startMaster() throws Exception {
        int numOfProcesess = Integer.parseInt(this.numOfProcesess.getText());

        Linker comm = new Linker("name", 0, numOfProcesess + 1, textArea1);
        TwoPhaseCoord master = new TwoPhaseCoord(comm,textArea1);


        setProgressBar(50,"Waiting for all processes to confirm");
        Color yellow = new Color(92, 92, 27);
        statusCheck.setForeground(yellow);


        for (int i = 1; i < numOfProcesess + 1; i++)
                (new ListenerThread(i, master)).start();

        String response = master.doCoordinator();

        // if finalCommit is in string
        if(response.contains("finalCommit")){
            setProgressBar(100,"All processes confirmed");
            Color green = new Color(0, 128, 0);
            statusCheck.setForeground(green);
            progressBar1.setForeground(green);
        }
        else{
            setProgressBar(100,"All processes aborted");
            Color red = new Color(255, 0, 0);
            statusCheck.setForeground(red);
            progressBar1.setForeground(red);
        }

    }

    public void startSlave() throws Exception {
        boolean response;
        if(responseOKRadioButton.isSelected()){
            response = true;
        }
        else if(responseFRadioButton.isSelected()){
            response = false;
        }
        else{
            return;
        }

        int processId = Integer.parseInt(this.processId.getText());
        int numOfProcesess = Integer.parseInt(this.numOfProcesess.getText());

        Linker comm = new Linker("name",processId , numOfProcesess + 1, textArea1);

        TwoPhaseParticipant slave = new TwoPhaseParticipant(comm,textArea1);
        for (int i = 0; i < numOfProcesess + 1; i++)
            if(i != processId)
                (new ListenerThread(i, slave)).start();


        Color green = new Color(0, 141, 0); //GREEN
        Color red = new Color(255, 0, 0); //RED

        if(response){
            progressBar1.setForeground(green);
        }
        else{
            progressBar1.setForeground(red);
        }

        int r = (int) (Math.random() * 12) + 1;
        for(int k = 0; k < 100; k+=r){
            setProgressBar(k,"Sending confirmation to coordinator");
            Thread.sleep(100);

        }
        setProgressBar(100,"Sending confirmation to coordinator");
        slave.propose(response);

        setProgressBar(100,"Waiting for coordinator");
        Color yellow = new Color(92, 92, 27);
        statusCheck.setForeground(yellow);

        boolean result = slave.decide();

        textArea1.append("The value decided:" + response);
        setProgressBar(100,"Sent: " + response);



        if(result){
            statusCheck.setForeground(Color.GREEN);
        }
        else{
            statusCheck.setForeground(red);
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
        slaveRadioButton.setSelected(true);
        responseOKRadioButton.setSelected(true);

        responseOKRadioButton.setEnabled(true);
        responseFRadioButton.setEnabled(true);
        noResponseRadioButton.setEnabled(true);
        statusCheck.setEnabled(false);
        statusCheck.setValue(100);
        statusCheck.setForeground(Color.gray);

        numOfProcesess.setText("3");






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
        textArea1.append("NameServer started");


        statusCheck.setForeground(Color.GREEN);
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




//    public void TwoPhaseTester(String baseName, int myId, int numProc, boolean t) throws Exception {
//        //print args
////        for (int i = 0; i < args.length; i++) {
////            System.out.println(args[i]);
////        }
//
//        Linker comm = new Linker(baseName, myId, numProc, textArea1);
//        if (myId == 0) {
//            TwoPhaseCoord master = new TwoPhaseCoord(comm,textArea1);
//            for (int i = 0; i < numProc; i++)
//                if (i != myId)
//                    (new ListenerThread(i, master)).start();
//
//            textArea1.append(master.doCoordinator()+"\n");
//        }
//        else {
//            TwoPhaseParticipant slave = new TwoPhaseParticipant(comm,textArea1);
//            for (int i = 0; i < numProc; i++)
//                if (i != myId)
//                    (new ListenerThread(i, slave)).start();
//
//            //sleep between 5 and 10 seconds
//            int sleepTime = (int) (Math.random() * 5000) + 5000;
//
//            Thread.sleep(sleepTime);
//
//            slave.propose(t);
//            textArea1.append("The value decided:" + slave.decide());
//        }
//    }
//
}
