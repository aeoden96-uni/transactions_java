package transactions;
import javax.swing.*;
import java.io.*; import java.lang.*;


/**
 * This class is used to process the messages received from the server. It is
 * responsible for creating the appropriate transaction object and passing the
 * message to the transaction object.
 *
 */
public abstract  class Process implements MsgHandler {
    int N, myId;

    Linker comm;

    JTextArea textArea;


    /**
     * This method is used to create a new process.
     *
     * @param initComm The linker object to use for communication.  This is used
     *             to send messages to other processes.
     */
    public Process(Linker initComm) {
        comm = initComm;
        myId = comm.getMyId();
        N = comm.getNumProc();
    }

    public abstract void handleMsg(Msg m, int src, String tag);

    public abstract void sendMsg(int destId, String tag);


    public void broadcastMsg(String tag) {
        for (int i = 0; i < N; i++) {
            if (i != myId) {
                sendMsg(i, tag);
            }
        }
    }

    public Msg receiveMsg(int fromId) {
        try {
            return comm.receiveMsg(fromId);
        } catch (IOException e){
            System.out.println(e.getMessage());
            comm.close();
            return null;
        }
    }
    public synchronized void myWait() {
        try {
            wait();
        } catch (InterruptedException e) {System.err.println(e.getMessage());
        }
    }

}
