package com.task04;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

//import com.amazonaws.services.lambda.runtime.Context;
//import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "sns_handler",
	roleName = "sns_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
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
