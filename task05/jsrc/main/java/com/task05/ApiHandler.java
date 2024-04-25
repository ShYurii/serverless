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


public class ApiHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    //	private static final String DYNAMODB_TABLE_NAME = "Events";
    private static final String DYNAMODB_TABLE_NAME = "cmtr-c5efef97-Events";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        DynamoDbClient client = DynamoDbClient.create();

        Map<String, Object> output = new HashMap<>();

        try {
            String id = UUID.randomUUID().toString();

            String createdAt = java.time.Clock.systemUTC().instant().toString();

            Map<String, AttributeValue> item = new HashMap<>();
			item.put("id", AttributeValue.builder().s(id).build());
//            item.put("principalId", AttributeValue.builder().s(input.get("principalId").toString()).build());
            item.put("createdAt", AttributeValue.builder().s(createdAt).build());

            Object principalId = input.get("principalId");
            if (principalId == null) {
                output.put("statusCode", 400);
                output.put("error", "Missing principalId");
                return output;
            }
//            item.put("principalId", AttributeValue.builder().s(principalId.toString()).build());
            item.put("principalId", AttributeValue.builder().n(principalId.toString()).build());

            Object content = input.get("content");
            if (content == null) {
                output.put("statusCode", 400);
                output.put("error", "Missing content");
                return output;
            }

            item.put("body", AttributeValue.builder().s(objectMapper.writeValueAsString(input.get("content"))).build());

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(DYNAMODB_TABLE_NAME)
                    .item(item)
                    .build();

            PutItemResponse putItemResponse = client.putItem(putItemRequest);

            output.put("statusCode", 201);
//			output.put("event", item);
            output.put("event", Map.of(
                    "createdAt", item.get("createdAt").s(),
//                    "principalId", item.get("principalId").s(),
//                    "principalId", item.get("principalId").n(),
                    "principalId", Integer.parseInt(item.get("principalId").n()),

                    "id", item.get("id").s(),
//                    "body", item.get("body").s()
                    "body", objectMapper.readValue(item.get("body").s(), Map.class)
            ));
        } catch (Exception e) {
            output.put("statusCode", 500);
            output.put("exception", e.toString());
        }

        return output;
    }
}
