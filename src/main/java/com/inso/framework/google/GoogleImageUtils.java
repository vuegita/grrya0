package com.inso.framework.google;

import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GoogleImageUtils {


    /**
     *
     * @param response
     * @param name           username + "@" + projectName  +"-" + MyEnvironment.getEnv();
     * @param googleKey
     */
    public static void getGoogleKeyEWM(HttpServletResponse response, String name, String googleKey) {
        String qcodeUrl = "otpauth://totp/%s?secret=%s";
        String url = String.format(qcodeUrl, name, googleKey);
        if (url != null && !"".equals(url)) {
            ServletOutputStream stream = null;
            try {
                int width = 200;
                int height = 200;
                stream = response.getOutputStream();
                QRCodeWriter writer = new QRCodeWriter();
                BitMatrix m = writer.encode(url, BarcodeFormat.QR_CODE, height, width);
                MatrixToImageWriter.writeToStream(m, "png", stream);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.flush();
                        stream.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        }
    }
}
