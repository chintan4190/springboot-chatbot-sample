package com.example.websocketdemo.service;

import com.example.websocketdemo.config.Constants;
import com.example.websocketdemo.model.ChatMessage;
import com.google.cloud.dialogflow.v2.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class DialogflowService {

    public ChatMessage getDialogflowResponse(ChatMessage chatMessageReq) throws Exception {
        ChatMessage chatMessageRes = null;

        chatMessageRes = detectIntentTexts(Constants.PROJECT_ID, Arrays.asList(chatMessageReq.getContent()),UUID.randomUUID().toString(),"EN",chatMessageReq);

        return chatMessageRes;
    }

    public static ChatMessage detectIntentTexts(String projectId, List<String> texts, String sessionId,
                                         String languageCode, ChatMessage chatMessageReq) throws Exception {
       ChatMessage chatMessageRes = new ChatMessage();
        // Instantiates a client
        try (SessionsClient sessionsClient = SessionsClient.create()) {
            // Set the session name using the sessionId (UUID) and projectID (my-project-id)
            SessionName session = SessionName.of(projectId, sessionId);
            System.out.println("Session Path: " + session.toString());

            // Detect intents for each text input
            for (String text : texts) {
                // Set the text (hello) and language code (en-US) for the query
                TextInput.Builder textInput = TextInput.newBuilder().setText(text).setLanguageCode(languageCode);

                // Build the query with the TextInput
                QueryInput queryInput = QueryInput.newBuilder().setText(textInput).build();

                // Performs the detect intent request
                DetectIntentResponse response = sessionsClient.detectIntent(session, queryInput);

                // Display the query result
                QueryResult queryResult = response.getQueryResult();
                chatMessageRes.setContent(queryResult.getFulfillmentText());
                chatMessageRes.setType(ChatMessage.MessageType.CHAT);
                chatMessageRes.setSender("Bot");


                System.out.println("====================");
                System.out.format("Query Text: '%s'\n", queryResult.getQueryText());
                System.out.format("Detected Intent: %s (confidence: %f)\n",
                        queryResult.getIntent().getDisplayName(), queryResult.getIntentDetectionConfidence());
                System.out.format("Fulfillment Text: '%s'\n", queryResult.getFulfillmentText());
            }
        }
        return chatMessageRes;
    }
}
