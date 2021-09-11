package io.itpl.qss.client;



import io.itpl.qss.exception.ImageSizeException;
import io.itpl.qss.exception.InvalidQRConfigException;
import io.itpl.qss.exception.QREncoderException;
import io.itpl.qss.render.QRCode;
import io.itpl.qss.render.StyleConfig;
import io.itpl.qss.utils.QRUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;

import static java.util.Base64.getEncoder;

/**
 * This is the delegate class for generating new QR Code image.
 * One need to create the QRCofig Object prior to create the Object of this class.
 */
public class QRGenerator {
    private QRConfig qrConfig;
    private QRCode myQrCode;
    private final static String ALIAS = "QRGenerator";
    public QRGenerator(QRConfig initialQRConfig) throws QREncoderException, InvalidQRConfigException {
    	if(initialQRConfig != null && initialQRConfig.validate()) {
    		this.qrConfig = initialQRConfig;
    		String data = qrConfig.getAlphanumericData();
    		int version = qrConfig.getQRVersion();
    		int size = qrConfig.getQRSize();
    		int error = qrConfig.getErrorCorrectionLevel();
    		StyleConfig style = qrConfig.getStyleConfig();
    		
    		log("Version:"+version+", size:"+size+",Error Level:"+error);
	        // Step 1: Initialize the QR Code Object
	        myQrCode = new QRCode(data, version, size,error,style);
	        
	        infoLog("QRCode Object Created with:"+myQrCode);
    	}else
    		throw new QREncoderException("QRConfig is null or invalid");
    }
    
/**
 * 
 * @param newQRConfig  QRConfig object to replace with.
 * @throws com.iwantunlimited.qss.exception.QREncoderException in case QRConfiguration is invalid.
 */
    public void setQRConfig(QRConfig newQRConfig)throws QREncoderException {
    	if(newQRConfig != null && newQRConfig.validate())
    		this.qrConfig = newQRConfig;
    	else
    		throw new QREncoderException("QRConfig is null or invalid");
        this.qrConfig = newQRConfig;
    }
/**
 * 
 * @return QRConfig Property.
 */
    public QRConfig getQrConfig() {
        return this.qrConfig;
    }
    /**
    * This method created the QRCode, generate the final image and return it as a BufferedImage. 
    * @return BufferedImage of the QRCode as per specified margin, overly image etc. 
    * @throws IOException in case of failure of generating output image stream.
    * @throws InvalidQRConfigException in case QR config is invalid.
    * @throws QREncoderException in case QR Data encoding is failed.
    * @throws ImageSizeException in case Given Imagesize is invalid or not allowed.
    */
    private BufferedImage generateQRImage() throws IOException,InvalidQRConfigException,QREncoderException, ImageSizeException {
        
    	Date t1 = new Date();
        // Let us get the image of QR Code.
        BufferedImage imageWithQR = myQrCode.toImage();
        long r1 = new Date().getTime()-t1.getTime();
        //System.out.println("Core Encoding Resp Time:"+r1+"ms");
        Date t2 = new Date();
        // Let us create Empty Canvas to put the QR Image in Center with Margin.
        // Calculate the Canvis size, it will be QRImage + margin;
        int margin = qrConfig.getMargin();
        infoLog("Margin ratio be added in QR Image:"+margin);
        // Add margin to the output image size.
        int resultImageSize = imageWithQR.getWidth() + margin;
        infoLog("The output Image size will be:"+resultImageSize);
        // Calculate (x,y) location for placement of the actual QR image on canvas.
        int qrXY = (resultImageSize - imageWithQR.getWidth())/2;
        // Create blank canvas for output image.
        BufferedImage qrCodeOutputImage = new BufferedImage(resultImageSize, resultImageSize, BufferedImage.TYPE_INT_RGB);
        
        //Now ready to draw the data.
        Graphics2D g = QRCode.of(qrCodeOutputImage);
        // Fill the canvas with Background color defined in StyleConfig.
        g.setColor(qrConfig.getStyleConfig().backgroundColor);
        
        g.fillRect(0,0,resultImageSize,resultImageSize);
        // Place the QRCode image to the calculated qrXY(x,y)
        g.drawImage(imageWithQR, qrXY, qrXY,null); 
        // Here the QR Output image is ready without Logo.
        g.dispose();
        // We shall insert the overly image logo based on QR Config.
        long r2 = new Date().getTime()-t1.getTime();
        //System.out.println("Putting QR on Canvas:"+r2+"ms");
        Date t3 = new Date();
        BufferedImage finalImage = null;
        if(this.qrConfig.isLogoRequired()){
            // 
        	finalImage = QRUtils.getOverlayImageURL(qrCodeOutputImage, qrConfig.getLogoImageURL(), qrConfig.getStyleConfig());
        }else{
            // Return QR Code without overly logo image.
        	finalImage =  qrCodeOutputImage;
        }
        long r3 = new Date().getTime()-t1.getTime();
        //System.out.println("Putting Logo on QR:"+r3+"ms");
        return finalImage;
    }
    public String getBase64EncodedImage(String imageFormat) throws IOException, QREncoderException, ImageSizeException, InvalidQRConfigException {
        BufferedImage image = generateQRImage();

        ByteArrayOutputStream binary = new ByteArrayOutputStream();
        ImageIO.write(image,imageFormat,binary);
        byte []content = binary.toByteArray();
        Base64.Encoder encoder = Base64.getEncoder();
        String encoded = getEncoder().encodeToString(content);
        return encoded;
    }
    public byte[] getBinaryImage(String imageFormat) throws IOException, QREncoderException, ImageSizeException, InvalidQRConfigException {
        BufferedImage image = generateQRImage();
        ByteArrayOutputStream binary = new ByteArrayOutputStream();
        ImageIO.write(image,imageFormat,binary);
        byte []content = binary.toByteArray();
        return content;

    }
    private void log(String msg){
    	//System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS+":-"+ msg);
    }
    public void infoLog(String msg) {
    	//System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS+":-"+ msg);
    }

}
