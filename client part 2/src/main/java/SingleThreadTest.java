import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadTest {

    private final static int TOTAL_NUM_REQUESTS = 1000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<SkierLiftRide> queue = new LinkedBlockingQueue<>();

        SkiersApiProducer skiersApiProducer = new SkiersApiProducer(queue, TOTAL_NUM_REQUESTS);
        Thread producerThread = new Thread(skiersApiProducer);
        long startTime = System.currentTimeMillis();
        producerThread.start();

        AtomicInteger numOfSuccessfulRequest = new AtomicInteger(0);
        AtomicInteger numOfFailedRequest = new AtomicInteger(0);
        AtomicInteger numOfTotalRequest = new AtomicInteger(0);
        ResultData resultData = new ResultData(numOfSuccessfulRequest, numOfFailedRequest, numOfTotalRequest);

        CountDownLatch firstPhaseCompleted = new CountDownLatch(TOTAL_NUM_REQUESTS);
        CountDownLatch allCompleted = new CountDownLatch(TOTAL_NUM_REQUESTS);

        OutputRecord outputRecord = new OutputRecord(new ArrayList<String[]>());

        SkiersApiConsumer skiersApiConsumer = new SkiersApiConsumer(queue, TOTAL_NUM_REQUESTS, resultData, firstPhaseCompleted, allCompleted, outputRecord);
        Thread testThread = new Thread(skiersApiConsumer);
        testThread.start();
        testThread.join();

        long finishTime = System.currentTimeMillis();
        long totalRunTime = (finishTime - startTime) / 1000;
        long throughput = resultData.getTotalRequestsSent().get() / totalRunTime;
        long averageLatency = skiersApiConsumer.getAverageLatency();

        System.out.println("Single Thread Test Output:");
        System.out.println("Number of successful requests sent is: " + resultData.getNumOfSuccessRequest().get());
        System.out.println("Number of failed requests is: " + resultData.getNumOfFailedRequest().get());
        System.out.println("Total run time for all phases to complete is (in second): " + totalRunTime + "s");
        System.out.println("Total throughput in requests per second is: " + throughput);
        System.out.println("Average latency per ms is: " + averageLatency);
    }
}
