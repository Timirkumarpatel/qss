package io.itpl.qss.encoder;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class QrDecoder {

	 public static String decodeQRCode(byte[] qrCodeimage) throws IOException {
	    	ByteArrayInputStream imageStream = new ByteArrayInputStream(qrCodeimage);
	        BufferedImage bufferedImage = ImageIO.read(imageStream);
	        LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
	        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
	        
	        try {
	            Result result = new MultiFormatReader().decode(bitmap);
	            return result.getText();
	        } catch (NotFoundException e) {
	            System.out.println("There is no QR code in the image");
	            return null;
	        }
	    }
}
