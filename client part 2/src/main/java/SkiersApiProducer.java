import java.util.concurrent.BlockingQueue;

public class SkiersApiProducer implements Runnable {

    private SkierLiftRide skierLiftRide;
    private BlockingQueue<SkierLiftRide> queue;
    private int totalNumOfRequests;

    public SkiersApiProducer(BlockingQueue<SkierLiftRide> queue, int totalNumOfRequests) {
        this.queue = queue;
        this.totalNumOfRequests = totalNumOfRequests;
    }

    @Override
    public void run() {
        for (int i = 0; i < totalNumOfRequests; i++) {
            this.skierLiftRide = new SkierLiftRide();
            skierLiftRide.generateRandomSkierLiftRide();
            try {
                queue.put(skierLiftRide);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
