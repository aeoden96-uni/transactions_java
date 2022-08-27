package transactions;

import javax.swing.*;
/**TwoPhaseParticipant is a process that participates in the two-phase commit protocol.
 * It is responsible for sending prepare message to the coordinator,
 * and receiving the replies.
 * If all replies are received, it sends the commit message to the coordinator.
 *
 */
public class TwoPhaseParticipant extends Process {

    boolean globalCommit;



    boolean doneAcknowledgingPhase = false;
    boolean done = false;


    boolean hasAcknowledged = false;
    boolean hasVoted = false;
    boolean myVote;



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
    public synchronized void vote(boolean vote) {
        myVote = vote;
        hasVoted = true;
        notify();
    }

    public synchronized void acknowledge() {
        hasAcknowledged = true;
        notify();
    }

    /**
     * This method is called by the coordinator to wait for the
     * proposal to be committed. It should return true if the proposal
     * is committed, false otherwise.
     * @return true if the proposal is committed, false otherwise
     */
    public synchronized boolean coordResult() {
        while (!done) myWait();
        return globalCommit;
    }


    @Override
    public void sendMsg(int destId, String tag) {

        if(tag.equals("acknowledge")) {
            Util.println(  "Sending ⇨\n"
                    + "destination: " + destId
                    + " | tag: " + tag + "\n"
                    + "msg: " + "" + "\n",textArea);

            comm.sendMsg(destId, tag, "Acknowledged.");
            return;
        }

        Util.println(  "Sending ⇨\n"
                + "destination: " + destId
                + " | tag: " + tag + "\n"
                + "msg : " + "message_"+ myId + "\n",textArea);

        comm.sendMsg(destId, tag, "message_"+ myId);
    }

    /**
     * This method handles the message received by the participant.
     * @param m message received
     * @param src source of the message
     * @param tag tag of the message
     */
    public synchronized void handleMsg(Msg m, int src, String tag) {
        while (!hasVoted) myWait();
        switch (tag) {
            case "request":
                if (myVote)
                    sendMsg(src, "yes");
                else
                    sendMsg(src, "no");
                break;
            case "finalCommit":
                doneAcknowledgingPhase = true;
                globalCommit = true;
                done = true;
                notify();
                while (!hasAcknowledged) myWait();
                sendMsg(src, "acknowledge");
                break;
            case "finalAbort":
                doneAcknowledgingPhase = true;
                globalCommit = false;
                done = true;
                notify();
                while (!hasAcknowledged) myWait();
                sendMsg(src, "acknowledge");
                break;
            default:
                Util.println("Unknown tag: " + tag, textArea);
                break;
        }
    }
}
