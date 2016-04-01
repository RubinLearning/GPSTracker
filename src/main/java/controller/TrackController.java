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

    @RequestMapping(value = {"/","/track/list"}, method = RequestMethod.GET)
    public String home(Locale locale, Model model){

        List<Track> tracks = trackService.getAll();
        model.addAttribute("tracks", tracks);

        return "track-list";
    }

    @RequestMapping(value = "/track/add", method = RequestMethod.GET)
    public String getAddTrackPage(Model model) {

        Track track = new Track();
        model.addAttribute("type", "add");
        model.addAttribute("track", track);

        return "track";
    }

    @RequestMapping(value = "/track/edit", method = RequestMethod.GET)
    public String getEditTrackPage(@RequestParam("id") Long trackId, Model model) {

        Track track = trackService.get(trackId);
        model.addAttribute("type", "edit");
        model.addAttribute("track", track);
        model.addAttribute("images", imageService.getAllByTrackId(trackId));

        return "track";
    }

    @RequestMapping(value = "/track/add", method = RequestMethod.POST)
    public String addTrek(@RequestParam("file") MultipartFile file, @ModelAttribute("trek") Track track) throws GPSTrackerException{

        trackService.add(track);
        trackService.updateGPX(track, file);

        return "redirect:/track/list";
    }

    @RequestMapping(value = "/track/edit", method = RequestMethod.POST)
    public String updateTrek(@RequestParam("id") Long trackId, @RequestParam("file") MultipartFile file, @ModelAttribute("trek") Track track) throws GPSTrackerException{

        trackService.edit(track);
        trackService.updateGPX(track, file);

        return "redirect:/track/list";
    }

    @RequestMapping(value = "/track/delete", method = RequestMethod.GET)
    public String deleteTrack(@RequestParam("id") Long trackId) {

        trackService.delete(trackId);

        return "redirect:/track/list";
    }

    @RequestMapping(value = "/track/view", method = RequestMethod.GET)
    public String viewGPX(@RequestParam("id") Long trackId, Model model) throws GPSTrackerException {
        model.addAttribute("trackId", trackId);
        return "track-map";
    }

    @RequestMapping(value = "/track/download", method = RequestMethod.GET)
    public String downloadGPX(@RequestParam("id") Long trackId, HttpServletResponse response, Model model) throws GPSTrackerException {

        String filename = "track_" + trackId.toString() + ".gpx";
        TrackGPX trackGPX = trackService.getGPXByTrackId(trackId);

        if (trackGPX == null) {
            model.addAttribute("contentError", GPSTrackerErrorType.LACK_OF_TRACK_FILE.getName());
            return "redirect:/track/list";
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
