package service.interfaces;

import domain.Track;
import domain.TrackGPX;
import org.springframework.web.multipart.MultipartFile;
import utils.GPSTrackerException;

import java.util.List;

public interface TrackService {

    List<Track> getAll();

    Track get(Long id);
    void add(Track track);
    void edit(Track track);
    void delete(Long id);

    TrackGPX getGPXByTrackId(Long trackId);
    void updateGPX(Track track, MultipartFile file) throws GPSTrackerException;

}
