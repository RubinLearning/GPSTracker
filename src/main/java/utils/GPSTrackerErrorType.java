package utils;

public enum GPSTrackerErrorType {

    LACK_OF_TRACK_FILE("Track file isn't available yet!"),
    OPERATION_NOT_ALLOWED("This operation isn't allowed!");

    private String name;

    GPSTrackerErrorType(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
