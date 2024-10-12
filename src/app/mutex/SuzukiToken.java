package app.mutex;

import java.io.Serializable;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SuzukiToken implements Serializable {
    private Queue<Integer> queue; // Queue of nodes waiting for the token
    private Map<Integer, Integer> LN; // Last known sequence number for each node

    // Constructor initializes the queue and LN map
    public SuzukiToken() {
        queue = new ConcurrentLinkedQueue<>();
        LN = new ConcurrentHashMap<>();
    }

    // Private constructor for creating a copy of the token
    private SuzukiToken(Queue<Integer> queue, Map<Integer, Integer> LN) {
        this.queue = new ConcurrentLinkedQueue<>(queue);
        this.LN = new ConcurrentHashMap<>(LN);
    }

    // Method to get a copy of the token
    public SuzukiToken getCopy() {
        return new SuzukiToken(queue, LN);
    }

    // Constructor initializes the queue
    public SuzukiToken(Queue<Integer> queue) {
        this.queue = new ConcurrentLinkedQueue<>(queue);
    }

    // Method to add a node to the queue
    public void addToQueue(int serventPort) {
        this.queue.add(serventPort);
    }

    // Getter for the queue
    public Queue<Integer> getQueue() {
        return queue;
    }

    // Getter for the LN map
    public Map<Integer, Integer> getLN() {
        return LN;
    }

    // Method to get the first node from the queue
    public int getFirstFromQueue() {
        if (!queue.isEmpty()) {
            return queue.remove();
        }
        return -1;
    }

    // Method to update the last known sequence number for a node
    public void updateLastNumber(Integer myServentPort, int sequenceNumber) {
        LN.put(myServentPort, sequenceNumber);
    }

    // toString method to return a string representation of the token
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SuzukiToken {\n");
        sb.append("  LN: {");
        for (Map.Entry<Integer, Integer> entry : LN.entrySet()) {
            sb.append("Port ").append(entry.getKey()).append(": ").append(entry.getValue()).append(", ");
        }
        sb.append("},\n");
        sb.append("  Queue: [");
        for (Integer port : queue) {
            sb.append(port).append(", ");
        }
        sb.append("]\n");
        sb.append("}");
        return sb.toString();
    }
}
