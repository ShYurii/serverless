package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.AWSLogsClientBuilder;
import com.amazonaws.services.logs.model.InputLogEvent;
import com.amazonaws.services.logs.model.PutLogEventsRequest;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.events.SnsEvents;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.syndicate.deployment.model.RegionScope;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Collections;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "sns_handler-role"
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@SnsEvents
@SnsEventSource(
		targetTopic="lambda_topic"
//		regionScope= RegionScope.DEFAULT
)
//public class SnsHandler implements RequestHandler<SNSEvent, Void> {
//
//	private static final Logger LOG = LogManager.getLogger(SnsHandler.class);
//
//	@Override
//	public Void handleRequest(SNSEvent event, Context context) {
//		if (event != null && event.getRecords() != null) {
//			for (SNSEvent.SNSRecord record : event.getRecords()) {
//				if (record != null && record.getSNS() != null) {
//					LOG.info("New message from SNS: " + record.getSNS().getMessage());
//				}
//			}
//		}
//		return null;
//	}
//}


public class SnsHandler implements RequestHandler<SNSEvent, Void> {

	private AWSLogs cloudWatchLogs = AWSLogsClientBuilder.defaultClient();

	@Override
	public Void handleRequest(SNSEvent event, Context context) {
		if (event == null || event.getRecords() == null) {
			context.getLogger().log("No records in the SNS event");
			return null;
		}

		for (SNSEvent.SNSRecord record : event.getRecords()) {
			if (record == null || record.getSNS() == null || record.getSNS().getMessage() == null) {
				context.getLogger().log("No message body in the SNS message");
				continue;
			}

			String messageBody = record.getSNS().getMessage();

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