package com.jp.backend.domain.file.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class S3BucketConfig {
	@Value("${cloud.aws.credentials.accessKey}")
	private String accessKey;
	@Value("${cloud.aws.credentials.secretKey}")
	private String secretKey;
	@Value("${cloud.aws.region.static}")
	private String region;

	// AWS S3 서비스에 접근하기 위한 Amazon S3 클라이언트 객체를 생성 --> 애플리케이션 전반에서 AWS S3 서비스 사용 가능
	@Bean
	public AmazonS3 amazonS3Client() {
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		// AWS 서비스에 엑세스하기 위한 기본적인 인증정보 제공
		// accessKey와 secretKey를 가지고 AWSCredentials 인스턴스 생성

		return AmazonS3ClientBuilder // AmazonS3 클라이언트 인스턴스를 생성하고 구성하기 위한 빌더 패턴 클래스
			.standard() // AmazonS3ClientBuilder의 기본 구성을 사용하여 빌더 인스턴스 생성
			.withCredentials(new AWSStaticCredentialsProvider(credentials)) // 생성된 AmazonS3 클라이언트에 인증 정보 제공
			.withRegion(region) // 클라이언트가 작업을 수행할 AWS 리전 설정
			.build();
	}
}
