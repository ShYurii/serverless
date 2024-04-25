package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.syndicate.deployment.annotations.events.SqsEvents;
import com.syndicate.deployment.annotations.events.SqsTriggerEventSource;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import com.amazonaws.services.lambda.runtime.events.SQSEvent;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

@LambdaHandler(lambdaName = "sqs_handler",
	roleName = "sqs_handler-role"
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@SqsEvents
@SqsTriggerEventSource (
		targetQueue="async_queue",
		batchSize=1
)
//public class SqsHandler implements RequestHandler<SQSEvent, Void> {
//
//	private static final Logger LOG = LogManager.getLogger(SqsHandler.class);
//
//	@Override
//	public Void handleRequest(SQSEvent event, Context context) {
//		for (SQSEvent.SQSMessage msg : event.getRecords()) {
//			LOG.info("New message from SQS: " + msg.getBody());
//		}
//		return null;
//	}
//}

public class SqsHandler implements RequestHandler<SQSEvent, Void> {

	private AWSLogs cloudWatchLogs = AWSLogsClientBuilder.defaultClient();

	@Override
	public Void handleRequest(SQSEvent event, Context context) {
		if (event == null || event.getRecords() == null) {
			context.getLogger().log("No records in the SQS event");
			return null;
		}

		for (SQSEvent.SQSMessage msg : event.getRecords()) {
			if (msg == null || msg.getBody() == null) {
				context.getLogger().log("No message body in the SQS message");
				continue;
			}

			String messageBody = msg.getBody();

			InputLogEvent logEvent = new InputLogEvent()
					.withTimestamp(System.currentTimeMillis())
					.withMessage(messageBody);

			PutLogEventsRequest putLogEventsRequest = new PutLogEventsRequest()
					.withLogGroupName("your-log-group")
					.withLogStreamName("your-log-stream")
					.withLogEvents(Collections.singletonList(logEvent));

			cloudWatchLogs.putLogEvents(putLogEventsRequest);
		}

		return null;
	}
}