import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private final static int TOTAL_NUM_REQUESTS = 200000;
    private final static int NUM_REQUESTS_PER_THREAD = 1000;
    private final static int FIRST_PHASE_NUM_THREADS = 32;
    private final static int SECOND_PHASE_NUM_THREADS = 168;

    public static void main(String[] args) throws InterruptedException, IOException {
        BlockingQueue<SkierLiftRide> queue = new LinkedBlockingQueue<>();
        OutputRecord outputRecord = new OutputRecord(new ArrayList<String[]>());

        SkiersApiProducer skiersApiProducer = new SkiersApiProducer(queue, TOTAL_NUM_REQUESTS);
        Thread producerThread = new Thread(skiersApiProducer);
        long startTime = System.currentTimeMillis();
        producerThread.start();

        AtomicInteger numOfSuccessfulRequest = new AtomicInteger(0);
        AtomicInteger numOfFailedRequest = new AtomicInteger(0);
        AtomicInteger numOfTotalRequest = new AtomicInteger(0);
        ResultData resultData = new ResultData(numOfSuccessfulRequest, numOfFailedRequest, numOfTotalRequest);

        CountDownLatch firstPhaseCompleted = new CountDownLatch(FIRST_PHASE_NUM_THREADS);
        CountDownLatch secondPhaseCompleted = new CountDownLatch(SECOND_PHASE_NUM_THREADS);
        CountDownLatch allCompleted = new CountDownLatch(FIRST_PHASE_NUM_THREADS + SECOND_PHASE_NUM_THREADS);

        for (int i = 0; i < FIRST_PHASE_NUM_THREADS; i++) {
            SkiersApiConsumer firstPhaseConsumer = new SkiersApiConsumer(queue, NUM_REQUESTS_PER_THREAD, resultData, firstPhaseCompleted, allCompleted, outputRecord);
            new Thread(firstPhaseConsumer).start();
        }
        firstPhaseCompleted.await();

        long firstPhaseFinishTime = System.currentTimeMillis();
        long firstPhaseTotalRunTime = (firstPhaseFinishTime - startTime) / 1000;
        long firstPhaseThroughput = resultData.getTotalRequestsSent().get() / firstPhaseTotalRunTime;

        int firstPhaseSuccessfulRequests = resultData.getNumOfSuccessRequest().get();
        int firstPhaseFailedRequests = resultData.getNumOfFailedRequest().get();

        System.out.println("Phase 1 Output:");
        System.out.println("Number of threads: " + FIRST_PHASE_NUM_THREADS);
        System.out.println("Number of successful requests sent is: " + resultData.getNumOfSuccessRequest().get());
        System.out.println("Number of failed requests is: " + resultData.getNumOfFailedRequest().get());
        System.out.println("Total run time for all phases to complete is (in second): " + firstPhaseTotalRunTime + "s");
        System.out.println("Total throughput in requests per second is: " + firstPhaseThroughput);
        System.out.println();

        long secondPhaseStartTime = System.currentTimeMillis();
        for (int j = 0; j < SECOND_PHASE_NUM_THREADS; j++) {
            SkiersApiConsumer secondPhaseConsumer =
                    new SkiersApiConsumer(queue, NUM_REQUESTS_PER_THREAD, resultData, secondPhaseCompleted, allCompleted, outputRecord);
            new Thread(secondPhaseConsumer).start();
        }
        allCompleted.await();

        long finishTime = System.currentTimeMillis();
        long secondPhaseRunTime = (finishTime - secondPhaseStartTime) / 1000;
        long secondPhaseThroughput = (resultData.getTotalRequestsSent().get() - firstPhaseSuccessfulRequests) / secondPhaseRunTime;

        int secondPhaseSuccessfulRequests = resultData.getNumOfSuccessRequest().get() - firstPhaseSuccessfulRequests;
        int secondPhaseFailedRequests = resultData.getNumOfFailedRequest().get() - firstPhaseFailedRequests;

        System.out.println("Phase 2 Output:");
        System.out.println("Number of threads: " + SECOND_PHASE_NUM_THREADS);
        System.out.println("Number of successful requests sent is: " + secondPhaseSuccessfulRequests);
        System.out.println("Number of failed requests is: " + secondPhaseFailedRequests);
        System.out.println("Total run time for all phases to complete is (in second): " + secondPhaseRunTime + "s");
        System.out.println("Total throughput in requests per second is: " + secondPhaseThroughput);
        System.out.println();

        long totalRunTime = (finishTime - startTime) / 1000;
        long throughput = resultData.getTotalRequestsSent().get() / totalRunTime;

        System.out.println("Part 1 Output:");
        System.out.println("Number of successful requests sent is: " + resultData.getNumOfSuccessRequest().get());
        System.out.println("Number of failed requests is: " + resultData.getNumOfFailedRequest().get());
        System.out.println("Total run time for all phases to complete is (in second): " + totalRunTime + "s");
        System.out.println("Total throughput in requests per second is: " + throughput);
        System.out.println();

        outputRecord.writeDataToCSV();

        System.out.println("Part 2 Output:");
        System.out.println("Mean response time in millisecond is: " + outputRecord.getMeanResponseTime());
        System.out.println("Median response time in millisecond is: " + outputRecord.getMedianResponseTime());
        System.out.println("Total throughput in requests per second is: " + throughput);
        System.out.println("99th percentile response time is: " + outputRecord.getP99());
        System.out.println("Min response time in millisecond is: " + outputRecord.getMinResponseTime());
        System.out.println("Max response time in millisecond is: " + outputRecord.getMaxResponseTime());
    }
}
