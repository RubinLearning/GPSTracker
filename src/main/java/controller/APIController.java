package controller;

import domain.Track;
import domain.TrackIMG;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import service.interfaces.ImageService;
import service.interfaces.TrackService;
import utils.GPSTrackerException;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class APIController {

    @Value("${buffer_size}")
    private int bufferSize;

    @Autowired
    @Resource
    private TrackService trackService;

    @Autowired
    @Resource
    private ImageService imageService;

    @RequestMapping(value = "/track/", method = RequestMethod.GET)
    public ResponseEntity<List<Track>> listAllTracks() {
        List<Track> tracks = trackService.getAll();
        if (tracks.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(tracks, HttpStatus.OK);
    }

    @RequestMapping(value = "/track/{id}", method = RequestMethod.GET)
    public ResponseEntity<Track> getTrack(@PathVariable("id") Long trackId) {
        Track track = trackService.get(trackId);
        if (track == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(track, HttpStatus.OK);
    }

    @RequestMapping(value = "/track/", method = RequestMethod.POST)
    public ResponseEntity<Track> addTrack(@RequestBody Track track, UriComponentsBuilder ucBuilder) throws GPSTrackerException {

        trackService.add(track);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/track/{id}").buildAndExpand(track.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/track/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Track> updateTrack(@PathVariable("id") Long trackId, @RequestBody Track track) {

        Track currentTrack = trackService.get(trackId);

        if (currentTrack == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        currentTrack.setName(track.getName());
        trackService.edit(currentTrack);

        return new ResponseEntity<>(currentTrack, HttpStatus.OK);
    }

    @RequestMapping(value = "/track/{id}/gpx/", method = RequestMethod.POST)
    public ResponseEntity<Track> updateTrackGPX(@PathVariable("id") Long trackId, @RequestParam MultipartFile file) throws GPSTrackerException{

        Track currentTrack = trackService.get(trackId);

        if (currentTrack == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        trackService.updateGPX(currentTrack, file);
        return new ResponseEntity<>(currentTrack, HttpStatus.OK);
    }

    @RequestMapping(value = "/track/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Track> deleteTrack(@PathVariable("id") Long trackId) {

        Track track = trackService.get(trackId);
        if (track == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        trackService.delete(trackId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @RequestMapping(value = "/track/{trackId}/image/", method = RequestMethod.POST)
    public ResponseEntity<Void> addImage(@PathVariable("trackId") Long trackId, @RequestParam("file") MultipartFile file, UriComponentsBuilder ucBuilder) throws GPSTrackerException{

        Track currentTrack = trackService.get(trackId);

        if (currentTrack == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        TrackIMG image = imageService.add(trackId, file);
        HttpHeaders headers = new HttpHeaders();
        Map<String, Long> map = new HashMap<>();
        map.put("trackId", trackId);
        map.put("imageId", image.getId());
        headers.setLocation(ucBuilder.path("/api/track/{trackId}/image/{imageId}").buildAndExpand(map).toUri());
        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/track/{trackId}/image/{imageId}", method = RequestMethod.DELETE)
    public ResponseEntity<Track> deleteImage(@PathVariable("trackId") Long trackId, @PathVariable("imageId") Long imageId) {

        TrackIMG image = imageService.get(imageId);
        if (image == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        imageService.delete(imageId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
