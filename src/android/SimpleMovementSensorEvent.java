package edu.berkeley.eecs.emission.cordova.integrityDetect;

import java.util.ArrayList;

public class SimpleMovementSensorEvent {

    public double getSTDX() {
        return stdX;
    }
    public double getMeanX() {
        return meanX;
    }
    public ArrayList getTimeStampsX() {
        return timeStampsX;
    }

    public double getSTDY() {
        return stdY;
    }
    public double getMeanY() {
        return meanY;
    }
    public ArrayList getTimeStampsY() {
        return timeStampsY;
    }

    public double getSTDZ() {
        return stdZ;
    }
    public double getMeanZ() {
        return meanZ;
    }
    public ArrayList getTimeStampsZ() {
        return timeStampsZ;
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }

    public double getFinalTimeStamp() {
        return finalTimeStamp;
    }

    private double stdX;
    private double meanX;
    private ArrayList timeStampsX;

    private double stdY;
    private double meanY;
    private ArrayList timeStampsY;
    
    private double stdZ;
    private double meanZ;
    private ArrayList timeStampsZ;

    private double latitude;
    private double longitude;

    private double finalTimeStamp;


    public SimpleMovementSensorEvent() {
    }

    public SimpleMovementSensorEvent(double stdX, double meanX, ArrayList timeStampsX, double stdY, double meanY, ArrayList timeStampsY, double stdZ, double meanZ, ArrayList timeStampsZ, double latitude, double longitude, double finalTimeStamp) {
        this.stdX = stdX;
        this.meanX = meanX;
        this.timeStampsX = timeStampsX;

        this.stdY = stdY;
        this.meanY = meanY;
        this.timeStampsY = timeStampsY;

        this.stdZ = stdZ;
        this.meanZ = meanZ;
        this.timeStampsZ = timeStampsZ;
        
        this.latitude = latitude;
        this.longitude = longitude;

        this.finalTimeStamp = finalTimeStamp;
    }
}
