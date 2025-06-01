package app;

import servent.message.SendTokenMessage;
import servent.message.TokenRequestMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SuzukiKasamiState {

    private final int totalNodes = ChordState.CHORD_SIZE; // Total number of nodes in the system

    private boolean hasToken;  // Does this node currently hold the token
    private int[] requestNumbers; // Tracks the largest request number for each node
    private int[] LN;   // Token's usage count per node
    private Queue<Integer> requestQueue; // FIFO queue of pending requests
    private int sequenceNumber;

    public static final Object lock = new Object(); // Lock for synchronizing access to shared resources

    public SuzukiKasamiState() {
        this.requestNumbers = new int[totalNodes];
        this.LN = new int[totalNodes];
        this.requestQueue = new ArrayDeque<>();
        this.sequenceNumber = 0;
    }

    public int[] getRequestNumbers() {
        return requestNumbers;
    }

    public boolean hasToken() {
        return hasToken;
    }

    public void setHasToken(boolean hasToken) {
        AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " had token: " + this.hasToken + " now has token: " + hasToken);
        this.hasToken = hasToken;
    }

    public Queue<Integer> getRequestQueue() {
        return requestQueue;
    }

    public int[] getLN() {
        return LN;
    }

    public void requestCriticalSection() {
        synchronized (lock) {
            sequenceNumber++;
            requestNumbers[AppConfig.myServentInfo.getChordId()] = sequenceNumber;

            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " requesting critical section with RN[" + AppConfig.myServentInfo.getChordId() + "] = " + requestNumbers[AppConfig.myServentInfo.getChordId()]);

            if (!hasToken) {
                MessageUtil.sendMessage(new TokenRequestMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), AppConfig.myServentInfo.getChordId(), requestNumbers[AppConfig.myServentInfo.getChordId()], sequenceNumber));
            }

            while (!hasToken && (!requestQueue.isEmpty() && requestQueue.peek() != AppConfig.myServentInfo.getChordId())) {
                try {
                    lock.wait(); // Wait until the token is received
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                    AppConfig.timestampedErrorPrint("Thread interrupted while waiting for token: " + e.getMessage());
                }
            }

            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " has acquired the token and can enter the critical section.");
            requestQueue.remove(AppConfig.myServentInfo.getChordId());
        }
    }

    public void sendToken(int targetNodeId) {
        MessageUtil.sendMessage(new SendTokenMessage(AppConfig.myServentInfo.getListenerPort(), targetNodeId, AppConfig.myServentInfo.getChordId(), new ConcurrentLinkedQueue<>(requestQueue), LN));
    }

    public void receiveToken(int[] newTokenNumbers, Queue<Integer> newRequestQueue) {
        AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " received token from node " + newRequestQueue.peek() + ". " + newRequestQueue);
        this.hasToken = true;
        this.LN = newTokenNumbers.clone();
        this.requestQueue.addAll(newRequestQueue);

        lock.notifyAll();
    }

    public void releaseCriticalSection() {
        synchronized (lock) {
            LN[AppConfig.myServentInfo.getChordId()] = requestNumbers[AppConfig.myServentInfo.getChordId()];

            AppConfig.timestampedStandardPrint("Q when starting" + requestQueue.toString());
            for (int i = 0; i < ChordState.CHORD_SIZE; i++) {
                if (i != AppConfig.myServentInfo.getChordId() && requestNumbers[i] > LN[i]) {
                    requestQueue.add(i);
                }

            }
            AppConfig.timestampedStandardPrint("Q after for loop " + requestQueue.toString());

            if (!requestQueue.isEmpty() && hasToken) {
                int nextNode = requestQueue.poll();
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " releasing the token and sending it to the next node in the request queue. " + nextNode);
                sendToken(nextNode);
                hasToken = false;
            }
            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " released critical section");
        }
    }
}
