package ru.mikescherbakov.shortener.utilities;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.regex.Pattern;

@UtilityClass
@Slf4j
public class Utilities {
    private static final Pattern doubleBackslashWithQuote =  Pattern.compile("\\\\\"");

    public static String encodeUrl(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error(Utilities.class.getSimpleName(), e);
            return value;
        }
    }

    public static String decodeUrl(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error(Utilities.class.getSimpleName(), e);
            return value;
        }
    }

    public static <T> String prettyJson(T object) throws JsonProcessingException {
        var mapper = new ObjectMapper();
        try {
            var response = mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);
            return doubleBackslashWithQuote.matcher(response).replaceAll("");
        } catch (JsonProcessingException e) {
            log.error(Utilities.class.getSimpleName(), e);
            throw e;
        }
    }

    public static String appendShortUrlPrefix(String shortUrlWithoutPrefix, String prefix) {
        return prefix + shortUrlWithoutPrefix;
    }

    public static long currentTimeStamp() {
        return Instant.now().getEpochSecond();
    }
}
