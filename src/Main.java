import java.util.List;

public class Main {

    public static void main(String[] args) {
        ShortestPath<State> finder
                = new BFS<>();

        long ta = System.currentTimeMillis();

        List<State> path
                = finder.search(State.getInitialStateNode(5, 5, 3),
                        (State node)
                        -> {
                    return node.isSolutionState();
                });

        long tb = System.currentTimeMillis();

        System.out.println("Duration: " + (tb - ta) + " milliseconds.");

        int fieldLength = ("" + path.size()).length();

        if (path.isEmpty()) {
            System.out.println("No solution.");
        } else {
            for (int i = 0; i < path.size(); ++i) {
                System.out.printf("State %" + fieldLength + "d: %s\n",
                        (i + 1),
                        path.get(i));
            }
        }
    }
}
