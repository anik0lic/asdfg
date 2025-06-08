package app;

import servent.message.SendTokenMessage;
import servent.message.TokenRequestMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SuzukiKasamiState {

    private final int totalNodes = ChordState.CHORD_SIZE;

    private boolean hasToken;
    private int[] requestNumbers;
    private int[] LN;
    private Queue<Integer> requestQueue;
    private int sequenceNumber;
    private boolean inCriticalSection;
    private boolean waiting;

    public Lock lock = new ReentrantLock();
    public Condition tokenCondition;

    public SuzukiKasamiState() {
        requestNumbers = new int[totalNodes];
        this.LN = new int[totalNodes];
        this.requestQueue = new LinkedBlockingQueue<>();
        this.sequenceNumber = 0;

        this.tokenCondition = lock.newCondition();

        this.hasToken = false;
        this.inCriticalSection = false;
        this.waiting = false;
    }

    public int[] getRequestNumbers() {
        return requestNumbers;
    }

    public boolean hasToken() {
        return hasToken;
    }

    public boolean isInCriticalSection() {
        return inCriticalSection;
    }

    public boolean isWaiting() {
        return waiting;
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

    public void setLN(int[] LN) {
        this.LN = LN;
    }

    public void setRequestQueue(Queue<Integer> requestQueue) {
        this.requestQueue = requestQueue;
    }

    public void requestCriticalSection() {
        lock.lock();
        try {
            if (hasToken) {
                inCriticalSection = true;
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " already have token, entering critical section.");
                return;
            }

            sequenceNumber++;
            requestNumbers[AppConfig.myServentInfo.getChordId()] = sequenceNumber;
            waiting = true;

            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " requesting critical section with RN[" + AppConfig.myServentInfo.getChordId() + "] = " + requestNumbers[AppConfig.myServentInfo.getChordId()]);

            if (!hasToken) {
                MessageUtil.sendMessage(new TokenRequestMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), AppConfig.myServentInfo.getChordId(), requestNumbers[AppConfig.myServentInfo.getChordId()], sequenceNumber));
            }

            while (!hasToken) {
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " is waiting for token...");
                tokenCondition.await();
            }

            waiting = false;
            inCriticalSection = true;
            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " has acquired the token and can enter the critical section.");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    public void sendToken(int targetNodeId) {
        if (!hasToken) {
            AppConfig.timestampedErrorPrint("Node " + AppConfig.myServentInfo.getChordId() + " cannot send token, it does not have it.");
            return;
        }

        MessageUtil.sendMessage(new SendTokenMessage(AppConfig.myServentInfo.getListenerPort(), targetNodeId, AppConfig.myServentInfo.getChordId(), new LinkedBlockingQueue<>(requestQueue), Arrays.copyOf(LN, LN.length)));
        hasToken = false;
    }

    public void releaseCriticalSection() {
        lock.lock();
        try {
            if (!hasToken) {
                AppConfig.timestampedErrorPrint("Node " + AppConfig.myServentInfo.getChordId() + " cannot release critical section, it does not have the token.");
                return;
            }
            if (!inCriticalSection) {
                AppConfig.timestampedErrorPrint("Node " + AppConfig.myServentInfo.getChordId() + " is not in critical section, cannot release it.");
                return;
            }
            inCriticalSection = false;
            LN[AppConfig.myServentInfo.getChordId()] = requestNumbers[AppConfig.myServentInfo.getChordId()];
            AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " is releasing the critical section.");

            for (int i = 0; i < ChordState.CHORD_SIZE; i++) {
                if (i != AppConfig.myServentInfo.getChordId() && requestNumbers[i] == LN[i] + 1 && !requestQueue.contains(i)) {
                    requestQueue.add(i);
                    AppConfig.timestampedStandardPrint("Added node " + i + " to the request queue. Current queue: " + requestQueue);
                }

            }

            if (!requestQueue.isEmpty()) {
                int nextNode = requestQueue.poll();
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " releasing the token and sending it to the next node in the request queue. " + nextNode);
                sendToken(nextNode);
            } else {
                AppConfig.timestampedStandardPrint("Node " + AppConfig.myServentInfo.getChordId() + " has no more requests in the queue, keeping the token.");
            }
        } finally {
            lock.unlock();
        }
    }
}
