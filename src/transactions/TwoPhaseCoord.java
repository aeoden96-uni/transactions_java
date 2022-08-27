package transactions;

import javax.swing.*;
import java.util.Objects;

public class TwoPhaseCoord extends Process {
    boolean globalCommit = false;
    boolean donePhase1 = false;
    boolean noReceived = false;
    int numParticipants;
    int numReplies = 0;


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
    public synchronized String doCoordinator() {
        // Phase 1
        broadcastMsg("request");
        while (!donePhase1)
            myWait();

        // Phase 2
        if (noReceived) {
            broadcastMsg("finalAbort");
            return "finalAbort " + myId;
        }
        else {
            globalCommit = true;
            broadcastMsg("finalCommit");
            return "finalCommit " + myId;
        }
    }
    @Override
    public void sendMsg(int destId, String tag) {

        Util.println(  "Sending ⇨\n"
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

        String decryptedMessage = messageDecrypt(m.getMessage());
        textArea.append("Decrypted message: " + decryptedMessage + "\n");

        if(!Objects.equals(decryptedMessage, "correct_message_" + fromId)) {
            textArea.append("Message from " + fromId + " is wrong\n\n");
            return new Msg(m.srcId, m.destId, "no", "wrong_message_" + fromId);
        }
        textArea.append("Message from " + fromId + " is OK\n\n");
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
