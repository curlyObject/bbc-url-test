package org.neil.main.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatusReport {

    private static final String LINE_ENDING = "\n";

    private final Map<Integer, Integer> statusCount;

    public StatusReport() {

        this.statusCount = new TreeMap<>();
    }

    public void incrementStatus(int status) {

        statusCount.put(status, statusCount.computeIfAbsent(status, (x -> 0)) + 1);
    }

    public String toJson() {

        final List<String> statusReport = new ArrayList<>();
        statusCount.forEach((status, count) -> {
            statusReport.add(
                    "  {" + LINE_ENDING +
                            "    \"Status_code\": " + status + "," + LINE_ENDING +
                            "    \"Number_of_responses\": " + count + LINE_ENDING +
                            "  }");
        });
        return "[" + LINE_ENDING + String.join("," + LINE_ENDING, statusReport) + LINE_ENDING + "]" + LINE_ENDING;
    }

}
