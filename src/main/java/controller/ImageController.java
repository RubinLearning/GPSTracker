package controller;

import domain.TrackIMG;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping(value = "/image/add", method = RequestMethod.GET)
    public String getAddImagePage(@RequestParam("id") Long trackId, Model model) {
        model.addAttribute("trackId", trackId);
        return "image";
    }

    @RequestMapping(value = "/image/add", method = RequestMethod.POST)
    public String addImage(@RequestParam("id") Long trackId, @RequestParam("file") MultipartFile file) throws GPSTrackerException{

        imageService.add(trackId, file);

        return "redirect:/track/edit?id=" + trackId;
    }

    @RequestMapping(value = "/image/download", method = RequestMethod.GET)
    public String downloadIMG(@RequestParam("id") Long trackId, @RequestParam("id") Long imageId, HttpServletResponse response, Model model) throws GPSTrackerException {

        String filename = "img_" + imageId.toString() + ".jpg";
        TrackIMG trackIMG = imageService.get(imageId);

        if (trackIMG == null) {
            model.addAttribute("contentError", GPSTrackerErrorType.LACK_OF_IMAGE_FILE.getName());
            return "redirect:/track/edit?id="+trackId;
        }

        response.setContentType("application/jpeg");
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

    @RequestMapping(value = "/image/delete", method = RequestMethod.GET)
    public String deleteImage(@RequestParam("id") Long trackId, @RequestParam("img") Long imageId) {

        imageService.delete(imageId);

        return "redirect:/track/edit?id=" + trackId;
    }

}
