package volunteer.sk.greate43.com.gotcha;

public class Memories {

    private String memoryId;
    private double lat;
    private double lon;

    public Memories(){
        //empty constructor
    }


    public String getMemoryId() {
        return memoryId;
    }

    public void setMemoryId(String memoryId) {
        this.memoryId = memoryId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() { return lon; }

    public void setLon(double lon) {
        this.lon = lon;
    }
}

