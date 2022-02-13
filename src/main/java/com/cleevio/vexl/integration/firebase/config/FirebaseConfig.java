//package com.cleevio.vexl.integration.firebase.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.io.ClassPathResource;
//
//import javax.annotation.PostConstruct;
//import java.io.IOException;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class FirebaseConfig {
//
//	@Value("${firebase.config.path}")
//	private final String configPath;
//
//	@PostConstruct
//	public void init() {
//		if (configPath.isBlank()) {
//			return;
//		}
//
//		try {
//			var options = FirebaseOptions.builder()
//					.setCredentials(GoogleCredentials.fromStream(new ClassPathResource(configPath).getInputStream())).build();
//
//			if (FirebaseApp.getApps().isEmpty()) {
//				FirebaseApp.initializeApp(options);
//				log.info("Firebase application has been initialized");
//			}
//
//		} catch (IOException e) {
//			log.error(e.getMessage());
//		}
//	}
//}
