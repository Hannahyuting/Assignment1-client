import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.opencsv.CSVWriter;

public class OutputRecord {
    private List<String[]> output;

    public OutputRecord(List<String[]> output) {
        this.output = output;
    }

    private static final String filePath = "outputRecord.csv";

    public void writeDataToCSV() throws IOException {
        FileWriter file = new FileWriter(filePath);
        CSVWriter writer = new CSVWriter(file);

        String[] header = {"Start Time", "Request Type", "Latency", "Response Code"};
        writer.writeNext(header);

        for (int i = 0; i < output.size(); i++) {
            writer.writeNext(output.get(i));
        }
        writer.close();
    }

    public void addToOutputRecord(String[] outputData) {
        this.output.add(outputData);
    }

    public long getMeanResponseTime() {
        long latency = 0;
        for (int i = 0; i < output.size(); i++) {
            latency += Long.parseLong(output.get(i)[2]);
        }
        return latency / output.size();
    }

    public long getMedianResponseTime() {
        List<Long> allResponseTime = getSortedResponseTime();
        if (allResponseTime.size() % 2 == 0) {
            return (allResponseTime.get(allResponseTime.size() / 2 - 1)
                    + allResponseTime.get(allResponseTime.size() / 2)) / 2;
        } else {
            return allResponseTime.get(allResponseTime.size() / 2);
        }
    }

    public long getP99() {
        List<Long> allResponseTime = getSortedResponseTime();
        int index = (int) Math.ceil(99 / 100.0 * allResponseTime.size());
        return allResponseTime.get(index);
    }

    public long getMinResponseTime() {
        List<Long> allResponseTime = getSortedResponseTime();
        return allResponseTime.get(0);
    }

    public long getMaxResponseTime() {
        List<Long> allResponseTime = getSortedResponseTime();
        return allResponseTime.get(allResponseTime.size() - 1);
    }

    public List<Long> getSortedResponseTime() {
        List<Long> allResponseTime = new ArrayList<>();
        for (int i = 0; i < output.size(); i++) {
            allResponseTime.add(Long.parseLong(output.get(i)[2]));
        }
        Collections.sort(allResponseTime);
        return allResponseTime;
    }
}
