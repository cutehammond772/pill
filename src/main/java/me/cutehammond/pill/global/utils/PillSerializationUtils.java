package me.cutehammond.pill.global.utils;

import lombok.NonNull;
import org.springframework.util.SerializationUtils;

import java.util.Base64;

public final class PillSerializationUtils {

    public static String serialize(@NonNull Object obj) {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(@NonNull String serialized, @NonNull Class<T> cls) {
        return cls.cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(serialized)));
    }

}
