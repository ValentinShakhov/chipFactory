import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SolutionTest {

    @Test
    public void testFromTask() {
        Integer[] machines = {1, 2, 4, 10, 5, 6};
        Result result = Solution.solveAndPrint(machines, 11);

        assertEquals(4, result.numberOfSolutions);
        assertEquals(0, result.waste);
    }

    @Test
    public void testEmpty() {
        Integer[] machines = {};
        Result result = Solution.solveAndPrint(machines, 11);

        assertEquals(0, result.numberOfSolutions);
        assertEquals(0, result.waste);
    }

    @Test
    public void testNumbersThatSumUpToLessThanRequired() {
        Integer[] machines = {1, 1, 1};
        Result result = Solution.solveAndPrint(machines, 11);

        assertEquals(0, result.numberOfSolutions);
        assertEquals(0, result.waste);
    }

    @Test
    public void testNumbersMoreThanRequired() {
        Integer[] machines = {21, 16, 72, 900};
        Result result = Solution.solveAndPrint(machines, 11);

        assertEquals(1, result.numberOfSolutions);
        assertEquals(5, result.waste);
    }

    @Test
    public void testNumbersMoreOrLessThanRequired() {
        Integer[] machines = {10, 21, 16, 72, 900};
        Result result = Solution.solveAndPrint(machines, 11);

        assertEquals(1, result.numberOfSolutions);
        assertEquals(5, result.waste);
    }

    @Test
    public void testAllEqualNumbers() {
        Integer[] machines = {1, 1, 1, 1};
        Result result = Solution.solveAndPrint(machines, 2);

        assertEquals(6, result.numberOfSolutions);
        assertEquals(0, result.waste);
    }

    @Test
    public void testEqualMachinesWithWasteOutcome() {
        Integer[] machines = {2, 2, 2, 2};
        Result result = Solution.solveAndPrint(machines, 7);

        assertEquals(1, result.numberOfSolutions);
        assertEquals(1, result.waste);
    }

    //@Test //Uncomment to run
    public void testLongRunning() {
        Integer[] machines = {1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6, 1, 2, 4, 10, 5, 6};
        Result result = Solution.solve(machines, 11);

        assertEquals(21735, result.numberOfSolutions);
        assertEquals(0, result.waste);
    }
}