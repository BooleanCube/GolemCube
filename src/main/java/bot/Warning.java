package bot;

public class Warning {
    public String reason;
    public long warnTime;

    public Warning(String r, long ltw) {
        warnTime = ltw;
        reason = r;
    }
}
