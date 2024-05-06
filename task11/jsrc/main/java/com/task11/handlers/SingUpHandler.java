package com.task11.handlers;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task11.constant.Constants;
import com.task11.entity.SignUpRequestModel;
import com.task11.utils.UserPoolUtils;

import java.util.Map;
import java.util.HashMap;

public class SingUpHandler {

    private static final String USER_POOL_NAME = "cmtr-c5efef97-simple-booking-userpool-test";

    private static final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

        try {
            Map<String, String> body = objectMapper.readValue(request.getBody(), Map.class);
            SignUpRequestModel signUpRequestModel = new SignUpRequestModel(body);
            String clientId = UserPoolUtils.getUserClientId(USER_POOL_NAME);
            String userPoolId = UserPoolUtils.getUserPoolId(USER_POOL_NAME);

            SignUpRequest signUpRequest = new SignUpRequest()
                    .withClientId(clientId)
                    .withUsername(signUpRequestModel.getEmail())
                    .withPassword(signUpRequestModel.getPassword())
                    .withUserAttributes(
                            new AttributeType().withName(Constants.SignUpParams.FIRST_NAME).withValue(signUpRequestModel.getFirstName()),
                            new AttributeType().withName(Constants.SignUpParams.LAST_NAME).withValue(signUpRequestModel.getLastName()),
                            new AttributeType().withName(Constants.SignUpParams.EMAIL).withValue(signUpRequestModel.getEmail()));
            cognitoClient.signUp(signUpRequest);

            AdminConfirmSignUpRequest adminConfirmSignUp = new AdminConfirmSignUpRequest()
                    .withUsername(signUpRequestModel.getEmail())
                    .withUserPoolId(userPoolId);

            cognitoClient.adminConfirmSignUp(adminConfirmSignUp);

            response.setStatusCode(Constants.StatusCodes.SUCCESS);
        } catch (Exception exception) {
            context.getLogger().log("Exception: " + exception);

            response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
        }

        // Добавить заголовки CORS
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");

        response.setHeaders(headers);

        return response;
    }
}