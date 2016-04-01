package service.interfaces;

import domain.TrackIMG;
import org.springframework.web.multipart.MultipartFile;
import utils.GPSTrackerException;

import java.util.List;

public interface ImageService {

    List<TrackIMG> getAllByTrackId(Long trackId);

    TrackIMG get(Long id);
    void add(Long trackId, MultipartFile file) throws GPSTrackerException;
    void delete(Long id);

}
