package controller;

import DTO.IMGGeoTagDTO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import domain.TrackIMG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import service.interfaces.ImageService;
import utils.GPSTrackerErrorType;
import utils.GPSTrackerException;
import utils.geotagreader.GeoTag;
import utils.geotagreader.JpegGeoTagReader;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ImageController {

    private static final Logger logger = LoggerFactory.getLogger(TrackController.class);

    @Value("${buffer_size}")
    private int bufferSize;

    @Autowired
    @Resource
    private ImageService imageService;

    @RequestMapping(value = "/track/{trackId}/image/new", method = RequestMethod.GET)
    public String getAddImagePage(@PathVariable("trackId") Long trackId, Model model) {
        model.addAttribute("trackId", trackId);
        return "image";
    }

    @RequestMapping(value = "/track/{trackId}/image/new", method = RequestMethod.POST)
    public String addImage(@PathVariable("trackId") Long trackId, @RequestParam("file") MultipartFile file) throws GPSTrackerException{

        imageService.add(trackId, file);

        return "redirect:/track/" + trackId;
    }

    @RequestMapping(value = "/track/{trackId}/image/{imageId}", method = RequestMethod.GET)
    public String downloadIMG(@PathVariable("trackId") Long trackId, @PathVariable("imageId") Long imageId, HttpServletResponse response, Model model) throws GPSTrackerException {

        String filename = "img_" + imageId.toString() + ".jpg";
        TrackIMG trackIMG = imageService.get(imageId);

        if (trackIMG == null) {
            model.addAttribute("contentError", GPSTrackerErrorType.LACK_OF_IMAGE_FILE.getName());
            return "redirect:/track/"+trackId;
        }

        response.setContentType("image/jpeg");
        response.setContentLength(trackIMG.getIMG().length);
        String contentDispositionType = "inline";
        response.setHeader("Content-Disposition", String.format(contentDispositionType + "; filename=\"" + filename + "\""));

        try (
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(trackIMG.getIMG());
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

    @RequestMapping(value = "/track/{trackId}/image/delete", method = RequestMethod.GET)
    public String deleteImage(@PathVariable("trackId") Long trackId, @RequestParam("id") Long imageId) {

        imageService.delete(imageId);

        return "redirect:/track/" + trackId;
    }

    @RequestMapping(value = "/track/{trackId}/geotags", method = RequestMethod.GET)
    public @ResponseBody
    String getGeotagsJSONByTrackId(@PathVariable("trackId") Long trackId) {

        List<IMGGeoTagDTO> geotags = getGeotagsDTOByTrackId(trackId);
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String jsonString = gson.toJson(geotags);

        return jsonString;
    }

    private List<IMGGeoTagDTO> getGeotagsDTOByTrackId(Long trackId) {

        List<IMGGeoTagDTO> result = new ArrayList<>();
        List<TrackIMG> trackIMGList = imageService.getAllByTrackId(trackId);

        for (TrackIMG trackIMG: trackIMGList) {
            if (trackIMG == null) {
                continue;
            }

            String filename = "img_" + trackIMG.getId().toString() + ".jpg";
            File file = new File(filename);
            try (
                    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(trackIMG.getIMG());
                    OutputStream outputStream = new FileOutputStream(file);
            ) {
                byte[] buffer = new byte[bufferSize];
                int bytesRead = -1;

                while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                continue;
            }

            JpegGeoTagReader jpegGeoTagReader = new JpegGeoTagReader();
            try {
                GeoTag geoTag = jpegGeoTagReader.readMetadata(file);
                result.add(new IMGGeoTagDTO(trackIMG.getId(), geoTag.getLatitude(), geoTag.getLongitude()));
            } catch (Exception e){
                continue;
            }

        }

        return result;
    }

}
