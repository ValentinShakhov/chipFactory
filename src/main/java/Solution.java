import java.util.*;
import java.util.stream.Collectors;

class Solution {

    public static Result solveAndPrint(Integer[] machines, int limit) {
        Result result = solve(machines, limit);
        System.out.println(result);
        return result;
    }

    public static Result solve(Integer[] machines, int limit) {
        if (machines.length == 0) {
            return new Result(0, Collections.emptySet(), 0);
        }

        Set<Set<Machine>> combinations = getCombinations(machines, limit);

        if (combinations == null) {
            return new Result(0, Collections.emptySet(), 0);
        }

        return buildMinWasteResult(limit, combinations);
    }

    private static Set<Set<Machine>> getCombinations(Integer[] machines, int limit) {
        //Map containing every intermediate sum with its possible combinations (to avoid extra traversals)
        Map<Integer, Set<Set<Machine>>> sumToCombinationsOfMachineIndexes = new HashMap<>();

        //Used to filter out outperforming machines with too high waste
        Optional<Integer> minWasteOutperformingPerformance = Arrays.stream(machines).sorted().filter(m -> m > limit).findFirst();

        //Filling up the map
        //Increasing the number of machines with each step
        for (int numberOfMachines = 1; numberOfMachines <= machines.length; numberOfMachines++) {
            //Calculating for each possible intermediate sum
            for (int intermediateSum = 1; intermediateSum <= limit; intermediateSum++) {
                Set<Set<Machine>> combinationsThatGiveRequiredSum = new HashSet<>();
                //Calculating for each machine
                for (int i = 0; i < numberOfMachines; i++) {
                    Set<Set<Machine>> combinationsWithGivenNumberOfMachines = new HashSet<>();

                    Machine curMachine = new Machine(i, machines[i]);
                    if (!minWasteOutperformingPerformance.isPresent() || curMachine.performance <= minWasteOutperformingPerformance.get()) {
                        int diffToFill = intermediateSum - curMachine.performance;
                        //Current machine is a good combination itself
                        if (diffToFill == 0) {
                            combinationsWithGivenNumberOfMachines.add(createSingleMachineCombination(curMachine));
                        }

                        //Fetching existing combinations that fill the difference between the sum required at this step and the current machine performance
                        //An outperforming combination with minimum waste is picked if none found
                        if (diffToFill > 0) {
                            Set<Set<Machine>> existingCombinationsToFillTheDiff = findExistingCombinationsToFillTheDiffWithPossibleWaste(sumToCombinationsOfMachineIndexes, diffToFill);
                            //Creating new combinations based on existing ones, where current machine is not present yet
                            combinationsWithGivenNumberOfMachines.addAll(combineWithExistingCombinations(curMachine, existingCombinationsToFillTheDiff));
                        }
                        //Storing calculated combinations for current intermediate sum
                        combinationsWithGivenNumberOfMachines.stream()
                                .filter(cur -> !combinationsThatGiveRequiredSum.contains(cur))
                                .forEach(cur -> combinationsThatGiveRequiredSum.addAll(combinationsWithGivenNumberOfMachines));
                    }
                }

                //Storing new combinations for current intermediate sum to the map
                sumToCombinationsOfMachineIndexes.merge(intermediateSum,
                        combinationsThatGiveRequiredSum,
                        (existingCombinations, newCombinations) -> {
                            existingCombinations.addAll(newCombinations);
                            return existingCombinations;
                        });
            }
        }

        //Only the combinations for required sum are returned, intermediate results are ignored
        //If none found, pick the outperforming one with minimum waste
        Set<Set<Machine>> combinations = sumToCombinationsOfMachineIndexes.get(limit);
        return combinations.isEmpty() ? getOutperformingCombinationWithMinWaste(machines, limit) : combinations;
    }

    private static Set<Set<Machine>> findExistingCombinationsToFillTheDiffWithPossibleWaste(Map<Integer, Set<Set<Machine>>> sumToCombinationsOfMachineIndexes, int diffToFill) {
        Set<Set<Machine>> result = new HashSet<>();

        int diffToFillWithPossibleWaste = diffToFill;
        while (result.isEmpty() && sumToCombinationsOfMachineIndexes.keySet().size() >= diffToFillWithPossibleWaste) {
            Set<Set<Machine>> combinationsToFillTheDiff = sumToCombinationsOfMachineIndexes.getOrDefault(diffToFillWithPossibleWaste, Collections.emptySet());
            if (combinationsToFillTheDiff.isEmpty()) {
                diffToFillWithPossibleWaste++;
            } else {
                result.addAll(combinationsToFillTheDiff);
            }
        }

        return result;
    }

    private static Set<Machine> createSingleMachineCombination(Machine curMachine) {
        Set<Machine> singleMachineCombination = new HashSet<>();
        singleMachineCombination.add(curMachine);
        return singleMachineCombination;
    }

    private static List<Set<Machine>> combineWithExistingCombinations(Machine curMachine, Set<Set<Machine>> existingCombinationsToFillTheDiff) {
        return existingCombinationsToFillTheDiff.stream()
                .filter(existingCombination -> !existingCombination.contains(curMachine))
                .map(existingCombination -> {
                    Set<Machine> combinationWithExistingCombination = new HashSet<>(existingCombination);
                    combinationWithExistingCombination.add(curMachine);
                    return combinationWithExistingCombination;
                }).collect(Collectors.toList());
    }

    private static Set<Set<Machine>> getCombinationsWithMinWaste(int limit, Set<Set<Machine>> internalCombinations, int minWaste) {
        return internalCombinations.stream()
                .filter(c -> c.stream()
                        .mapToInt(m -> m.performance)
                        .sum() - limit == minWaste)
                .collect(Collectors.toSet());
    }

    private static Integer calculateMinWaste(int limit, Set<Set<Machine>> internalCombinations) {
        return internalCombinations.stream()
                .map(combinationMachines -> combinationMachines.stream()
                        .mapToInt(m -> m.performance)
                        .sum())
                .map(compoundPerformance -> compoundPerformance - limit)
                .min(Integer::compareTo).orElse(0);
    }

    private static Collection<Collection<Integer>> mapToResultCombinations(Set<Set<Machine>> combinations) {
        return combinations.stream()
                .map(combination -> combination.stream()
                        .map(c -> c.performance).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }

    private static Result buildMinWasteResult(int limit, Set<Set<Machine>> combinations) {
        int minWaste = calculateMinWaste(limit, combinations);
        Set<Set<Machine>> combinationsWithLeastWaste = getCombinationsWithMinWaste(limit, combinations, minWaste);
        return new Result(combinationsWithLeastWaste.size(), mapToResultCombinations(combinationsWithLeastWaste), minWaste);
    }

    private static Set<Set<Machine>> getOutperformingCombinationWithMinWaste(Integer[] machines, int limit) {
        Set<Set<Machine>> result = new HashSet<>();
        List<Integer> machinesAsList = Arrays.asList(machines);
        machinesAsList.sort(Comparator.naturalOrder());

        machinesAsList.stream()
                .filter(m -> m > limit)
                .findFirst()
                .ifPresent(minWasteOutperformingMachinePerformance -> {
                    for (int i = 0; i < machines.length; i++) {
                        if (machines[i].equals(minWasteOutperformingMachinePerformance)) {
                            Set<Machine> singleMachineCombination = new HashSet<>();
                            singleMachineCombination.add(new Machine(i, minWasteOutperformingMachinePerformance));
                            result.add(singleMachineCombination);
                        }
                    }
                });

        return result;
    }
}