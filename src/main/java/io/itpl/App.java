package io.itpl;

import io.itpl.qss.client.QRConfig;
import io.itpl.qss.client.QRGenerator;
import io.itpl.qss.exception.ImageSizeException;
import io.itpl.qss.exception.InvalidQRConfigException;
import io.itpl.qss.exception.QREncoderException;
import io.itpl.qss.render.StyleConfig;
import java.awt.*;
import java.io.IOException;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws QREncoderException, IOException, InvalidQRConfigException, ImageSizeException {
        String data = "www.google.com";
        String logo = "https://picsum.photos/id/1/20/20";
        int size = QRConfig.SIZE_SMALL;
        QRConfig config = QRConfig.from(data,size,logo);
        StyleConfig qss = StyleConfig.getDefaultStyleConfig();
        qss.topLeftFinderShape = StyleConfig.FINDER_CIRCLE;
        qss.dataColorPrimary = Color.red;
        qss.finderColor = Color.red;
        qss.dataBlockShape = StyleConfig.DATA_SHAPE_SQUARE;
        qss.dataColorMode = 0;
        config.setStyleConfig(qss);
        QRGenerator generator = new QRGenerator(config);
        String encoded = generator.getBase64EncodedImage("png");
        System.out.println(encoded);
    }
}
