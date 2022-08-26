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

    /**
     * This method is called by the coordinator to start the two-phase
     * commit protocol. It should return true if the protocol succeeds,
     * false otherwise.
     *
     * @param initComm linker to the coordinator process
     * @param textArea text area to display messages
     */
    public TwoPhaseParticipant(Linker initComm, JTextArea textArea) {
        super(initComm);
        this.textArea = textArea;
    }


    /**
     * This method proposes a value to be committed.
     * @param vote value to be committed
     */
    public synchronized void propose(boolean vote) {
        localCommit = vote;
        hasProposed = true;
        notify();
    }

    /**
     * This method is called by the coordinator to wait for the
     * proposal to be committed. It should return true if the proposal
     * is committed, false otherwise.
     * @return true if the proposal is committed, false otherwise
     */
    public synchronized boolean decide() {
        while (!done) myWait();
        return globalCommit;
    }

    /**
     * This method handles the message received by the participant.
     * @param m message received
     * @param src source of the message
     * @param tag tag of the message
     */
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
