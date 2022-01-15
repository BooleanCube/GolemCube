package bot.listeners.moderation;

public class MessageHistory {
    public MessageHistory(int msgNum, long lastTimeSent) {
        this.msgNum = msgNum;
        this.lastTimeSent = lastTimeSent;
    }

    int msgNum;
    long lastTimeSent;
}
