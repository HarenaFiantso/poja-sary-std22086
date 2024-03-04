package school.hei.sary.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImageDTO {
  private String original_url;
  private String transformed_url;
}
