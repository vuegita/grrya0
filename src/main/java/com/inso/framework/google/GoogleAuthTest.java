package com.inso.framework.google;

/*
 * Not really a unit test- but it shows usage
 */
public class GoogleAuthTest {

    public static void main(String[] args) {
        genSecretTest();
//        authTest();
        }
    public static void genSecretTest() {
        String secret = GoogleAuthenticator.generateSecretKey();
        String url = GoogleAuthenticator.getQRBarcodeURL("admin", "dev", secret);
        System.out.println("Please register " + url);
        System.out.println("Secret key is " + secret);
    }

    // Change this to the saved secret from the running the above test.
    static String savedSecret = "BSZLN576KZD2ZKJA";

    public static void authTest() {
        // enter the code shown on device. Edit this and run it fast before the code expires!
        long code = 432939;
        long t = System.currentTimeMillis();
        GoogleAuthenticator ga = new GoogleAuthenticator();
        ga.setWindowSize(2); //should give 5 * 30 seconds of grace...
        boolean r = ga.check_code(savedSecret, code, t);
        System.out.println("Check code = " + r);
    }
}
