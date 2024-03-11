package tg.gouv.anid.residentid.deduplicationservice.config;

import jakarta.persistence.AttributeConverter;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.util.Base64;
import java.util.Objects;

@Component
public class ByteArrayAttributeEncryptor implements AttributeConverter<byte[], byte[]> {

    private static final String AES = "AES";
    private static final String SECRET = "F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShV";

    private final Key key;
    private final Cipher cipher;

    public ByteArrayAttributeEncryptor() throws Exception {
        key = new SecretKeySpec(SECRET.getBytes(), AES);
        cipher = Cipher.getInstance(AES);
    }

    @Override
    public byte[] convertToDatabaseColumn(byte[] attribute) {
        try {
            if (Objects.nonNull(attribute)) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                String str = Base64.getEncoder().encodeToString(cipher.doFinal(attribute));
                return str.getBytes();
            }
            return new byte[0];
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] convertToEntityAttribute(byte[] dbData) {
        try {
            if (Objects.nonNull(dbData)) {
                cipher.init(Cipher.DECRYPT_MODE, key);
                String str = new String(cipher.doFinal(Base64.getDecoder().decode(new String(dbData))));
                return str.getBytes();
            }
            return new byte[0];
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}