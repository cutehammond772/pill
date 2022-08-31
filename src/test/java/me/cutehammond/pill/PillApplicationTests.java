package me.cutehammond.pill;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;

@SpringBootTest
@Slf4j
class PillApplicationTests {

	@Test
	void contextLoads() {
		SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		String seckey = Encoders.BASE64.encode(key.getEncoded());
		log.info(seckey);
	}

}
