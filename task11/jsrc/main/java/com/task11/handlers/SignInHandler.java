package com.task11.handlers;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ListUsersRequest;
import com.amazonaws.services.cognitoidp.model.ListUsersResult;
import com.amazonaws.services.cognitoidp.model.UserType;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task11.constant.Constants;
import com.task11.entity.SignInRequestModel;
import com.task11.utils.UserPoolUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

import static com.task11.constant.Constants.SignInParams.PASSWORD;
import static com.task11.constant.Constants.SignInParams.USERNAME;

public class SignInHandler {

    private static final String USER_POOL_NAME = "cmtr-c5efef97-simple-booking-userpool-test";

    private static final AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static APIGatewayProxyResponseEvent execute(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        try {
            Map<String, String> body = objectMapper.readValue(request.getBody(), Map.class);
            SignInRequestModel signInRequestModel = new SignInRequestModel(body);

            ListUsersRequest listUsersRequest = new ListUsersRequest()
                    .withUserPoolId(UserPoolUtils.getUserPoolId(USER_POOL_NAME))
                    .withLimit(50);
            ListUsersResult result = cognitoClient.listUsers(listUsersRequest);
            List<UserType> userTypeList = result.getUsers().stream()
                    .filter(userType ->
                            userType.getUsername().equals(signInRequestModel.getEmail())).collect(Collectors.toList());

            if (userTypeList.isEmpty()) {
                response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
                return response;
            }

            String clientId = UserPoolUtils.getUserClientId(USER_POOL_NAME);
            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withUserPoolId(UserPoolUtils.getUserPoolId(USER_POOL_NAME))
                    .withClientId(clientId)
                    .addAuthParametersEntry(USERNAME, signInRequestModel.getEmail())
                    .addAuthParametersEntry(PASSWORD, signInRequestModel.getPassword());

            AdminInitiateAuthResult authResult = cognitoClient.adminInitiateAuth(authRequest);
            AuthenticationResultType authResultType = authResult.getAuthenticationResult();
            String accessToken = authResultType.getAccessToken();

            response.setStatusCode(Constants.StatusCodes.SUCCESS);
            response.setBody("{\"accessToken\": \"" + accessToken + "\"}");

        } catch (IOException exception) {
            context.getLogger().log("Exception: " + exception);

            response.setStatusCode(Constants.StatusCodes.BAD_REQUEST);
        }

        // headers CORS
        Map<String, String> headers = new HashMap<>();
        headers.put("Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");
        headers.put("Accept-Version", "*");

        response.setHeaders(headers);

        return response;
    }
}