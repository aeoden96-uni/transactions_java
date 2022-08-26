package transactions;

import java.io.*;

/**
 * This class is used to send and receive messages.
 *
 */
public interface MsgHandler {

    /**
     * Send a message to the specified destination.
     *
     * @param m The message to send.
     * @param srcId The id of the source.
     * @param tag The tag of the message.
     */
    public void handleMsg(Msg m, int srcId, String tag);

    /**
     * Receive a message from the specified source.
     *
     * @param fromId The id of the source.
     * @return The message received.
     * @throws IOException If there is an error receiving the message.
     */
    public Msg receiveMsg(int fromId) throws IOException;
}
