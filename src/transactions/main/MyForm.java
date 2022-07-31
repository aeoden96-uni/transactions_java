package transactions.main;

import transactions.backend.Linker;
import transactions.backend.ListenerThread;
import transactions.backend.TwoPhaseCoord;
import transactions.backend.TwoPhaseParticipant;
import transactions.backend.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MyForm {
    private JPanel panel1;
    private JTextArea textArea;
    private JCheckBox runCheckBox;
    private JButton button1;
    private JComboBox comboBox1;
    private JFormattedTextField formattedTextField1;
    private JFormattedTextField formattedTextField2;

    public int slaveId = 1;

    public MyForm() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (runCheckBox.isSelected()) {
                    runCheckBox.setSelected(false);
                    button1.setText("Start");
                    button1.setSelected(true);
                    textArea.append("Stopping...\n");
                    //TwoPhaseCoord.getInstance().stop();
                } else {
                    runCheckBox.setSelected(true);
                    button1.setText("Stop");
                    button1.setSelected(false);
                    textArea.append("Starting...\n");
                   // TwoPhaseCoord.getInstance().start();
                    MyListenerThread thread = new MyListenerThread();
                    thread.start();


                }
            }
        });
    }

    public class MyListenerThread extends Thread {

        public void startNameServer() {
            NameServer ns = new NameServer();
            textArea.append("NameServer started.");
            try {
                ServerSocket listener = new ServerSocket(Symbols.ServerPort);
                while (true) {
                    Socket aClient = listener.accept();
                    ns.handleclient(aClient);
                    aClient.close();
                }
            } catch (IOException e) {
                textArea.append("Server aborted:" + e);
            }
        }

        public void run() {
            try {
                switch (comboBox1.getSelectedItem().toString()) {
                    case "NameServer" -> startNameServer();
                    case "master" -> create_master("master", 2, "");
                    case "slave" -> create_slave("slave", slaveId++, 2, "t");
                }

            } catch (Exception ex) {
                textArea.append("\nException:" + ex);

            }

        }

        public void create_master(String name,int num_of_proc, String t) throws Exception {
            String baseName = name;
            int myId =0;
            int numProc = num_of_proc;
            Linker comm = new Linker(baseName, myId, numProc, textArea);

            TwoPhaseCoord master = new TwoPhaseCoord(comm);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, master)).start();
            master.doCoordinator(textArea);


        }
        public void create_slave(String name,int index,int num_of_proc,String is_slave ) throws Exception {  //Slave1 1 2   Slave2 2 2
            String baseName = name;
            int myId =index;
            int numProc = num_of_proc;
            Linker comm = new Linker(baseName, myId, numProc, textArea);


            TwoPhaseParticipant slave = new TwoPhaseParticipant(comm);
            for (int i = 0; i < numProc; i++)
                if (i != myId)
                    (new ListenerThread(i, slave)).start();
            if (is_slave.equals("t")) slave.propose(true);
            else slave.propose(false);
            textArea.append("The value decided:" + slave.decide());

        }
    }



    public static void main(String[] args) {

        for (int i = 0; i < 4; i++) {
            JFrame frame = new JFrame("Transaction");
            frame.setContentPane(new MyForm().panel1);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.pack();
            frame.setVisible(true);
        }


    }


}
