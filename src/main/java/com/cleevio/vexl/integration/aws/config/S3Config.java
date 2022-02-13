//package com.cleevio.vexl.integration.aws.config;
//
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.regions.Regions;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//@RequiredArgsConstructor
//public class S3Config {
//
//	@Value("${aws.s3.access}")
//	private final String access;
//	@Value("${aws.s3.secret}")
//	private final String secret;
//
//	@Bean
//	public AmazonS3 s3client() {
//		AWSCredentials credentials = new BasicAWSCredentials(access, secret);
//
//		return AmazonS3ClientBuilder
//				.standard()
//				.withCredentials(new AWSStaticCredentialsProvider(credentials))
//				.withRegion(Regions.EU_CENTRAL_1)
//				.build();
//	}
//
//}
