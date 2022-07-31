package transactions.backend;

import javax.swing.*;

public class TwoPhaseCoord extends Process {
    boolean globalCommit = false;
    boolean donePhase1 = false;
    boolean noReceived = false;
    int numParticipants;
    int numReplies = 0;
    public TwoPhaseCoord(Linker initComm) {
        super(initComm);
        numParticipants = N - 1;
    }
    public synchronized  void doCoordinator(JTextArea textArea) {
        // Phase 1
        broadcastMsg("request", myId,textArea);
        while (!donePhase1)
            myWait();

        // Phase 2
        if (noReceived)
            broadcastMsg("finalAbort", myId,textArea);
        else {
            globalCommit = true;
            broadcastMsg("finalCommit", myId,textArea);
        }
    }
    public synchronized void handleMsg(Msg m, int src, String tag) {
        if (tag.equals("yes")) {
            numReplies++;
            if (numReplies == numParticipants) {
                donePhase1 = true;
                notify();
            }
        } else if (tag.equals("no")) {
            noReceived = true;
            donePhase1 = true;
            notify();
        }
    }
}
