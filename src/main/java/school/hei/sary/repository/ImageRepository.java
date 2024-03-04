package school.hei.sary.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.sary.repository.model.Image;

public interface ImageRepository extends JpaRepository<Image, String> {
}
