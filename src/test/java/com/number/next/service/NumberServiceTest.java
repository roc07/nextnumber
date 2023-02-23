package com.number.next.service;

import com.number.next.exception.InvalidListSize;
import com.number.next.exception.InvalidParametersException;
import com.number.next.model.Parameters;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NumberServiceTest {

    @InjectMocks
    private NumberService numberService;

    @Test
    void getSingleNumber_withValidNumbers_willAlwaysReturn() {
        List<Integer> numbers = getNumbers();
        numberService.prepareNumbers(numbers);

        int number = numberService.getSingleNumber();

        assertTrue(number > 0);
        assertEquals(numbers.size(), numberService.getProbabilities().size());
    }

    @Test
    void getSingleNumber_withNoNumbersProvided_willThrow() {
        numberService.prepareNumbers(List.of());

        assertThrows(InvalidListSize.class, () -> numberService.getSingleNumber());
    }

    @Test
    void prepareProbabilities_withNumbersAndProbabilities_willPreparePercentages() {
        Parameters parameters = Parameters
                .builder()
                .numbers(getNumbers())
                .probability(getProbabilities())
                .build();
        List<Double> percentages = numberService.prepareProbabilities(parameters);

        assertEquals(0.10, percentages.get(0));
        assertEquals(0.15, percentages.get(1));
        assertEquals(0.50, percentages.get(2));
        assertEquals(0.25, percentages.get(3));
    }

    @Test
    void prepareProbabilities_withDifferentListSizes_willThrow() {
        Parameters parameters = Parameters
                .builder()
                .numbers(getNumbers())
                .probability(List.of(20d, 30d, 100d, 50d, 20d))
                .build();
        assertThrows(InvalidParametersException.class, () -> numberService.prepareProbabilities(parameters));
    }

    @Test
    void prepareProbabilities_withNoNumbersProvided_willThrow() {
        Parameters parameters = Parameters
                .builder()
                .probability(getProbabilities())
                .build();
        assertThrows(InvalidListSize.class, () -> numberService.prepareProbabilities(parameters));
    }

    @Test
    void multipleCallsToGetSingleNumber_withNumbersAndProbabilities_willReturnWithinExpectedRanges() {
        Parameters parameters = Parameters
                .builder()
                .numbers(getNumbers())
                .probability(getProbabilities())
                .build();
        numberService.prepareProbabilities(parameters);

        Map<Integer, Integer> results = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            int num = numberService.getSingleNumber();
            int end = results.get(num) != null ? results.get(num) : 0;
            results.put(num, ++end);
        }

        assertTrue(results.get(1) > 900 && results.get(1) < 1100);
        assertTrue(results.get(2) > 1400 && results.get(1) < 1600);
        assertTrue(results.get(3) > 4900 && results.get(1) < 5100);
        assertTrue(results.get(4) > 2400 && results.get(1) < 2600);
    }

    @Test
    void getNumbersWithAppearances_withNumbersAndProbabilities_willReturnMap() {
        Parameters parameters = Parameters
                .builder()
                .numbers(getNumbers())
                .probability(getProbabilities())
                .build();
        numberService.prepareProbabilities(parameters);
        Map<Integer, Integer> results = numberService.getNumbersWithAppearances(10000);

        assertTrue(results.get(1) > 900 && results.get(1) < 1100);
        assertTrue(results.get(2) > 1400 && results.get(1) < 1600);
        assertTrue(results.get(3) > 4900 && results.get(1) < 5100);
        assertTrue(results.get(4) > 2400 && results.get(1) < 2600);
    }

    private List<Integer> getNumbers() {
        return List.of(1, 2, 3, 4);
    }

    private List<Double> getProbabilities() {
        return List.of(20d, 30d, 100d, 50d);
    }

}