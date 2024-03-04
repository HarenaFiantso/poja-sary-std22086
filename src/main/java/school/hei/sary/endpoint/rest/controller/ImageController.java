package school.hei.sary.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
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
}
