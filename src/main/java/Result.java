import java.util.List;
import java.util.stream.Collectors;

public class Result {

    int numberOfSolutions;
    List<List<Integer>> combinations;
    int waste;

    public Result(int numberOfSolutions, List<List<Integer>> combinations, int waste) {
        this.numberOfSolutions = numberOfSolutions;
        this.combinations = combinations;
        this.waste = waste;
    }

    @Override
    public String toString() {
        return "Nr solutions=" + combinations.size() + "\n" +
                combinations.stream()
                        .map(c -> c.stream()
                                .map(i -> Integer.toString(i))
                                .collect(Collectors.joining(" ")))
                        .collect(Collectors.joining("\n")) + "\n" +
                "Waste=" + 0;
    }
}