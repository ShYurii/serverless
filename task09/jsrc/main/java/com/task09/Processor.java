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
//import org.example.OpenMeteoAPI;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.syndicate.deployment.annotations.LambdaUrlConfig;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.TracingMode;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "processor",
		roleName = "processor-role",
		tracingMode = TracingMode.Active
)
@LambdaUrlConfig(
		authType = AuthType.NONE,
		invokeMode = InvokeMode.BUFFERED
)


public class Processor implements RequestHandler<Object, Map<String, Object>> {

	private static final AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClientBuilder.defaultClient();

	@Override
	public Map<String, Object> handleRequest(Object request, Context context) {
		Gson gson = new Gson();
		OpenMeteoWeather meteoAPI = new OpenMeteoWeather();

		Forecast forecast = gson.fromJson(meteoAPI.callApi(), new TypeToken<Forecast>() {
		}.getType());

		// Проверьте, существуют ли 'hourly_units' в полученном прогнозе
		if (forecast.getHourlyUnits() == null) {
			// Если 'hourly_units' отсутствуют, установите их в ожидаемые значения
			forecast.setHourlyUnits(new HourlyUnits("°C", "iso8601"));
		}

		WeatherRecord weatherRecord = new WeatherRecord();
		weatherRecord.setId(UUID.randomUUID().toString());
		weatherRecord.setForecast(forecast);

		DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB);
		mapper.save(weatherRecord);

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("statusCode", 200);
		resultMap.put("body", "Success");
		return resultMap;
	}
}



//@LambdaHandler(lambdaName = "processor",
//	roleName = "processor-role",
////	isPublishVersion = true,
////	aliasName = "${lambdas_alias_name}",
////	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED,
//		tracingMode= TracingMode.Active
//)
////
////@LambdaLayer(
////		layerName = "sdk-layer",
////		libraries = {"lib/open_meteo_api-1.0-SNAPSHOT.jar"},
////		runtime = DeploymentRuntime.JAVA11,
//////		architectures = {Architecture.ARM64},
////		artifactExtension = ArtifactExtension.ZIP
////)
//@LambdaUrlConfig(
//		authType = AuthType.NONE,
//		invokeMode = InvokeMode.BUFFERED
//)
//
//public class Processor implements RequestHandler<Object, String> {
//
//	private DynamoDbClient dynamoDB;
//	private String DYNAMODB_TABLE_NAME = "cmtr-c5efef97-Weather-test";
//
//	public Processor() {
//		this.dynamoDB = DynamoDbClient.create();
//	}
//
//	public String handleRequest(Object input, Context context) {
//		OpenMeteoAPI api = new OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
//		String forecast;
//
//		try {
//			forecast = api.getWeatherForecast();
//			JSONObject forecastJson = new JSONObject(forecast);
//
//			Map<String, AttributeValue> item = new HashMap<>();
//			item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
//			item.put("forecast", AttributeValue.builder().s(forecastJson.toString()).build());
//
//			PutItemRequest putItemRequest = PutItemRequest.builder()
//					.tableName(DYNAMODB_TABLE_NAME)
//					.item(item)
//					.build();
//
//			dynamoDB.putItem(putItemRequest);
//
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return forecast;
//	}
//}

//-----------v2
//
//
//public class Processor implements RequestHandler<Object, String> {
//
//	private DynamoDbClient dynamoDB;
//	private String DYNAMODB_TABLE_NAME = "Weather";
//
//	public Processor() {
//		this.dynamoDB = DynamoDbClient.create();
//	}
//
//	public String handleRequest(Object input, Context context) {
//		OpenMeteoAPI api = new OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
//		String forecast;
//
//		try {
//			forecast = api.getWeatherForecast();
//			JSONObject forecastJson = new JSONObject(forecast);
//
//			Map<String, AttributeValue> item = new HashMap<>();
//			item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
//			item.put("latitude", AttributeValue.builder().n(String.valueOf(forecastJson.getDouble("latitude"))).build());
//			item.put("longitude", AttributeValue.builder().n(String.valueOf(forecastJson.getDouble("longitude"))).build());
//			item.put("generationtime_ms", AttributeValue.builder().n(String.valueOf(forecastJson.getDouble("generationtime_ms"))).build());
//			item.put("utc_offset_seconds", AttributeValue.builder().n(String.valueOf(forecastJson.getInt("utc_offset_seconds"))).build());
//			item.put("timezone", AttributeValue.builder().s(forecastJson.getString("timezone")).build());
//			item.put("timezone_abbreviation", AttributeValue.builder().s(forecastJson.getString("timezone_abbreviation")).build());
//			item.put("elevation", AttributeValue.builder().n(String.valueOf(forecastJson.getDouble("elevation"))).build());
//			item.put("hourly_units", AttributeValue.builder().s(forecastJson.getJSONObject("hourly_units").toString()).build());
//			item.put("hourly", AttributeValue.builder().s(forecastJson.getJSONObject("hourly").toString()).build());
//
//			PutItemRequest putItemRequest = PutItemRequest.builder()
//					.tableName(DYNAMODB_TABLE_NAME)
//					.item(item)
//					.build();
//
//			dynamoDB.putItem(putItemRequest);
//
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//		return forecast;
//	}
//}