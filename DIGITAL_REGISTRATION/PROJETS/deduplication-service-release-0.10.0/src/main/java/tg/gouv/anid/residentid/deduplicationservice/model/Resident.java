package tg.gouv.anid.residentid.deduplicationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Resident {
    private Long id;
    private String firstname;
    private String lastname;
    private String fullname;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dateOfBirth;
    private boolean foreigner;
    private String email;
    private String phone;
    private String pobox;
    private String spouseFirstname;
    private String spouseLastname;
    private String parentFirstname;
    private String parentLastname;
    private String parentPhone;
    private String parentUIN;
    @JsonIgnore
    private String residentFingerprint;
    @JsonIgnore
    private String residentPhoto;
    private String parentRelationShip;
    private String residentUIN;
    @JsonIgnore
    private String residentFingerName;
    @JsonIgnore
    private String fingerprintImagePath;
    private String fingerprintImageUrl;
    @JsonIgnore
    private Set<Fingerprintinfo> fingerprintinfos = new HashSet<>();
    private String requestId;
    private String filiation;
    private String familyCode;
    private String secondaryPhone;
    private String college;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate entryDate;
    private String civility;
    private String contributorType;
    private byte[] imageData;
}
