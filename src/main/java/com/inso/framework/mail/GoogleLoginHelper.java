package com.inso.framework.mail;

import com.alibaba.fastjson.JSONObject;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.inso.framework.log.Log;
import com.inso.framework.log.LogFactory;
import com.inso.modules.paychannel.helper.PaymentRequestHelper;

import java.util.Collections;


public class GoogleLoginHelper {

    private static Log LOG = LogFactory.getLog(GoogleLoginHelper.class);

    // https://developers.google.com/identity/sign-in/web/backend-auth?hl=zh-cn
    private static final String URL = "https://oauth2.googleapis.com/tokeninfo?id_token=";



    private static GoogleIdTokenVerifier mVerifier;
    static {
        String clientId = "YOUR_CLIENT_ID";
        HttpTransport httpTransport = new NetHttpTransport();
        JsonFactory jsonFactory = new JacksonFactory();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(httpTransport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(clientId))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();
        mVerifier = verifier;
    }

    public static String getEmailAddress(String accessToken)
    {
        String url = URL + accessToken;
        JSONObject jsonObject = PaymentRequestHelper.getInstance().syncGetForJSONResult(url, null);
        if(jsonObject == null || jsonObject.isEmpty())
        {
            return null;
        }
        String email = jsonObject.getString("email");
        return email;
    }

    public static String verifyAndGetEmail(String idTokenString)
    {
        try {
            GoogleIdToken idToken = mVerifier.verify(idTokenString);
            if (idToken == null) {

                //System.out.println("Invalid ID token.");
                return null;
            }

            Payload payload = idToken.getPayload();

            // Print user identifier
//            String userId = payload.getSubject();
//            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
//            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//            String name = (String) payload.get("name");
//            String pictureUrl = (String) payload.get("picture");
//            String locale = (String) payload.get("locale");
//            String familyName = (String) payload.get("family_name");
//            String givenName = (String) payload.get("given_name");

            return email;
        } catch (Exception e) {
            LOG.info("handle error:", e);
        }
        return null;
    }


}
