package io.wisoft.webapplication;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Sensing {

  @Id
  private UUID id;

  private double value;

  private LocalDateTime createdAt;

  private Sensing(double value) {
    this.value = value;
  }

  public static Sensing create(final double value) {
    Sensing sensing = new Sensing(value);
    sensing.id = UUID.randomUUID();
    sensing.createdAt = LocalDateTime.now();
    return sensing;
  }

}
