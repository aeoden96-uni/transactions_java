package transactions;
import java.util.*;import java.net.*;import java.io.*;

public class Connector {
    ServerSocket listener;  Socket [] link;

    /**
     * This method is used to create a socket connection to the name server.
     *
     * @param basename The name of the name server.
     * @param myId The id of the client.
     * @param numProc The number of processes in the system.
     * @param dataIn The input stream to read from.
     * @param dataOut The output stream to write to.
     * @throws Exception If there is an error creating the socket.
     */
    public void Connect(String basename, int myId, int numProc,
                        BufferedReader[] dataIn, PrintWriter[] dataOut) throws Exception {
        Name myNameclient = new Name();
        link = new Socket[numProc];
        int localport = getLocalPort(myId);
        listener = new ServerSocket(localport);

        /* register in the name server */
        myNameclient.insertName(basename + myId,
                (InetAddress.getLocalHost()).getHostName(), localport);

        /* accept connections from all the smaller processes */
        for (int i = 0; i < myId; i++) {
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
            String getline = dIn.readLine();
            StringTokenizer st = new StringTokenizer(getline);
            int hisId = Integer.parseInt(st.nextToken());
            int destId = Integer.parseInt(st.nextToken());
            String tag = st.nextToken();
            if (tag.equals("hello")) {
                link[hisId] = s;
                dataIn[hisId] = dIn;
                dataOut[hisId] = new PrintWriter(s.getOutputStream());
            }
        }
        /* contact all the bigger processes */
        for (int i = myId + 1; i < numProc; i++) {
            PortAddr addr;
            do {
                addr = myNameclient.searchName(basename + i);
                Thread.sleep(100);
            } while (addr.getPort() == -1);
            link[i] = new Socket(addr.getHostName(), addr.getPort());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new
                    InputStreamReader(link[i].getInputStream()));
            /* send a hello message to P_i */
            dataOut[i].println(myId +" "+ i +" "+ "hello" + " " + "null");
            dataOut[i].flush();
        }
    }

    /**
     * This method is used to get the local port number of the process.
     * @param id The id of the process.
     * @return The local port number of the process.
     */
    int getLocalPort(int id) { return Symbols.ServerPort + 10 + id; }

    /**
     * This method is used to close the socket connection to the name server.
     */
    public void closeSockets(){
        try {
            listener.close();
            for (int i=0;i<link.length; i++) link[i].close();
        } catch (Exception e) {System.err.println(e);}
    }
}
