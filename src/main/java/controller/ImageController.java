package controller;

import domain.TrackIMG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import service.interfaces.ImageService;
import utils.GPSTrackerErrorType;
import utils.GPSTrackerException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

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

}
