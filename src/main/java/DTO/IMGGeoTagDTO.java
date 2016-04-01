package DTO;

import com.google.gson.annotations.Expose;

public class IMGGeoTagDTO {

    @Expose
    Long imageId;

    @Expose
    double lat;

    @Expose
    double lng;

    public IMGGeoTagDTO(Long imageId, double lat, double lng) {
        this.imageId = imageId;
        this.lat = lat;
        this.lng = lng;
    }

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
