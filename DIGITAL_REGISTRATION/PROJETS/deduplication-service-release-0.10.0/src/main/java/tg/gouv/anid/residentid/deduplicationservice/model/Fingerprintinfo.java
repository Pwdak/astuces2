package tg.gouv.anid.residentid.deduplicationservice.model;

import com.machinezoo.sourceafis.FingerprintTemplate;
import jakarta.persistence.Convert;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;
import tg.gouv.anid.residentid.deduplicationservice.config.AttributeEncryptor;

import java.io.Serializable;


@Getter
@Setter
@NoArgsConstructor
@Table("fingerprintinfo")
public class Fingerprintinfo implements Serializable {
    private Long id;
    @Convert(converter = AttributeEncryptor.class)
    private String fingername;
    @Convert(converter = AttributeEncryptor.class)
    private String imagepath;
    private Long resident_id;
    private FingerprintTemplate template;
    private byte[] fingerprintbyte;
    private byte[] fingerprintimage;

    public FingerprintTemplate getTemplate() {
        return new FingerprintTemplate(fingerprintbyte);
    }
}
