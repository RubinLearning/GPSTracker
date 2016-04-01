package controller;

import domain.TrackGPX;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import service.interfaces.ImageService;
import service.interfaces.TrackService;
import domain.Track;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import utils.GPSTrackerErrorType;
import utils.GPSTrackerException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

@Controller
public class TrackController {

    private static final Logger logger = LoggerFactory.getLogger(TrackController.class);

    @Value("${buffer_size}")
    private int bufferSize;

    @Autowired
    @Resource
    private TrackService trackService;

    @Autowired
    @Resource
    private ImageService imageService;

    @RequestMapping(value = {"/","/tracks"}, method = RequestMethod.GET)
    public String home(Locale locale, Model model){

        List<Track> tracks = trackService.getAll();
        model.addAttribute("tracks", tracks);

        return "track-list";
    }

    @RequestMapping(value = "/track/{trackId}", method = RequestMethod.GET)
    public String getEditTrackPage(@PathVariable("trackId") Long trackId, Model model) {

        Track existingTrack = trackService.get(trackId);
        model.addAttribute("type", "existing");
        model.addAttribute("track", existingTrack);
        model.addAttribute("images", imageService.getAllByTrackId(trackId));

        return "track";
    }

    @RequestMapping(value = "/track/{trackId}", method = RequestMethod.POST)
    public String updateTrek(@PathVariable("trackId") Long trackId, @RequestParam("file") MultipartFile file, @ModelAttribute("track") Track track) throws GPSTrackerException{

        track.setId(trackId);
        trackService.edit(track);
        trackService.updateGPX(track, file);

        return "redirect:/tracks";
    }

    @RequestMapping(value = "/track/new", method = RequestMethod.GET)
    public String getAddTrackPage(Model model) {

        Track track = new Track();
        model.addAttribute("type", "new");
        model.addAttribute("track", track);

        return "track";
    }

    @RequestMapping(value = "/track/new", method = RequestMethod.POST)
    public String addTrek(@RequestParam("file") MultipartFile file, @ModelAttribute("trek") Track track) throws GPSTrackerException{

        trackService.add(track);
        trackService.updateGPX(track, file);

        return "redirect:/tracks";
    }

    @RequestMapping(value = "/track/delete", method = RequestMethod.GET)
    public String deleteTrack(@RequestParam("id") Long trackId) {

        trackService.delete(trackId);

        return "redirect:/tracks";
    }

    @RequestMapping(value = "/track/{trackId}/map", method = RequestMethod.GET)
    public String getTrackMapPage(@PathVariable("trackId") Long trackId, Model model) throws GPSTrackerException {
        model.addAttribute("trackId", trackId);
        return "track-map";
    }

    @RequestMapping(value = "/track/{trackId}/map/download", method = RequestMethod.GET)
    public String downloadGPX(@PathVariable("trackId") Long trackId, HttpServletResponse response, Model model) throws GPSTrackerException {

        String filename = "track_" + trackId.toString() + ".gpx";
        TrackGPX trackGPX = trackService.getGPXByTrackId(trackId);

        if (trackGPX == null) {
            model.addAttribute("contentError", GPSTrackerErrorType.LACK_OF_TRACK_FILE.getName());
            return "redirect:/tracks";
        }

        response.setContentType("application/xml");
        response.setContentLength(trackGPX.getGPX().length);
        String contentDispositionType = "inline";
        response.setHeader("Content-Disposition", String.format(contentDispositionType + "; filename=\"" + filename + "\""));

        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(trackGPX.getGPX());
                OutputStream outputStream = response.getOutputStream();
        ) {
            byte[] buffer = new byte[bufferSize];
            int bytesRead = -1;

            while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new GPSTrackerException(e.getMessage());
        }

        return null;
    }

}
