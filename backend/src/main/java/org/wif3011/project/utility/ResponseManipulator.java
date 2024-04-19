package org.wif3011.project.utility;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ResponseManipulator {
    public static Map<String, Integer> sortAndLimit(Map<String, Integer> wordFrequencyMap, int numOfWords, boolean sortAscending) {
        // define comparator based on sorting order
        Comparator<Map.Entry<String, Integer>> comparator = sortAscending ?
                Comparator.comparing((Map.Entry<String, Integer> entry) -> entry.getValue()) :
                Comparator.comparing((Map.Entry<String, Integer> entry) -> entry.getValue()).reversed();

        // sort the wordFrequencyMap based on values
        List<Map.Entry<String, Integer>> sortedEntries = wordFrequencyMap.entrySet().stream()
                .sorted(comparator)
                .limit(numOfWords)
                .toList();

        // collect the sorted and limited entries into a new map
        Map<String, Integer> sortedAndLimitedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sortedAndLimitedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedAndLimitedMap;
    }
}
