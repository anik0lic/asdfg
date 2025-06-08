package app;

import servent.message.PingMessage;
import servent.message.RemoveNodeMessage;
import servent.message.RequestConfirmation;
import servent.message.util.MessageUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FailureDetection implements Runnable {

    public static final long WEAK_FAILURE_THRESHOLD = 4000;
    public static final long STRONG_FAILURE_THRESHOLD = 10000;

    private Map<Integer, Long> lastSeen = new ConcurrentHashMap<>();
    private Map<Integer, Boolean> susNodes = new ConcurrentHashMap<>();
    private Set<Integer> deadNodes = new HashSet<>();

    @Override
    public void run() {
        while (true) {
            long currentTime = System.currentTimeMillis();

            for (Map.Entry<Integer, Long> entry : lastSeen.entrySet()) {
                int nodeId = entry.getKey();
                long time = entry.getValue();

                if (currentTime - time > WEAK_FAILURE_THRESHOLD && !susNodes.containsKey(nodeId)) {
                    susNodes.put(nodeId, true);

                    int successorPort = AppConfig.chordState.getSuccessorTable()[0].getListenerPort();
                    AppConfig.timestampedStandardPrint("Node " + nodeId + " marked as suspicious. Requesting confirmation from " + successorPort);
                    MessageUtil.sendMessage(new RequestConfirmation(AppConfig.myServentInfo.getListenerPort(), successorPort, nodeId));

                }

                if (currentTime - time > STRONG_FAILURE_THRESHOLD && deadNodes.contains(nodeId)) {
                    MessageUtil.sendMessage(new RemoveNodeMessage(nodeId, AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getPredecessorValueMap(), AppConfig.chordState.getSuccessorTable()[AppConfig.chordState.getSuccessorTable().length - 1].getListenerPort()));
                    lastSeen.remove(nodeId);
                    susNodes.remove(nodeId);
                    deadNodes.remove(nodeId);
                }
            }

            sendPingToPredecessor();

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void updateLastSeen(int nodeId) {
        lastSeen.put(nodeId, System.currentTimeMillis());
        susNodes.remove(nodeId);
        AppConfig.timestampedStandardPrint("Updated last seen for node " + nodeId);
    }

    private void sendPingToPredecessor() {
        int predecessorPort = AppConfig.chordState.getPredecessor().getListenerPort();

        lastSeen.put(predecessorPort, -1L);

        PingMessage pingToPredecessor = new PingMessage(AppConfig.myServentInfo.getListenerPort(), predecessorPort);

        MessageUtil.sendMessage(pingToPredecessor);

        AppConfig.timestampedStandardPrint("Sent PING to predecessor: " + predecessorPort);
    }

    public Long getLastSeen(int nodeId) {
        return lastSeen.get(nodeId);
    }

    public Set<Integer> getDeadNodes() {
        return deadNodes;
    }
}
