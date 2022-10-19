import java.util.concurrent.ThreadLocalRandom;

public class SkierLiftRide {
    private Integer skierId;
    private Integer resortId;
    private Integer liftId;
    private String seasonId;
    private String dayId;
    private Integer time;

    private static final int MIN_SKIER_ID = 1;
    private static final int MAX_SKIER_ID = 100001;
    private static final int MIN_RESORT_ID = 1;
    private static final int MAX_RESORT_ID = 11;
    private static final int MIN_LIFT_ID = 1;
    private static final int MAX_LIFT_ID = 41;
    private static final String CURRENT_SEASON_ID = "2022";
    private static final String CURRENT_DAY_ID = "1";
    private static final int MIN_TIME = 1;
    private static final int MAX_TIME = 361;

    public SkierLiftRide() {}

    public void generateRandomSkierLiftRide() {
        this.setSkierId(ThreadLocalRandom.current().nextInt(MIN_SKIER_ID, MAX_SKIER_ID));
        this.setResortId(ThreadLocalRandom.current().nextInt(MIN_RESORT_ID, MAX_RESORT_ID));
        this.setLiftId(ThreadLocalRandom.current().nextInt(MIN_LIFT_ID, MAX_LIFT_ID));
        this.setSeasonId(CURRENT_SEASON_ID);
        this.setDayId(CURRENT_DAY_ID);
        this.setTime(ThreadLocalRandom.current().nextInt(MIN_TIME, MAX_TIME));
    }

    public Integer getSkierId() {
        return skierId;
    }

    public void setSkierId(Integer skierId) {
        this.skierId = skierId;
    }

    public Integer getResortId() {
        return resortId;
    }

    public void setResortId(Integer resortId) {
        this.resortId = resortId;
    }

    public Integer getLiftId() {
        return liftId;
    }

    public void setLiftId(Integer liftId) {
        this.liftId = liftId;
    }

    public String getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(String seasonId) {
        this.seasonId = seasonId;
    }

    public String getDayId() {
        return dayId;
    }

    public void setDayId(String dayId) {
        this.dayId = dayId;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }
}
