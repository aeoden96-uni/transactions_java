package transactions;

import java.io.*;

public class ListenerThread extends Thread {
    int channel;
    MsgHandler process;

    /**
     * Constructor
     *
     * @param channel the channel to listen on for incoming messages
     * @param process the MsgHandler to use for incoming messages
     */
    public ListenerThread(int channel, MsgHandler process) {
        this.channel = channel;
        this.process = process;
    }

    /**
     * Listen for incoming messages on the channel and pass them to the
     * MsgHandler for processing. This method is called by the start() method
     * of the thread.
     */
    public void run() {
        while (true) {
            try {
                Msg m = process.receiveMsg(channel);             
                process.handleMsg(m, m.getSrcId(), m.getTag());
            } catch (IOException e) {
                System.err.println(e);            
            }
        }
    }
}
