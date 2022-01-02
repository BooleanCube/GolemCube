package bot;

import java.util.List;

public class ReputationsResult {
    private final BMember member;
    private final List<List<BMember>> memberReputations;

    public ReputationsResult(BMember member, List<List<BMember>> memberReputations) {
        this.member = member;
        this.memberReputations = memberReputations;
    }

    public BMember getMember() {
        return member;
    }

    public List<List<BMember>> getMemberReputations() {
        return memberReputations;
    }

    public static class BMember {
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

}
