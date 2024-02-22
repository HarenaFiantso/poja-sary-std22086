package school.hei.sary.endpoint.rest.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import school.hei.sary.PojaGenerated;
import school.hei.sary.file.BucketComponent;

@PojaGenerated
@RestController
@AllArgsConstructor
@RequestMapping("/blacks")
public class ImageController {

  private final BucketComponent bucketComponent;

  @PutMapping("/{key}")
  public ResponseEntity<String> transformAndStoreImage(
      @PathVariable String key, @RequestBody MultipartFile file) {
    try {
      BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(file.getBytes()));

      BufferedImage blackAndWhiteImage = convertToBlackAndWhite(originalImage);

      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      ImageIO.write(blackAndWhiteImage, "png", outputStream);

      File tempFile = File.createTempFile("temp-image", ".png");
      Files.write(tempFile.toPath(), outputStream.toByteArray());

      bucketComponent.upload(tempFile, key);

      return ResponseEntity.ok("Image transformed and stocked successfully !");
    } catch (IOException e) {
      return ResponseEntity.status(500).body("Failed to treat image");
    }
  }

  public BufferedImage convertToBlackAndWhite(BufferedImage originalImage) {
    int width = originalImage.getWidth();
    int height = originalImage.getHeight();
    BufferedImage blackAndWhiteImage =
        new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        int rgb = originalImage.getRGB(i, j);
        int red = (rgb >> 16) & 0xFF;
        int green = (rgb >> 8) & 0xFF;
        int blue = rgb & 0xFF;
        int grayValue = (red + green + blue) / 3;
        int grayRgb = (grayValue << 16) + (grayValue << 8) + grayValue;
        blackAndWhiteImage.setRGB(i, j, grayRgb);
      }
    }

    return blackAndWhiteImage;
  }
}
