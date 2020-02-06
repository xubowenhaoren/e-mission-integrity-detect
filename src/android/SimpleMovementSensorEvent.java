package edu.berkeley.eecs.emission.cordova.integrityDetect;

public class SimpleMovementSensorEvent {

    public float getAccuracy() {
        return accuracy;
    }

    public int getNumberOfBumps() {
        return numberOfBumps;
    }

    public double getThershold() {
        return thershold;
    }

    public long getTimeDuration() {
        return timeDuration;
    }

    private int accuracy;
    private int numberOfBumps;
    private double thershold;
    private long timeDuration;


    public SimpleMovementSensorEvent() {
    }

    public SimpleMovementSensorEvent(int accuracy, int numberOfBumps, double thershold, long timeDuration) {
        this.accuracy = accuracy;
        this.numberOfBumps = numberOfBumps;
        this.thershold = thershold;
        this.timeDuration = timeDuration;
    }
}
