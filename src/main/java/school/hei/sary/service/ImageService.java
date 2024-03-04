package school.hei.sary.service;

import ij.IJ;
import ij.ImagePlus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.sary.file.BucketComponent;
import ij.process.ImageConverter;

import java.io.File;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class ImageService {
  private final BucketComponent bucketComponent;
  private final Path IMAGE_BUCKET_DIRECTORY = Path.of("image/");

  @Transactional
  public void uploadImageFile(File imageFile, String imageName) {
    String bucketKey = IMAGE_BUCKET_DIRECTORY + imageName;
    bucketComponent.upload(imageFile, bucketKey);
    boolean isDelete = imageFile.delete();
    if (!isDelete) {
      throw new RuntimeException("file " + bucketKey + " is not deleted.");
    }
  }

  private File convertImageToBlackAndWhite(File originalFile, File outputFile) {
    ImagePlus image = IJ.openImage(originalFile.getPath());
    try {
      ImageConverter converter = new ImageConverter(image);
      converter.convertToGray8();
    } catch (Exception e) {
      throw new RuntimeException("Image file invalid");
    }
    ij.io.FileSaver fileSaver = new ij.io.FileSaver(image);
    fileSaver.saveAsPng(outputFile.getPath());
    return outputFile;
  }
}
