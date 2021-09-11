package io.itpl.qss.utils;



import io.itpl.qss.render.QRCode;
import io.itpl.qss.render.StyleConfig;
import io.itpl.qss.exception.ImageSizeException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class QRUtils {
	private static final String ALIAS = "QRUtils";
    public static BufferedImage getImageFromURL(String url) throws IOException {
        URL imageURL = new URL(url);
        return ImageIO.read(imageURL);
    }

    public static BufferedImage getImageFromFile(String path) throws IOException {
        File imageFile = new File(path);

        return ImageIO.read(imageFile);

    }


    public static String getCurrentTimeStamp() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    	return formatter.format(new Date());
    }

    public static BufferedImage getOverlayImageURL(BufferedImage qrImage, String url, StyleConfig styleConfig) throws IOException,ImageSizeException {
    	Date t1 = new Date();
        BufferedImage overly = getImageFromURL(url);
        long r1 = new Date().getTime()-t1.getTime();
        return getOverlayImage(qrImage,overly,styleConfig);

    }
    public static BufferedImage getOverlayImage(BufferedImage qrImage, String url,StyleConfig styleConfig) throws IOException,ImageSizeException {
        BufferedImage overly = getImageFromFile(url);
        return getOverlayImage(qrImage,overly,styleConfig);

    }
    public static BufferedImage getOverlayImage(BufferedImage qrImage, BufferedImage overly,StyleConfig styleConfig) throws IOException,ImageSizeException {
        // Load logo image
        // Check the size
    	
        int width = overly.getWidth();
        int height = overly.getHeight();
        //if (width != height)
        //    throw new ImageSizeException("Image must be with 1:1 aspect ratio");

        int requiredLogoImageHeight = qrImage.getHeight()/5;

        // For Example Logo Image Height is 620 & Requied Size is 250
        // So, delta can be calculated as 620 - 250 = 370
        // now delta, i.e 370/620 is a scale value to resize.i.e. 0.60 (60%)
        // calculated new height will be 620 * 0.60
        // same way, in order to maintain same aspect ratio, we will calculate width as well.
        int delta = Math.abs(height - requiredLogoImageHeight);

        float resizeScale = ((float)requiredLogoImageHeight / (float)height);

        if(resizeScale > 1) {
        	resizeScale = 2;
        }
        requiredLogoImageHeight = Math.round(height * resizeScale);
        int requiredLogoWidth = Math.round(width * resizeScale);


        //overly = resize(overly, logoImageWidth, logoImageWidth);
        overly = resize(overly, requiredLogoWidth, requiredLogoImageHeight);

        // Calculate the delta height and width between QR code and logo
        int deltaHeight = qrImage.getHeight() - overly.getHeight();
        int deltaWidth = qrImage.getWidth() - overly.getWidth();

        // Initialize combined image
        BufferedImage combined = new BufferedImage(qrImage.getHeight(), qrImage.getWidth(),
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(combined);//(Graphics2D) combined.getGraphics();

        // Write QR code to new image at position 0/0
        g.drawImage(qrImage, 0, 0, null);


        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        int deltaBoxW = Math.round(overly.getWidth()*0.2f);
        int deltaBoxY = Math.round(overly.getHeight()*0.2f);
        int logoCircleWidth = Math.round(overly.getWidth()) + Math.round(overly.getWidth()*0.2f) ; //20% margin
        int logoCircleHeight = Math.round(overly.getHeight()) + Math.round(overly.getHeight()*0.2f) ; //20% margin
        
        // Write logo into combine image at position (deltaWidth / 2) and
        // (deltaHeight / 2). Background: Left/Right and Top/Bottom must be
        // the same space for the logo to be centered
        int imgX = (int) Math.round(deltaWidth / 2), imgY = (int) Math.round(deltaHeight / 2);
        int bkgX = imgX - deltaBoxW/2;
        int bkgY = imgY - deltaBoxY/2;
        //g.drawImage(overly, (int) Math.round(deltaWidth / 2), (int) Math.round(deltaHeight / 2), null);
        if(styleConfig.bDrawLogoBackground){
            g.setColor(styleConfig.logoBackgroundColor);
            g.fillRoundRect(bkgX, bkgY, logoCircleWidth, logoCircleHeight,logoCircleWidth/2,logoCircleHeight/2);
        }
        

        g.drawImage(overly, imgX, imgY, null);
        g.dispose();
        return combined;

    }
    public static BufferedImage getOverlayImage(BufferedImage qrImage, String path) throws IOException, ImageSizeException {
        return getOverlayImage(qrImage, path,StyleConfig.getDefaultStyleConfig());

    }

    public static StyleConfig getItplStyle(){
        Color dataPrimary = new Color(61,128,71);//61,128,71
        Color logobackground = new Color(61,128,71,200);
        Color background = new Color(203,211,50,255);
        Color finderColor = new Color (128,173,60);
        Color ballColor = dataPrimary;

        
        StyleConfig styleConfig = StyleConfig.getDefaultStyleConfig();
        styleConfig.dataColorPrimary = dataPrimary;
        styleConfig.finderColor = finderColor;
        styleConfig.finderBallColor = ballColor;
        styleConfig.dataColorSecondary = finderColor;
        styleConfig.dataColorMode = StyleConfig.DATA_COLOR_MULTICOLOR;
        styleConfig.dataBlockShape = StyleConfig.DATA_SHAPE_ROUNDED_BARS;
        styleConfig.backgroundColor = new Color(1f,1f,1f,.9f);//Color.WHITE;
        styleConfig.logoBackgroundColor = logobackground;
        styleConfig.topLeftFinderShape = StyleConfig.FINDER_OPP_ROUND_CORNERS;
        styleConfig.topRightFinderShape = StyleConfig.FINDER_OPP_ROUND_CORNERS;
        styleConfig.bottomLeftFinderShape = StyleConfig.FINDER_CIRCLE;
        styleConfig.finderBallShape = StyleConfig.BALL_CIRCLE;
        return styleConfig;
    }

    public static String readBinaryFile(String path)throws IOException{
        File file = new File(path);
        
        RandomAccessFile fileReader = new RandomAccessFile(file, "r");
        int fileSize = (int)fileReader.length();
        byte [] data = new byte[fileSize] ;
        int block = fileReader.read(data, 0, fileSize);
        log("Total "+block +" read successfully from the file "+path);
        return new String(data);
        
    }
    
    private static void log(String msg) {
    	//System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS+":-"+msg);
    }

    private static BufferedImage resize(BufferedImage image, int width, int height) {
		int type = image.getType() == 0? BufferedImage.TYPE_INT_ARGB : image.getType();
		Image newImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(newImage, 0, 0, width, height, null);
		g.dispose();
		return resizedImage;
	}

}
