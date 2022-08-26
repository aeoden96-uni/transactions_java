package transactions;

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

    /**TwoPhaseCoord is a process that coordinates the two-phase commit protocol.
     * It is responsible for sending the prepare message to all other processes,
     * and receiving the replies.
     * If all replies are received, it sends the commit message to all other processes.
     *
     * @param initComm the linker object that contains the sockets to all other processes.
     * @param textArea the text area where the messages are displayed.
     */
    public TwoPhaseCoord(Linker initComm, JTextArea textArea) {
        super(initComm);
        numParticipants = N - 1;
        this.textArea = textArea;
    }

    /**
     * Phase 1: send prepare messages to all participants.
     *
     * Phase 2: send commit messages to all participants.
     * If any participant replies with a reject message,
     * then the coordinator aborts the transaction.
     *
     *
     * @return true if the transaction can be committed, false otherwise.
     *
     * @throws Exception if an error occurs.
     *
     *
     *
     * @return
     */
    public synchronized  String doCoordinator() {
        // Phase 1
        broadcastMsg("request", myId);
        while (!donePhase1)
            myWait();

        // Phase 2
        if (noReceived) {
            broadcastMsg("finalAbort", myId);
            return "finalAbort " + myId;
        }
        else {
            globalCommit = true;
            broadcastMsg("finalCommit", myId);
            return "finalCommit " + myId;
        }
    }

    /**
     * Handle a message received from a participant.
     *
     * @param m the message received.
     * @param src the id of the participant who sent the message.
     * @param tag the tag of the message.
     */
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
