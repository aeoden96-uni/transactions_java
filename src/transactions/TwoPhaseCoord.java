package transactions;

import javax.swing.*;
import java.util.Objects;

public class TwoPhaseCoord extends Process {
    boolean globalCommit = false;
    boolean doneVotingPhase = false;

    boolean doneAcknowledgingPhase = false;
    boolean noReceived = false;
    int numParticipants;
    int numReplies = 0;
    int numAknowledged = 0;


    /**TwoPhaseCoord is a process that coordinates the two-phase commit protocol.
     * It is responsible for sending prepare message to all other processes,
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
     * Phase 2: send commit messages to all participants.
     * If any participant replies with a reject message,
     * then the coordinator aborts the transaction.
     *
     * @return finalAbort if the transaction is aborted, finalCommit if the transaction is committed.
     */


    public synchronized void votingPhase() {
        // Phase 1
        broadcastMsg("request");
        while (!doneVotingPhase)
            myWait();
    }

    public synchronized void sendCommit() {
        // Phase 2
        if (noReceived) {
            broadcastMsg("finalAbort");
        }
        else {
            globalCommit = true;
            broadcastMsg("finalCommit");
        }
    }

    public synchronized void acknowledgePhase() {
        while (!doneAcknowledgingPhase)
            myWait();
    }

    public synchronized String finalPhase(){
        if (noReceived) {
            return "finalAbort " + myId;
        }
        else {
            return "finalCommit " + myId;
        }
    }


    public synchronized String doCoordinator() {
        votingPhase();

        sendCommit();

        acknowledgePhase();

        return finalPhase();
    }

    @Override
    public void sendMsg(int destId, String tag) {

        Util.println(  "\nSending ⇨\n"
                + "destination: " + destId
                + " | tag: " + tag + "\n"
                + "msg: " + myId + "\n",textArea);

        comm.sendMsg(destId, tag, String.valueOf(myId));
    }


    private String messageDecrypt(String encryptedMessage){
        return AES.decrypt(encryptedMessage);
    }

    @Override
    public Msg receiveMsg(int fromId) {
        Msg m = super.receiveMsg(fromId);
        return m;
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
                doneVotingPhase = true;
                notify();
            }
        } else if (tag.equals("no")) {
            noReceived = true;
            doneVotingPhase = true;
            notify();
        } else if (tag.equals("acknowledge")) {
            numAknowledged++;
            if (numAknowledged == numParticipants) {
                doneAcknowledgingPhase = true;
                notify();
            }
        }
    }
}
