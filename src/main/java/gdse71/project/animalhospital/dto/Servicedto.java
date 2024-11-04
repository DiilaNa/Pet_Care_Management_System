package gdse71.project.animalhospital.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class Servicedto {
        private String ServiceID;
        private String ServiceName;
        private Double Cost;
        private String Duration;
        private String PetIdService;
}
