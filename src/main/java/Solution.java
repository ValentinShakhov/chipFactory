import java.util.*;
import java.util.stream.Collectors;

/*
The complexity is: O(n*n)
where:
n - number of machines
 */
class Solution {

    //This method looks ugly a little bit because of the need to print out the result
    public static Result solve(Integer[] machines, int limit) {
        Result result = null;

        if (machines.length == 0) {
            result = new Result(0, Collections.emptyList(), 0);
        }

        if (result == null) {
            List<Integer> machinesList = Arrays.asList(machines);
            Collections.sort(machinesList);

            Integer leastPerformanceMachine = machinesList.get(0);

            if (leastPerformanceMachine > limit) {
                List<Integer> leastPerformanceMachineList = new ArrayList<>();
                List<List<Integer>> resultCombinations = new ArrayList<>();
                leastPerformanceMachineList.add(leastPerformanceMachine);
                resultCombinations.add(leastPerformanceMachineList);

                result = new Result(1, resultCombinations, leastPerformanceMachine - limit);
            } else {
                List<List<Machine>> internalCombinations = getCombinations(machinesList, limit);
                List<List<Integer>> resultCombinations = internalCombinations != null ? mapToResultCombinations(internalCombinations) : Collections.emptyList();

                result = new Result(resultCombinations.size(), resultCombinations, 0);
            }
        }

        System.out.println(result);

        return result;
    }

    public static List<List<Machine>> getCombinations(List<Integer> machines, int limit) {
        Map<Integer, List<List<Machine>>> sumToCombinationsOfMachineIndexes = new HashMap<>();

        for (int numberOfMachinesFromLeft = 1; numberOfMachinesFromLeft <= machines.size(); numberOfMachinesFromLeft++) {
            for (int reqSum = 1; reqSum <= limit; reqSum++) {
                List<List<Machine>> combinationsOfMachinesThatGiveRequiredSum = new ArrayList<>();
                for (int i = 0; i < numberOfMachinesFromLeft; i++) {
                    List<List<Machine>> combinationsWithGivenNumberOfMachines = new ArrayList<>();

                    Machine curMachine = new Machine(i, machines.get(i));
                    int curDifToFill = reqSum - curMachine.performance;
                    if (curDifToFill == 0) {
                        ArrayList<Machine> singleMachineCombination = new ArrayList<>();
                        singleMachineCombination.add(curMachine);
                        combinationsWithGivenNumberOfMachines.add(singleMachineCombination);
                    }

                    List<List<Machine>> existingCombinationsToFillTheDiff = sumToCombinationsOfMachineIndexes.get(curDifToFill);
                    if (existingCombinationsToFillTheDiff != null) {
                        existingCombinationsToFillTheDiff.stream()
                                .filter(existingCombination -> !existingCombination.contains(curMachine))
                                .forEach(existingCombination -> {
                                    List<Machine> combinationWithExistingCombination = new ArrayList<>(existingCombination);
                                    combinationWithExistingCombination.add(curMachine);
                                    combinationWithExistingCombination.sort(Comparator.comparingInt(m -> m.id));
                                    combinationsWithGivenNumberOfMachines.add(combinationWithExistingCombination);
                                });
                    }

                    combinationsWithGivenNumberOfMachines.stream()
                            .filter(cur -> !combinationsOfMachinesThatGiveRequiredSum.contains(cur))
                            .forEach(cur -> combinationsOfMachinesThatGiveRequiredSum.addAll(combinationsWithGivenNumberOfMachines));
                }
                if (!combinationsOfMachinesThatGiveRequiredSum.isEmpty()) {
                    List<List<Machine>> existingCombinations = sumToCombinationsOfMachineIndexes.get(reqSum);
                    if (existingCombinations == null) {
                        sumToCombinationsOfMachineIndexes.put(reqSum, combinationsOfMachinesThatGiveRequiredSum);
                    } else {
                        combinationsOfMachinesThatGiveRequiredSum.forEach(combinationOfMachinesThatGivesSum -> {
                            if (!existingCombinations.contains(combinationOfMachinesThatGivesSum)) {
                                existingCombinations.add(combinationOfMachinesThatGivesSum);
                            }
                        });
                    }
                }
            }
        }

        return sumToCombinationsOfMachineIndexes.get(limit);
    }

    private static List<List<Integer>> mapToResultCombinations(List<List<Machine>> internalCombinations) {
        return internalCombinations.stream()
                .map(combination -> combination.stream()
                        .map(c -> c.performance).collect(Collectors.toList()))
                .collect(Collectors.toList());
    }
}