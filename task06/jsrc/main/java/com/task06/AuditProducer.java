package com.task06;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.DynamoDbTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent;
import com.amazonaws.services.lambda.runtime.events.DynamodbEvent.DynamodbStreamRecord;
import com.syndicate.deployment.annotations.events.DynamoDbEvents;



@LambdaHandler(lambdaName = "audit_producer",
	roleName = "audit_producer-role"
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@DynamoDbTriggerEventSource(
		targetTable="Configuration",
		batchSize=1
)

public class AuditProducer implements RequestHandler<DynamodbEvent, Void> {

//	private static final String DYNAMODB_TABLE_NAME = "cmtr-c5efef97-Audit";
	private static final String DYNAMODB_TABLE_NAME = "Audit";
	private final DynamoDbClient client = DynamoDbClient.create();

	@Override
	public Void handleRequest(DynamodbEvent event, Context context) {
		for (DynamodbStreamRecord record : event.getRecords()) {
			if ("INSERT".equals(record.getEventName())) {
				handleInsert(record);
			} else if ("MODIFY".equals(record.getEventName())) {
				handleModify(record);
			} else if ("REMOVE".equals(record.getEventName())) {
				handleRemove(record);
			}
		}
		return null;
	}

	private void handleInsert(DynamodbStreamRecord record) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
		item.put("itemKey", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("key").getS()).build());
		item.put("modificationTime", AttributeValue.builder().s(Instant.now().toString()).build());
		item.put("newValue", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("value").getN()).build());
		putItem(item);
	}
//private void handleInsert(DynamodbStreamRecord record) {
//	Map<String, AttributeValue> item = new HashMap<>();
//	item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
//	item.put("itemKey", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("key").getS()).build());
//	item.put("modificationTime", AttributeValue.builder().s(Instant.now().toString()).build());
//
//	Map<String, AttributeValue> newValue = new HashMap<>();
//	newValue.put("key", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("key").getS()).build());
//	newValue.put("value", AttributeValue.builder().n(record.getDynamodb().getNewImage().get("value").getN()).build());
//	item.put("newValue", AttributeValue.builder().m(newValue).build());
//
//	putItem(item);
//}



	private void handleModify(DynamodbStreamRecord record) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
		item.put("itemKey", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("key").getS()).build());
		item.put("modificationTime", AttributeValue.builder().s(Instant.now().toString()).build());
		item.put("updatedAttribute", AttributeValue.builder().s("value").build());
		item.put("oldValue", AttributeValue.builder().s(record.getDynamodb().getOldImage().get("value").getN()).build());
		item.put("newValue", AttributeValue.builder().s(record.getDynamodb().getNewImage().get("value").getN()).build());
		putItem(item);
	}

	private void handleRemove(DynamodbStreamRecord record) {
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build());
		item.put("itemKey", AttributeValue.builder().s(record.getDynamodb().getOldImage().get("key").getS()).build());
		item.put("modificationTime", AttributeValue.builder().s(Instant.now().toString()).build());
		item.put("oldValue", AttributeValue.builder().s(record.getDynamodb().getOldImage().get("value").getN()).build());
		putItem(item);
	}

	private void putItem(Map<String, AttributeValue> item) {
		PutItemRequest putItemRequest = PutItemRequest.builder()
				.tableName(DYNAMODB_TABLE_NAME)
				.item(item)
				.build();
		client.putItem(putItemRequest);
	}
}