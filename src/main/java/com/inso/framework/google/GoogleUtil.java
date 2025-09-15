package com.inso.framework.google;

public class GoogleUtil {

    public  static boolean checkGoogleCode(String googleKey, String googleCode){
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(5); //should give 5 * 30 seconds of grace...
        return ga.check_code(googleKey, Long.parseLong(googleCode), t);
    }
}
