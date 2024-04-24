package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.events.SnsEventSource;
import com.syndicate.deployment.annotations.events.SnsEvents;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
//import com.syndicate.deployment.model.RetentionSetting;

//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

//import com.syndicate.deployment.annotations.resources.SnsTriggerEventSource;
//import software.amazon.awscdk.services.lambda.eventsources.SqsEventSource;


import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "sns_handler-role"
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)

@SnsEvents
@SnsEventSource(
		targetTopic="lambda_topic"
//		regionScope="eu-central-1"
)
public class SnsHandler implements RequestHandler<SNSEvent, Void> {

	private static final Logger LOG = LogManager.getLogger(SnsHandler.class);

	@Override
	public Void handleRequest(SNSEvent event, Context context) {
		for (SNSEvent.SNSRecord record : event.getRecords()) {
			LOG.info("New message from SNS: " + record.getSNS().getMessage());
		}
		return null;
	}
}
