import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import io.swagger.client.model.LiftRide;

import java.sql.Timestamp;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class SkiersApiConsumer implements Runnable {

    private BlockingQueue<SkierLiftRide> queue;
    private SkiersApi apiInstance;
    private ApiClient apiClient;
    private SkierLiftRide skierLiftRide;
    private LiftRide body;
    private int numOfRequests;
    private ResultData resultData;
    private CountDownLatch currentPhaseCompleted;
    private CountDownLatch allCompleted;
    private long averageLatency;
    private OutputRecord outputRecord;

    private static final String BASE_PATH = "http://54.191.52.8:8080/assignment1-server_war/";

    public SkiersApiConsumer(BlockingQueue<SkierLiftRide> queue, int numOfRequests, ResultData resultData, CountDownLatch currentPhaseCompleted, CountDownLatch allCompleted, OutputRecord outputRecord) {
        this.queue = queue;
        this.numOfRequests = numOfRequests;
        this.resultData = resultData;
        this.currentPhaseCompleted = currentPhaseCompleted;
        this.allCompleted = allCompleted;
        this.averageLatency = 0;
        this.outputRecord = outputRecord;
    }

    @Override
    public void run() {
        this.apiClient = new ApiClient();
        apiClient.setBasePath(BASE_PATH);
        this.apiInstance = new SkiersApi(apiClient);
        long totalLatency = 0;
        int statusCode = 0;

        for (int i = 0; i < numOfRequests; i++) {
            try {
                skierLiftRide = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            body = new LiftRide();
            body.setLiftID(skierLiftRide.getLiftId());
            body.setTime(skierLiftRide.getTime());

            long startTime = System.currentTimeMillis();
            try {
                ApiResponse apiResponse = apiInstance.writeNewLiftRideWithHttpInfo(body, skierLiftRide.getResortId(),
                        skierLiftRide.getSeasonId(), skierLiftRide.getDayId(), skierLiftRide.getSkierId());
                statusCode = apiResponse.getStatusCode();

                int numRetries = 0;
                if (statusCode == 201 || statusCode == 200) {
                    resultData.incSuccessRequest(1);
                    resultData.incTotalRequest(1);
                } else {
                    while (statusCode != 201 || statusCode != 200) {
                        apiResponse = apiInstance.writeNewLiftRideWithHttpInfo(body, skierLiftRide.getResortId(),
                                skierLiftRide.getSeasonId(), skierLiftRide.getDayId(), skierLiftRide.getSkierId());
                        statusCode = apiResponse.getStatusCode();
                        numRetries++;
                        if (numRetries >= 5) {
                            resultData.incFailedRequest(1);
                            resultData.incTotalRequest(1);
                        }
                    }
                }
            } catch (ApiException e) {
                e.printStackTrace();
            }

            long finishTime = System.currentTimeMillis();
            long currentLatency = finishTime - startTime;
            totalLatency += currentLatency;

            Timestamp startTimestamp = new Timestamp(startTime);
            String[] outputData = {startTimestamp.toString(), "POST", Long.toString(currentLatency), Integer.toString(statusCode)};
            outputRecord.addToOutputRecord(outputData);
        }
        averageLatency = totalLatency / numOfRequests;
        currentPhaseCompleted.countDown();
        allCompleted.countDown();
    }

    public long getAverageLatency() {
        return averageLatency;
    }
}
