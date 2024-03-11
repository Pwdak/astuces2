package tg.gouv.anid.residentid.deduplicationservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Convert;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tg.gouv.anid.residentid.deduplicationservice.config.AttributeEncryptor;
import tg.gouv.anid.residentid.deduplicationservice.enumeration.ResidentStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentPending implements Serializable {
    private Long id;
    @Convert(converter = AttributeEncryptor.class)
    protected String firstname;
    @Convert(converter = AttributeEncryptor.class)
    protected String lastname;
    @JsonFormat(pattern = "dd-MM-yyyy")
    protected LocalDate dateOfBirth;
    protected Long placeOfBirthId;
    protected Long addressId;
    @Convert(converter = AttributeEncryptor.class)
    protected String gender;
    protected Long languageId;
    @Convert(converter = AttributeEncryptor.class)
    protected String pobox;
    @Convert(converter = AttributeEncryptor.class)
    protected String spouseFirstname;
    @Convert(converter = AttributeEncryptor.class)
    protected String spouseLastname;
    @Convert(converter = AttributeEncryptor.class)
    protected String parentFirstname;
    @Convert(converter = AttributeEncryptor.class)
    protected String parentLastname;
    @Convert(converter = AttributeEncryptor.class)
    protected String parentPhone;
    @Convert(converter = AttributeEncryptor.class)
    protected String parentUIN;
    protected byte[] residentPhoto;
    protected byte[] residentFingerprintLeft;
    protected byte[] residentFingerprintRight;
    @Convert(converter = AttributeEncryptor.class)
    protected String parentRelationShip;
    @Convert(converter = AttributeEncryptor.class)
    protected String residentUIN;
    @Convert(converter = AttributeEncryptor.class)
    protected String residentFingerNameLeft;
    protected String residentFingerNameRight;
    protected String requestId;
    protected boolean foreigner;
    @Convert(converter = AttributeEncryptor.class)
    protected String secondaryPhone;
    @Convert(converter = AttributeEncryptor.class)
    private String email;
    @Convert(converter = AttributeEncryptor.class)
    private String phone;
    @Convert(converter = AttributeEncryptor.class)
    private String filiation;
    @Convert(converter = AttributeEncryptor.class)
    private String bloodGroup;
    private Long membershipJobId;
    private Long delegationId;
    @Convert(converter = AttributeEncryptor.class)
    private String familyCode;
    @Convert(converter = AttributeEncryptor.class)
    private String college;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate entryDate;
    @Convert(converter = AttributeEncryptor.class)
    private String civility;
    @Convert(converter = AttributeEncryptor.class)
    private String contributorType;
    @Convert(converter = AttributeEncryptor.class)
    private String qcmStr;
    @Enumerated(EnumType.STRING)
    private ResidentStatus status;
    private String statusMessage;
    private boolean isHandLeftException;
    private boolean isHandRightException;

    public String getResidentFingerprintLeftStr() {
        if (Objects.isNull(residentFingerprintLeft) || residentFingerprintLeft.length < 10) {
            return null;
        }
        return Base64.getEncoder().encodeToString(residentFingerprintLeft);
    }

    public String getResidentFingerprintRightStr() {
        if (Objects.isNull(residentFingerprintRight) || residentFingerprintRight.length < 10) {
            return null;
        }
        return Base64.getEncoder().encodeToString(residentFingerprintRight);
    }
    @Override
    public String toString() {
        return "ResidentPending{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", placeOfBirthId=" + placeOfBirthId +
                ", addressId=" + addressId +
                ", gender='" + gender + '\'' +
                ", languageId=" + languageId +
                ", status=" + status +
                '}';
    }
}
