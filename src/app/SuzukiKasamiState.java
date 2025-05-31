package app;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SuzukiKasamiState {

    private int nodeId;  // Node's unique ID
    private final int totalNodes = ChordState.CHORD_SIZE; // Total number of nodes in the system

    private boolean hasToken;  // Does this node currently hold the token
    private int[] requestNumbers; // Tracks the largest request number for each node
    private int[] tokenNumbers;   // Token's usage count per node
    private Queue<Integer> requestQueue; // FIFO queue of pending requests

    public SuzukiKasamiState() {
        this.requestNumbers = new int[totalNodes];
        this.tokenNumbers = new int[totalNodes];
        this.requestQueue = new ConcurrentLinkedQueue<>();
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
    }

    public synchronized void requestCriticalSection() {
        requestNumbers[nodeId]++;
        broadcastRequest();
    }

    public synchronized boolean canEnterCriticalSection() {
        return hasToken;
    }

    public synchronized void enterCriticalSection() {
        if (!hasToken) {
            throw new IllegalStateException("Node doesn't have the token to enter critical section.");
        }
    }

    public synchronized void releaseCriticalSection() {
        // Increment this node’s tokenNumbers count
        tokenNumbers[nodeId]++;

        // Pass the token to the next node in the request queue
        if (!requestQueue.isEmpty()) {
            int nextNode = requestQueue.poll();
            passToken(nextNode);
        }
    }

    private void broadcastRequest() {
        for (int i = 0; i < totalNodes; i++) {
            if (i == nodeId) continue;

            // Send a request message to all other nodes
            // RequestMessage includes: requesterId and requestNumber
            sendRequestMessage(i, nodeId, requestNumbers[nodeId]);
        }
    }

    private void passToken(int nextNodeId) {
        // Simulate token being sent to the next node
        hasToken = false;

        // Notify the next node to grant it the token
        sendToken(nextNodeId, tokenNumbers);
    }

    private void sendRequestMessage(int targetNodeId, int requesterId, int requestNumber) {
        // Logic to send a request message to a specific node
        // (communication simulated via messaging in a distributed environment)
    }

    private void sendToken(int targetNodeId, int[] tokenNumbers) {
        // Send the token to the specified node
        // TokenMessage includes: senderId, tokenNumbers, and requestQueue
    }

    public synchronized void receiveRequestMessage(int senderId, int requestNumber) {
        // Update the requestNumbers array
        requestNumbers[senderId] = Math.max(requestNumbers[senderId], requestNumber);

        // If this node has the token, check if the request can be granted
        if (hasToken && !requestQueue.contains(senderId) && requestNumber == tokenNumbers[senderId] + 1) {
            requestQueue.add(senderId);
        }
    }

    public synchronized void receiveToken(int[] newTokenNumbers, Queue<Integer> newRequestQueue) {
        this.hasToken = true;
        this.tokenNumbers = newTokenNumbers.clone();
        this.requestQueue.addAll(newRequestQueue);
    }

}
