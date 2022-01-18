package bot.listeners.moderation;

public class MessageHistory {
    int msgNum;
    long lastTimeSent;

    public MessageHistory(int msgNum, long lastTimeSent) {
        this.msgNum = msgNum;
        this.lastTimeSent = lastTimeSent;
    }
}
