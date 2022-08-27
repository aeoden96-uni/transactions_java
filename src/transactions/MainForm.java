package transactions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainForm {
    Color green = new Color(0, 141, 0); //GREEN
    Color red = new Color(255, 0, 0); //RED

    Color yellow = new Color(92, 92, 27); //YELLOW
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
    private JProgressBar statusCheck;
    private JLabel labelID;
    private JLabel labelNum;
    private JProgressBar participantReply;
    private JLabel participantReplyLabel;
    private JLabel statusLabel;
    private JSpinner processId;
    private JSpinner numOfProcesess;

    private JProgressBar acknowledgementsRecivedStatus;
    private JLabel acknowledgementsRecivedStatusLabel;
    private JProgressBar votesRecivedStatus;
    private JLabel votesRecivedLabel;


    private ButtonGroup typeGroup;

    private ButtonGroup responseGroup;

    static int numOfProcesses;

    public void setProgressBar(int value,String text) {

        //set text in progress bar
        progressBar1.setString(text);
        progressBar1.setStringPainted(true);

        progressBar1.setIndeterminate(false);
        progressBar1.setValue(value);
    }

    public static void main(String[] args){
        numOfProcesses = 1;
        int numOfWindows = 1;

        if(args.length > 0) {
            numOfProcesses = Integer.parseInt(args[0]);
            numOfWindows = numOfProcesses + 2;

        }

        for (int i = 0; i < numOfWindows; i++) {
            JFrame window = new JFrame("Two-phase commit protocol");
            window.setContentPane(new MainForm().panel1);
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setLocation(1000, 100* i);
            window.setResizable(false);
            window.setPreferredSize(new Dimension(500, 360));
            window.setSize(new Dimension(330, 320));
            window.pack();
            window.setVisible(true);
        }

    }

    public void handleRadioButton(){

        responseOKRadioButton.setVisible(slaveRadioButton.isSelected());
        responseFRadioButton.setVisible(slaveRadioButton.isSelected());
        noResponseRadioButton.setVisible(slaveRadioButton.isSelected());

        //ID and number of processes
        labelID.setVisible(!nameServerRadioButton.isSelected());
        processId.setVisible(!nameServerRadioButton.isSelected());
        processId.setEnabled(slaveRadioButton.isSelected());
        processId.setValue(slaveRadioButton.isSelected()? processId.getValue() : 0);

        labelNum.setVisible(!nameServerRadioButton.isSelected());
        numOfProcesess.setVisible(!nameServerRadioButton.isSelected());

        //indicators
        statusLabel.setVisible(!nameServerRadioButton.isSelected());
        statusCheck.setVisible(!nameServerRadioButton.isSelected());
        votesRecivedStatus.setVisible(masterRadioButton.isSelected());
        votesRecivedLabel.setVisible(masterRadioButton.isSelected());
        acknowledgementsRecivedStatus.setVisible(masterRadioButton.isSelected());
        acknowledgementsRecivedStatusLabel.setVisible(masterRadioButton.isSelected());

        participantReplyLabel.setVisible(slaveRadioButton.isSelected());
        participantReply.setVisible(slaveRadioButton.isSelected());


        if(responseOKRadioButton.isSelected()){
            participantReply.setForeground(green);
        }
        else if(responseFRadioButton.isSelected()){
            participantReply.setForeground(red);
        }
        else if(noResponseRadioButton.isSelected()){
            participantReply.setForeground(Color.BLACK);
        }
        else{
            participantReply.setForeground(Color.BLACK);
        }

    }


    public void startCoordinator() throws Exception {
        int numOfProcesess = Integer.parseInt(this.numOfProcesess.getValue().toString());

        Linker comm = new Linker("name", 0, numOfProcesess + 1, textArea1);
        TwoPhaseCoord coordinator = new TwoPhaseCoord(comm,textArea1);

        for (int i = 1; i < numOfProcesess + 1; i++)
            (new ListenerThread(i, coordinator)).start();

        setProgressBar(50,"Waiting for all processes to confirm");
        progressBar1.setIndeterminate(true);
        statusCheck.setForeground(yellow);

        coordinator.votingPhase();

        progressBar1.setIndeterminate(false);
        setProgressBar(100,"All processes confirmed");
        progressBar1.setForeground(green);
        votesRecivedStatus.setForeground(green);


        progressBar1.setForeground(Color.gray);


        for(int k = 0; k < 100; k+=1){
            setProgressBar(k,"Sending commit/rollback msg");
            Thread.sleep(100);
            //print thread status
        }
        setProgressBar(100,"Sending commit/rollback msg");

        coordinator.sendCommit();

        setProgressBar(0,"Waiting acknowledgements");
        progressBar1.setIndeterminate(true);


        coordinator.acknowledgePhase();

        progressBar1.setIndeterminate(false);
        setProgressBar(100,"All processes acknowledged");
        acknowledgementsRecivedStatus.setForeground(green);



        String response = coordinator.finalPhase();
        //returns "commit" or "abort"

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

    public void startParticipant() throws Exception {
        boolean response;
        if(responseOKRadioButton.isSelected()){
            response = true;
        }
        else{
            response = false;
        }

        int processId = -1;
        int numOfProcesess = -1;

        try{
            processId = Integer.parseInt(this.processId.getValue().toString());
            numOfProcesess = Integer.parseInt(this.numOfProcesess.getValue().toString());

            assert processId > 0 && processId <= numOfProcesess;
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "Participant ID is not valid");
            //exit program
            System.exit(1);

        }

        Linker comm = null;
        try {
            comm = new Linker("name",processId , numOfProcesess + 1, textArea1);
        } catch (java.net.ConnectException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog( new JFrame("Error"), "Name server is not running", "Error", JOptionPane.ERROR_MESSAGE);

            System.exit(1);
        }


        TwoPhaseParticipant participant = new TwoPhaseParticipant(comm,textArea1);
        for (int i = 0; i < numOfProcesess + 1; i++)
            if(i != processId)
                (new ListenerThread(i, participant)).start();



        textArea1.append("The value decided:" + response + "\n\n");


        int r = (int) (Math.random() * 12) + 1;
        for(int k = 0; k < 100; k+=r){
            setProgressBar(k,"Sending confirmation.");
            Thread.sleep(200);
            //print thread status
        }
        setProgressBar(100,"Sending confirmation.");

        Color yellow = new Color(92, 92, 27);
        if(noResponseRadioButton.isSelected()){
            textArea1.append("\nError - No response from the participant.\n\n");
            setProgressBar(100,"Error - no response from the participant");

            progressBar1.setForeground(yellow);
            progressBar1.setIndeterminate(true);
            Thread.sleep(100000);
            return;
        }

        participant.vote(response);

        setProgressBar(0,"Waiting for coordinator");
        progressBar1.setIndeterminate(true);

        statusCheck.setForeground(yellow);

        boolean result = participant.coordResult();
        progressBar1.setIndeterminate(false);
        statusCheck.setForeground(result?green:red);



        r = (int) (Math.random() * 12) + 1;
        for(int k = 0; k <= 100; k+=r){
            setProgressBar(k,"Sending acknowledgement");
            Thread.sleep(200);
        }
        setProgressBar(100,"Sending acknowledgement");

        participant.acknowledge();




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
                        startCoordinator();
                    }
                    else{
                        startParticipant();
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

        numOfProcesess.setValue(numOfProcesses);
        processId.setValue(1);

        participantReply.setValue(100);
        participantReply.setForeground(green);
        participantReply.setEnabled(false);

        acknowledgementsRecivedStatusLabel.setVisible(false);
        acknowledgementsRecivedStatus.setVisible(false);
        acknowledgementsRecivedStatus.setValue(100);
        acknowledgementsRecivedStatus.setForeground(Color.gray);
        acknowledgementsRecivedStatus.setEnabled(false);

        votesRecivedLabel.setVisible(false);
        votesRecivedStatus.setVisible(false);
        votesRecivedStatus.setValue(100);
        votesRecivedStatus.setForeground(Color.gray);
        votesRecivedStatus.setEnabled(false);






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


        statusCheck.setForeground(green);
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


}
