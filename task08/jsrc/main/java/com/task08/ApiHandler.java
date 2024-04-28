package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.task08.OpenMeteoAPI;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
	isPublishVersion = true,
	aliasName = "${lambdas_alias_name}",
	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)


public class ApiHandler implements RequestHandler<Object, String> {
	@Override
	public String handleRequest(Object input, Context context) {
		com.task08.OpenMeteoAPI api = new com.task08.OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
		String forecast;
		try {
			forecast = api.getWeatherForecast();
		} catch (Exception e) {
			forecast = "Error: " + e.getMessage();
		}
		return forecast;
	}
}