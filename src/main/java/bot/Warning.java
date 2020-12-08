package bot;

public class Warning {
    public Warning(String r, long ltw) {
        warnTime = ltw;
        reason = r;
    }
    public String reason = "";
    public long warnTime = 0;
}
