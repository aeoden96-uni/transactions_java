package transactions;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class Topology {

    /**
     * This method reads network topology from linked list of nodes.
     * @param myId This is the id of the node that is reading the topology.
     * @param N This is the number of nodes in the network.
     * @param neighbors This is the linked list of neighbors of the node.
     * @param textArea This is the text area where the topology is displayed.
     */
    public static void readNeighbors(int myId, int N,
                                     IntLinkedList neighbors, JTextArea textArea) {
        textArea.append("Reading topology\n");
        try {
            BufferedReader dIn = new BufferedReader(
                    new FileReader("topology" + myId));
            StringTokenizer st = new StringTokenizer(dIn.readLine());
            while (st.hasMoreTokens()) {
                int neighbor = Integer.parseInt(st.nextToken());
                neighbors.add(neighbor);
            }
        } catch (FileNotFoundException e) {
            for (int j = 0; j < N; j++)
                if (j != myId) neighbors.add(j);
        } catch (IOException e) {
            System.err.println(e);
        }
        textArea.append(neighbors.toString() + "\n");
    }
}
