package com.task08;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaLayer;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
//import com.syndicate.deployment.model.Architecture;
import com.syndicate.deployment.model.ArtifactExtension;
import com.syndicate.deployment.model.DeploymentRuntime;
//import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;
import com.syndicate.deployment.model.lambda.url.InvokeMode;
import org.example.OpenMeteoAPI;
import org.json.JSONObject;

import java.io.StringReader;


@LambdaHandler(lambdaName = "api_handler",
	roleName = "api_handler-role",
//	isPublishVersion = true,
//	aliasName = "${lambdas_alias_name}",
		layers = {"sdk-layer"}
//		runtime = DeploymentRuntime.JAVA11,
//		architecture = Architecture.ARM64,
//	logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
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
//
//public class ApiHandler{
//
//	public JSONObject handleRequest() {
//		OpenMeteoAPI api = new OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
//		JSONObject forecast;
//		try {
//			forecast = api.getWeatherForecast();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	return forecast;
//}
public class ApiHandler implements RequestHandler<Object, String> {
//	@Override
	public String handleRequest(Object input, Context context) {
		OpenMeteoAPI api = new OpenMeteoAPI(52.5200, 13.4050); // Berlin, Germany
		String forecast;
		String out;
		try {
			forecast = api.getWeatherForecast();
			JSONObject json = new JSONObject(forecast);
			JSONObject hourly = json.getJSONObject("hourly");
			out= String.valueOf(hourly);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
//		try {
////			forecast = api.getWeatherForecast();
//			String response = api.getWeatherForecast();
//
//			context.getLogger().log("Response from OpenMeteoAPI: " + response);
//// Parse the response from the OpenMeteoAPI
//			Gson gson = new GsonBuilder().setLenient().create();
//			JsonReader jsonReader = new JsonReader(new StringReader(response));
//			jsonReader.setLenient(true);
//			JsonObject json = gson.fromJson(jsonReader, JsonObject.class);
//
//			// Extract the needed fields
//			JsonElement time = json.get("time");                        // replace with the actual field name in the response
//			JsonElement temperature_2m = json.get("temperature_2m");    // replace with the actual field name in the response
//			JsonElement relative_humidity_2m = json.get("relative_humidity_2m"); // replace with the actual field name in the response
//			JsonElement wind_speed_10m = json.get("wind_speed_10m");    // replace with the actual field name in the response
//
//			// Construct the expected response
//			JsonObject result = new JsonObject();
//			result.add("time", time);
//			result.add("temperature_2m", temperature_2m);
//			result.add("relative_humidity_2m", relative_humidity_2m);
//			result.add("wind_speed_10m", wind_speed_10m);
//
//			// Convert the result to a JSON string
//			forecast = gson.toJson(result);
//		} catch (Exception e) {
//			forecast = "Error: " + e.getMessage();
//		}
		return out;
	}
}