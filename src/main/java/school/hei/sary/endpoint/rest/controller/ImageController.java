package school.hei.sary.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import school.hei.sary.dto.ImageDTO;
import school.hei.sary.service.ImageService;

@RestController
@AllArgsConstructor
public class ImageController {
  private final ImageService imageService;

  @PutMapping(value = "/black-and-white/{id}")
  public ImageDTO getBlackAndWhiteImage(
      @PathVariable(name = "id") String id, @RequestBody(required = false) byte[] file) {
    try {
      return imageService.uploadAndConvertImageToBlackAndWhite(id, file);
    } catch (Exception e) {
      return null;
    }
  }

  @GetMapping(value = "/black-and-white/{id}")
  public ImageDTO getImageURL(@PathVariable(name = "id") String id) {
    try {
      return imageService.getPicturesURL(id);
    } catch (Exception e) {
      return null;
    }
  }
}
