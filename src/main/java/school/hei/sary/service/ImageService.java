package school.hei.sary.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.sary.file.BucketComponent;

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
}
