package bot;

public class BMember {

    private final int rank;
    private final String name;
    private final int points;

    public BMember(int rank, String name, int points) {
        this.rank = rank;
        this.name = name;
        this.points = points;
    }

    public int getRank() {
        return rank;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }
}
