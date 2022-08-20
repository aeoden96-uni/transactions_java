package transactions;

import javax.swing.*;

public class TwoPhaseParticipant extends Process {
    boolean localCommit;
    boolean globalCommit;
    boolean done = false;
    boolean hasProposed = false;


    public TwoPhaseParticipant(Linker initComm) {
        super(initComm);
    }

    public TwoPhaseParticipant(Linker initComm, JTextArea textArea) {
        this(initComm);
        this.textArea = textArea;
    }

    public synchronized void propose(boolean vote) {
        localCommit = vote;
        hasProposed = true;
        notify();
    }
    public synchronized boolean decide() {
        while (!done) myWait();
        return globalCommit;
    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
        while (!hasProposed) myWait();
        if (tag.equals("request")) {
            if (localCommit)
                sendMsg(src, "yes");
            else
                sendMsg(src, "no");
        } else if (tag.equals("finalCommit")) {
            globalCommit = true;
            done = true;
            notify();
        } else if (tag.equals("finalAbort")) {
            globalCommit = false;
            done = true;
            notify();
        }
    }
}
