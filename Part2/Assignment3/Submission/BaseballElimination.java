import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {

    private int teamNums;
    private Queue<String> teamNames;
    private Team[] teams;
    private boolean eliminated[];

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        teamNames = new Queue<>();
        teamNums = 0;
        storeData(filename);
        eliminateTrivial();
        eliminateNontrivial();
    }

    private void storeData(String filename) {
        In in = new In(filename);

        teamNums = in.readInt();
        eliminated = new boolean[teamNums];
        teams = new Team[teamNums];

        int i = 0;
        while (i < teamNums) {
            String teamName = in.readString();
            int win = in.readInt();
            int loss = in.readInt();
            int left = in.readInt();
            int[] games = new int[teamNums];
            int j = 0;
            while (j < teamNums) {
                games[j] = in.readInt();
                j++;
            }
            Team team = new Team(teamName, win, loss, left, games);
            teams[i] = team;
            teamNames.enqueue(teamName);
            i++;
        }
    }

    private FlowNetwork constructNetwork(int teamNo) {
        FlowNetwork network;
        Team selectedTeam = teams[teamNo];
        Team[] newTeams = new Team[teamNums - 1];
        int a = 0;
        for (int i = 0; i < teamNums; i++)
            if (i != teamNo) newTeams[a++] = teams[i];

        int teamNums = newTeams.length;
        int gameNum = (teamNums - 1) * teamNums / 2;
        int V = 2 + gameNum + teamNums;
        network = new FlowNetwork(V);
        int gameVertices = 1;
        for (int i = 0; i < teamNums - 1; i++) {
            int[] games = newTeams[i].games;
            for (int j = i + 1; j < teamNums; j++) {
                FlowEdge edge = new FlowEdge(0, gameVertices, games[j]);
                network.addEdge(edge);
                gameVertices++;
            }
        }

        int teamVertices = 1 + gameNum;
        gameVertices = 1;
        double infinityCap = Double.POSITIVE_INFINITY;
        for (int i = 0; i < teamNums - 1; i++) {
            for (int j = i + 1; j < teamNums; j++) {
                FlowEdge edge1 = new FlowEdge(gameVertices, teamVertices + i, infinityCap);
                FlowEdge edge2 = new FlowEdge(gameVertices, teamVertices + j, infinityCap);
                network.addEdge(edge1);
                network.addEdge(edge2);
                gameVertices++;
            }
        }

        for (int i = 0; i < teamNums; i++) {
            int leftWin = selectedTeam.win + selectedTeam.left - teams[i].win;
            if (leftWin > 0) {
                FlowEdge edge = new FlowEdge(teamVertices + i, V - 1, leftWin);
                network.addEdge(edge);
            } else {
                FlowEdge edge = new FlowEdge(teamVertices + i, V - 1, 0);
                network.addEdge(edge);
            }
        }

        return network;
    }

    private void eliminateTrivial() {
        for (int i = 0; i < teamNums; i++) {
            Team team = teams[i];
            for (int j = 0; j < teamNums; j++) {
                Team temp = teams[j];
                if (team.win + team.left <= temp.win) {
                    eliminated[i] = true;
                }
            }
        }
    }

    private void eliminateNontrivial() {
        for (int i = 0; i < teamNums; i++) {
            if (!eliminated[i]) {
                FlowNetwork network = constructNetwork(i);
                int newTeamNums = teamNums - 1;
                int gameNums = (newTeamNums - 1) * newTeamNums / 2;
                int V = 2 + gameNums + newTeamNums;
                FordFulkerson ford = new FordFulkerson(network, 0, V - 1);
                for (int j = 0; j < gameNums; j++) {
                    if (ford.inCut(j + 1)) {
                        eliminated[i] = true;
                    }
                }
            }
        }
    }

    private class Team {
        String teamName;
        int win, loss, left;
        int[] games;

        public Team(String teamName, int win, int loss, int left, int[] games) {
            this.teamName = teamName;
            this.win = win;
            this.loss = loss;
            this.left = left;
            this.games = games;
        }
    }

    // number of teamNames
    public int numberOfTeams() {
        return teamNums;
    }

    // all teamNames
    public Iterable<String> teams() {
        return teamNames;
    }

    // number of wins for given team
    public int wins(String team) {
        if (!validateVertices(team)) throw new IllegalArgumentException();

        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team.equals(temp.teamName)) {
                return temp.win;
            }
        }
        return 0;
    }

    private boolean validateVertices(String team) {
        if (team == null) return false;

        for (int i = 0; i < teamNums; i++) {
            if (teams[i].teamName.equals(team)) {
                return true;
            }
        }
        return false;
    }

    // number of losses for given team
    public int losses(String team) {
        if (!validateVertices(team)) throw new IllegalArgumentException();

        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team.equals(temp.teamName)) {
                return temp.loss;
            }
        }
        return 0;
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (!validateVertices(team)) throw new IllegalArgumentException();

        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team.equals(temp.teamName)) {
                return temp.left;
            }
        }
        return 0;
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (!validateVertices(team1) || !validateVertices(team2)) throw new IllegalArgumentException();

        Team team = null;
        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team1.equals(temp.teamName)) {
                team = temp;
                break;
            }
        }

        if (team == null) throw new IllegalArgumentException();

        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team2.equals(temp.teamName)) {
                return team.games[i];
            }
        }

        return 0;
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (!validateVertices(team)) throw new IllegalArgumentException();

        for (int i = 0; i < teamNums; i++) {
            Team temp = teams[i];
            if (team.equals(temp.teamName)) {
                return eliminated[i];
            }
        }
        return false;
    }

    // subset R of teamNames that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (!validateVertices(team)) throw new IllegalArgumentException();

        double teamWinTotal = wins(team) + remaining(team);

        if (isEliminated(team)) {
            double winsTotal = 0;
            int checkTeams = 0;
            Queue<String> certiTeamNames = new Queue<>();
            Queue<Team> certiTeams = new Queue<>();
            for (int i = 0; i < teamNums; i++) {
                Team temp = teams[i];
                if (!isEliminated(temp.teamName)) {
                    winsTotal += temp.win;

                    for (Team t : certiTeams) {
                        winsTotal += t.games[i];
                    }

                    checkTeams++;
                    certiTeamNames.enqueue(temp.teamName);
                    certiTeams.enqueue(temp);
                }
                if (winsTotal / checkTeams - teamWinTotal > 1e-6) {
                    break;
                }
            }
            return certiTeamNames;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
