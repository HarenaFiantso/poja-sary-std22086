package school.hei.sary.service;

import ij.IJ;
import ij.ImagePlus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.sary.PojaGenerated;
import school.hei.sary.dto.ImageDTO;
import school.hei.sary.file.BucketComponent;
import ij.process.ImageConverter;
import school.hei.sary.repository.ImageRepository;
import school.hei.sary.repository.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.time.Duration;

@Service
@AllArgsConstructor
@PojaGenerated
public class ImageService {
  private final BucketComponent bucketComponent;
  private final ImageRepository imageRepository;
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

  @Transactional
  public ImageDTO uploadAndConvertImageToBlackAndWhite(String id, byte[] file)
      throws IOException {
    if (file == null) {
      throw new RemoteException("Image file is mandatory");
    }
    String fileEndSuffix = ".png";
    String newFileName = id + fileEndSuffix;
    String blackFileName = id + "-black" + fileEndSuffix;

    File originalFile = File.createTempFile(newFileName, fileEndSuffix);
    File blackTempImageFile = File.createTempFile(blackFileName, fileEndSuffix);
    writeFileFromByteArray(file, originalFile);
    File blackImageFile = convertImageToBlackAndWhite(originalFile, blackTempImageFile);

    uploadImageFile(originalFile, newFileName);
    uploadImageFile(blackImageFile, blackFileName);
    Image toSave =
        Image.builder()
            .id(id)
            .originalBucketKey(IMAGE_BUCKET_DIRECTORY + newFileName)
            .blackAndWhiteBucketKey(IMAGE_BUCKET_DIRECTORY + blackFileName)
            .build();
    imageRepository.save(toSave);
    return getPicturesURL(id);
  }

  public ImageDTO getPicturesURL(String pictureInformationId) {
    Image image =
        imageRepository
            .findById(pictureInformationId)
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Picture information with id " + pictureInformationId + " does not exist"));

    String originalPictureURL =
        bucketComponent
            .presign(image.getOriginalBucketKey(), Duration.ofHours(1))
            .toString();
    String backAndWhitePictureURL =
        bucketComponent
            .presign(image.getBlackAndWhiteBucketKey(), Duration.ofHours(1))
            .toString();

    return ImageDTO.builder()
        .original_url(originalPictureURL)
        .transformed_url(backAndWhitePictureURL)
        .build();
  }

  private void writeFileFromByteArray(byte[] bytes, File file) {
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
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
