import java.util.concurrent.atomic.AtomicInteger;

public class ResultData {

    private AtomicInteger numOfSuccessRequest;
    private AtomicInteger numOfFailedRequest;
    private AtomicInteger totalRequestsSent;

    public ResultData(AtomicInteger numOfSuccessRequest, AtomicInteger numOfFailedRequest, AtomicInteger totalRequestsSent) {
        this.numOfSuccessRequest = numOfSuccessRequest;
        this.numOfFailedRequest = numOfFailedRequest;
        this.totalRequestsSent = totalRequestsSent;
    }

    public void incSuccessRequest(int num) {
        numOfSuccessRequest.addAndGet(num);
    }

    public void incFailedRequest(int num) {
        numOfFailedRequest.addAndGet(num);
    }

    public void incTotalRequest(int num) {
        totalRequestsSent.addAndGet(num);
    }

    public AtomicInteger getNumOfSuccessRequest() {
        return this.numOfSuccessRequest;
    }

    public AtomicInteger getNumOfFailedRequest() {
        return this.numOfFailedRequest;
    }

    public AtomicInteger getTotalRequestsSent() {
        return this.totalRequestsSent;
    }
}
