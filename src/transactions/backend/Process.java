package transactions.backend;
import javax.swing.*;
import java.io.*; import java.lang.*;

public class Process implements MsgHandler {

    JTextArea textArea;
    int N, myId;
    Linker comm;
    public Process(Linker initComm) {
        comm = initComm;
        myId = comm.getMyId();
        N = comm.getNumProc();
    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
    }



    public void sendMsg(int destId, String tag, String msg,JTextArea textArea) {
        Util.println("Sending msg to " + destId + ":" +tag + " " + msg, textArea);
        comm.sendMsg(destId, tag, msg);
    }
    public void sendMsg(int destId, String tag, int msg,JTextArea textArea) {
        sendMsg(destId, tag, String.valueOf(msg)+" ",textArea);
    }
    public void sendMsg(int destId, String tag, int msg1, int msg2,JTextArea textArea) {
        sendMsg(destId,tag,String.valueOf(msg1)
                +" "+String.valueOf(msg2)+" ",textArea);
    }
    public void sendMsg(int destId, String tag,JTextArea textArea) {
        sendMsg(destId, tag, " 0 ",textArea);
    }
    public void broadcastMsg(String tag, int msg,JTextArea textArea) {
        for (int i = 0; i < N; i++)
            if (i != myId) sendMsg(i, tag, msg,textArea);
    }
    public void sendToNeighbors(String tag, int msg,JTextArea textArea) {
        for (int i = 0; i < N; i++)
            if (isNeighbor(i)) sendMsg(i, tag, msg,textArea);
    }
    public boolean isNeighbor(int i) {
        if (comm.neighbors.contains(i)) return true;
        else return false;
    }

    @Override
    public Msg receiveMsg(int fromId) throws IOException {
        try {
            return comm.receiveMsg(fromId,textArea);
        } catch (IOException e){
            this.textArea.append("\n"+e.getMessage());
            comm.close();
            return null;
        }
    }

    public Msg receiveMsg(int fromId,JTextArea textArea) throws IOException {
        this.textArea = textArea;
        return receiveMsg(fromId);

    }
    public synchronized void myWait() {
        try {
            wait();
        } catch (InterruptedException e) {System.err.println(e);
        }
    }

}
