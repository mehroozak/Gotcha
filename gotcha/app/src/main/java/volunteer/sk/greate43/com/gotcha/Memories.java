package volunteer.sk.greate43.com.gotcha;

public class Memories {
    private String  MemoryName;
    private String memoryId;
    private double lat;
    private double lon;
    private String memoryPicUrl;

    public Memories(){
        //empty constructor
    }

    public String getMemoryPicUrl() { return memoryPicUrl; }

    public void setMemoryPicUrl(String memoryPicUrl) { this.memoryPicUrl = memoryPicUrl; }

    public String getMemoryName() { return MemoryName;    }

    public void setMemoryName(String memoryName) { MemoryName = memoryName;    }

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

