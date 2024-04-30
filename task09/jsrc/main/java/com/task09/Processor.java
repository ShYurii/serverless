package com.task09;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;

import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
import com.syndicate.deployment.model.TracingMode;

import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import org.example.OpenMeteoAPI;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "processor",
	roleName = "processor-role",
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
		tracingMode= TracingMode.Active
)

@LambdaLayer(
		layerName = "sdk-layer",
		libraries = {"lib/open_meteo_api-1.0-SNAPSHOT.jar"},
		runtime = DeploymentRuntime.JAVA11,
//		architectures = {Architecture.ARM64},
		artifactExtension = ArtifactExtension.ZIP
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)

public class Processor implements RequestHandler<Object, String> {

	private DynamoDbClient dynamoDB;
	private String DYNAMODB_TABLE_NAME = "cmtr-c5efef97-Weather-test";

	public Processor() {
		this.dynamoDB = DynamoDbClient.create();
	}

	public String handleRequest(Object input, Context context) {
		OpenMeteoAPI api = new OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
		String forecast;

		try {
			forecast = api.getWeatherForecast();
			JSONObject forecastJson = new JSONObject(forecast);

			Map<String, AttributeValue> item = new HashMap<>();
			item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
			item.put("forecast", AttributeValue.builder().s(forecastJson.toString()).build());

			PutItemRequest putItemRequest = PutItemRequest.builder()
					.tableName(DYNAMODB_TABLE_NAME)
					.item(item)
					.build();

			dynamoDB.putItem(putItemRequest);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return forecast;
	}
}