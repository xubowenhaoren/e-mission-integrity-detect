package edu.berkeley.eecs.emission.cordova.integrityDetect;

public class SimpleMovementSensorEvent {

    public float getAccuracy() {
        return accuracy;
    }

    public int getNumberOfBumps() {
        return numberOfBumps;
    }

    public double getThreshold() {
        return threshold;
    }

    public long getTimeDuration() {
        return timeDuration;
    }

    private int accuracy;
    private int numberOfBumps;
    private double threshold;
    private long timeDuration;


    public SimpleMovementSensorEvent() {
    }

    public SimpleMovementSensorEvent(int accuracy, int numberOfBumps, double threshold, long timeDuration) {
        this.accuracy = accuracy;
        this.numberOfBumps = numberOfBumps;
        this.threshold = threshold;
        this.timeDuration = timeDuration;
    }
}
