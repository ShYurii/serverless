package com.task07;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.RuleEventSource;
import com.syndicate.deployment.annotations.events.RuleEvents;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@LambdaHandler(lambdaName = "uuid_generator",
	roleName = "uuid_generator-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@RuleEvents
@RuleEventSource(targetRule="uuid_trigger")

public class UuidGenerator implements RequestHandler<Object, String> {

//	private static final String BUCKET_NAME = "uuid-storage";
	private static final String BUCKET_NAME = "cmtr-c5efef97-uuid-storage-test";

	@Override
	public String handleRequest(Object input, Context context) {
		AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();

//		String uuids = Stream.generate(UUID::randomUUID)
//				.limit(10)
//				.map(UUID::toString)
//				.collect(Collectors.joining(","));
		String uuids = Stream.generate(UUID::randomUUID)
				.limit(10)
				.map(uuid -> "\"" + uuid.toString() + "\"")
				.collect(Collectors.joining(","));

		String content = String.format("{\"ids\":[%s]}", uuids);
		String fileName = Instant.now().toString();

		s3Client.putObject(BUCKET_NAME, fileName, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), new ObjectMetadata());

		return String.format("File %s created in bucket %s", fileName, BUCKET_NAME);
	}
}
