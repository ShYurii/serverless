package com.task05;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;


//import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
//import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
//import com.amazonaws.services.dynamodbv2.model.PutItemResult;
//import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.util.HashMap;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)



public class ApiHandler implements RequestHandler<Map<String,Object>, Map<String,Object>> {

//	private static final String DYNAMODB_TABLE_NAME = "Events";
	private static final String DYNAMODB_TABLE_NAME = "cmtr-c5efef97-Events";
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public Map<String,Object> handleRequest(Map<String, Object> input, Context context) {
		DynamoDbClient client = DynamoDbClient.create();

		Map<String, Object> output = new HashMap<>();

		try {
			String id = UUID.randomUUID().toString();
			String createdAt = java.time.Clock.systemUTC().instant().toString();

			Map<String, AttributeValue> item = new HashMap<>();
//			item.put("id", AttributeValue.builder().s(id).build());
			item.put("Id", AttributeValue.builder().n(id).build());
//			item.put("Id", AttributeValue.builder().s(id).build());
			item.put("principalId", AttributeValue.builder().s(input.get("principalId").toString()).build());
			item.put("createdAt", AttributeValue.builder().s(createdAt).build());
			item.put("body", AttributeValue.builder().s(objectMapper.writeValueAsString(input.get("content"))).build());

			PutItemRequest putItemRequest = PutItemRequest.builder()
					.tableName(DYNAMODB_TABLE_NAME)
					.item(item)
					.build();

			PutItemResponse putItemResponse = client.putItem(putItemRequest);

			output.put("statusCode", 201);
			output.put("event", item);
		} catch (Exception e) {
			output.put("statusCode", 500);
			output.put("exception", e.toString());
		}

		return output;
	}
}




//public class ApiHandler implements RequestHandler<Object, Map<String, Object>> {
//
//	public Map<String, Object> handleRequest(Object request, Context context) {
//		System.out.println("Hello from lambda");
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		resultMap.put("statusCode", 200);
//		resultMap.put("body", "Hello from Lambda");
//		return resultMap;
//	}
//}



//public class ApiHandler implements RequestHandler<Map<String,Object>, Map<String,Object>> {
//
//	private static final String DYNAMODB_TABLE_NAME = "Events";
//	private static final ObjectMapper objectMapper = new ObjectMapper();
//
//	@Override
//	public Map<String,Object> handleRequest(Map<String, Object> input, Context context) {
//		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
//
//		String id = UUID.randomUUID().toString();
//		String createdAt = java.time.Clock.systemUTC().instant().toString();
//
//		Map<String, AttributeValue> item = new HashMap<>();
//		item.put("id", new AttributeValue(id));
//		item.put("principalId", new AttributeValue(input.get("principalId").toString()));
//		item.put("createdAt", new AttributeValue(createdAt));
//		item.put("body", new AttributeValue(objectMapper.writeValueAsString(input.get("content"))));
//
//		PutItemRequest putItemRequest = new PutItemRequest(DYNAMODB_TABLE_NAME, item);
//		PutItemResult putItemResult = client.putItem(putItemRequest);
//
//		Map<String, Object> output = new HashMap<>();
//		output.put("statusCode", 201);
//		output.put("event", item);
//
//		return output;
//	}
//}

//
//
//public class ApiHandler implements RequestHandler<Map<String,Object>, Map<String,Object>> {
//
//	private static final String DYNAMODB_TABLE_NAME = "Events";
//	private static final ObjectMapper objectMapper = new ObjectMapper();
//
//	@Override
//	public Map<String,Object> handleRequest(Map<String, Object> input, Context context) {
//		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.defaultClient();
//		Map<String, Object> output = new HashMap<>();
//
//		try {
//			String id = UUID.randomUUID().toString();
//			String createdAt = java.time.Clock.systemUTC().instant().toString();
//
//			Map<String, AttributeValue> item = new HashMap<>();
//			item.put("id", new AttributeValue(id));
//			item.put("principalId", new AttributeValue(input.get("principalId").toString()));
//			item.put("createdAt", new AttributeValue(createdAt));
//			item.put("body", new AttributeValue(objectMapper.writeValueAsString(input.get("content"))));
//
//			PutItemRequest putItemRequest = new PutItemRequest(DYNAMODB_TABLE_NAME, item);
//			PutItemResult putItemResult = client.putItem(putItemRequest);
//
//			output.put("statusCode", 201);
//			output.put("event", item);
//		} catch (Exception e) {
//			output.put("statusCode", 500);
//			output.put("exception", e.toString());
//		}
//
//		return output;
//	}
//}
