package tg.gouv.anid.residentid.deduplicationservice.config;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {

    private static final String AES = "AES";
    private static final String SECRET = "F-JaNdRgUkXp2s5v8y/B?E(G+KbPeShV";

    private final Key key;
    private final Cipher cipher;

    public AttributeEncryptor() throws NoSuchPaddingException, NoSuchAlgorithmException {
        key = new SecretKeySpec(SECRET.getBytes(), AES);
        cipher = Cipher.getInstance(AES);
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            if (StringUtils.hasText(attribute)) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
                return Base64.getEncoder().encodeToString(cipher.doFinal(attribute.getBytes()));
            }
            return null;
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            if (StringUtils.hasText(dbData)) {
                cipher.init(Cipher.DECRYPT_MODE, key);
                return new String(cipher.doFinal(Base64.getDecoder().decode(dbData)), StandardCharsets.UTF_8);
            }
            return null;
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new IllegalStateException(e);
        }
    }
}