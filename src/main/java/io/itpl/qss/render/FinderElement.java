package io.itpl.qss.render;



import io.itpl.qss.exception.InvalidQRConfigException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * // Finder Patters are the Square blocks that exists on three corner of QR
 * code (Top-Left, Top-Right and Bottom-Left). QR scanner use them to detect the
 * oriantation of the QR Code. The Finder Patterns has a Size of 7 modules
 * irrespective of the QR version. they are buit as three components; Outer
 * block : 7 modules Innter block : 5 modules Finder ball : 3 modules The Finder
 * Pattern need to be rendered in ratio of 1:1:3:1:1 (each module column/row),
 * so that scnner can detect them.
 */
public class FinderElement {
    private int qr_version = 2;
    private int qr_size;
    private int finderLocation = -1; // 0 = Top-Left, 1 = Top-Right, 2 = Bottom-Left
    /**
     * One module may need more then 1 pixel depending on the qr size. scale
     * determine the no's of pixels in each module.
     */
    public float scale = 1f;
    /**
     * QR Code Finder Pattern Size is fixed as 7 module irrespective of verion of
     * the QR.
     */
    public static final int FINDER_SIZE = 7;
    public static final String ALIAS = "FinderElement";

    public int finderWidth = 7; // initilize with default size (scale 1:1)

    public int outerFrameX = 0;
    public int outerFrameY = 0;

    public int innerFrameX = 1;
    public int innerFrameY = 1;
    public int innerFrameWidth = 5;

    public int ballX = 2;
    public int ballY = 2;
    public int ballWidth = 3;

    private int finderShape;
    private StyleConfig style = StyleConfig.getDefaultStyleConfig();

    /**
     * 
     * @param _version version of the QR code being used
     * @param _size    Size of the QR Code
     * @param location Location of the Finder Pattern (i.e. 0 = Top-Left, 1 =
     *                 Top-Right, 2 = Bottom-Left)
     */
    public FinderElement(int _version, int _size, int location) {
        log("Creating FinderElement- Version:" + _version + ",Size:" + _size + ", Location:" + location);
        this.qr_version = _version;
        this.qr_size = _size;
        this.finderLocation = location;
        // Let us calculate no's of the QR modules for the given qr_version;
        int modules = ((qr_version - 1) * 4) + 21;
        // Round-up the size respective to the version and determine scale for modules.
        
        // Now we can determine the scale of module to pixels.
        // For example QR version 2 having 25 modules, So if qr_size = 50, then scale =
        // 50/25 = 2.
        this.scale = this.qr_size / modules;
        //log("QR Module+ "+modules+",Scale:" + scale);
        createFinder();

    }

    private void createFinder() {
        this.finderWidth = Math.round(this.finderWidth * scale);
        this.innerFrameWidth = Math.round(this.innerFrameWidth * scale);
        this.ballWidth = Math.round(this.ballWidth * scale);

        //log("createFinder():finderWidth-" + this.finderWidth);
        //log("createFinder():innerFrameWidth-" + this.innerFrameWidth);
        //log("createFinder():ballWidth-" + this.ballWidth);
        switch (this.finderLocation) {
        // Top-Left Finder Pattern
        case 0:
            this.finderShape = style.topLeftFinderShape;
            //log("Top-Left Shape:" + this.finderShape);
            //
            this.outerFrameX = 0;
            this.innerFrameX = Math.round(1 * scale);
            this.ballX = Math.round(2 * scale);

            this.outerFrameY = 0;
            this.innerFrameY = Math.round(1 * scale);
            this.ballY = Math.round(2 * scale);
            break;
        // Top-Right Finder Patterm
        case 1:
            this.finderShape = style.topRightFinderShape;
            //log("Top-Right Shape:" + this.finderShape);
            // The Formula to calculate X is : (((V-1)*4)+21) - 7
            // Wherein Y will remain same as Top-Left Block
            this.outerFrameX = (this.qr_size - this.finderWidth);
            this.innerFrameX = Math.round((this.outerFrameX + (1 * scale)));
            this.ballX = Math.round((this.innerFrameX +(1 * scale)));
            
            //this.outerFrameX = ((((this.qr_version - 1) * 4) + 21) - 7) * scale;
            //this.innerFrameX = (((((this.qr_version - 1) * 4) + 21) - 7) + 1) * scale;
            //this.ballX = (((((this.qr_version - 1) * 4) + 21) - 7) + 2) * scale;

            this.outerFrameY = 0;
            this.innerFrameY = Math.round(1 * scale);
            this.ballY = Math.round(2 * scale);
            log("Top-Right:("+this.outerFrameX+","+outerFrameY+")");
            break;
        // Bottom-Left
        case 2:
            this.finderShape = style.bottomLeftFinderShape;
            //log("Bottom-Left Shape:" + this.finderShape);
            // Swap X<->Y
            
            this.outerFrameX = 0;
            this.innerFrameX = Math.round(1 * scale);
            this.ballX = Math.round(2 * scale);
            
            this.outerFrameY = (this.qr_size - this.finderWidth);
            this.innerFrameY = Math.round((this.outerFrameY + (1 * scale)));
            this.ballY = Math.round((this.innerFrameY + (1 * scale)));
            
            
        default:
            // Do Nothing here
        }

    }

    public String toString() {
        StringBuffer data = new StringBuffer();
        data.append("Finer-Location:" + this.finderLocation + "\n");
        data.append("Size:" + this.qr_size + "\n");
        data.append("OuterFrame:("+this.outerFrameX+","+this.outerFrameY+",size@"+this.finderWidth+")\n");
        data.append("InnerFrame:("+this.innerFrameX+","+this.innerFrameY+",size@"+this.innerFrameWidth+")\n");
        data.append("Ball:("+this.ballX+","+this.ballY+",size@"+this.ballWidth+")\n");
        data.append("Finder Shape:"+this.finderShape + ", Ball Shape:"+this.style.finderBallShape);
        
        return data.toString();
    }

    public BufferedImage toImage() throws InvalidQRConfigException {

        //
        //log("Finder[" + this.finderLocation + "], Shape:" + this.finderShape);
        switch (this.finderShape) {
        case StyleConfig.FINDER_SQURE:
            return this.toSquareImage();

        case StyleConfig.FINDER_CIRCLE:
            return this.toCircleImage();

        case StyleConfig.FINDER_ROUNDED_SQUARE:
            return this.toRoundedCornerSquare();

        case StyleConfig.FINDER_HEXAGON:
            return this.toHexagoneImage();

        case StyleConfig.FINDER_OPP_ROUND_CORNERS:
            return this.toOppRoundCornerSquare();

        }
        throw new InvalidQRConfigException("Finder Style not found:" + finderShape);
    }

    public BufferedImage toSquareImage() {
        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.finderColor);
        g.fillRect(0, 0, this.finderWidth, this.finderWidth);
        int innerFrameMargin = (this.finderWidth - this.innerFrameWidth) / 2;
        int ballMargin = (this.finderWidth - this.ballWidth) / 2;
        g.setColor(style.backgroundColor);
        g.fillRect(innerFrameMargin, innerFrameMargin, this.innerFrameWidth, this.innerFrameWidth);
        g.setColor(style.finderBallColor);
        g.fillRect(ballMargin, ballMargin, this.ballWidth, this.ballWidth);
        return image;
    }

    public BufferedImage toRoundedCornerSquare() {
        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, finderWidth, finderWidth);
        g.setColor(style.finderColor);
        g.fillRoundRect(0, 0, this.finderWidth, this.finderWidth, this.finderWidth / 3, this.finderWidth / 3);
        
        int innerFrameMargin = (this.finderWidth - this.innerFrameWidth) / 2;
        int ballMargin = (this.finderWidth - this.ballWidth) / 2;
        
        g.setColor(style.backgroundColor);
        g.fillRoundRect(innerFrameMargin, innerFrameMargin, this.innerFrameWidth, this.innerFrameWidth,
                this.innerFrameWidth / 3, this.innerFrameWidth / 3);
        
                g.setColor(style.finderBallColor);
        //g.fillRoundRect(ballMargin, ballMargin, this.ballWidth, this.ballWidth, this.ballWidth / 3, this.ballWidth / 3);
        switch (this.style.finderBallShape) {
            case StyleConfig.BALL_DEFAULT:
                g.fillRoundRect(ballMargin, ballMargin, this.ballWidth, this.ballWidth, this.ballWidth / 3, this.ballWidth / 3);
                break;
            case StyleConfig.BALL_CIRCLE:
            g.fillOval(ballMargin, ballMargin, this.ballWidth, this.ballWidth);
                break;
        }
        return image;
    }

    public BufferedImage toOppRoundCornerSquare() {
        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, this.finderWidth, this.finderWidth);
        /* This Shape is made of 3 shapes
         1. Round Cornered Rect with full Width
         2. Filled Rect A with Half Width for Corner A
         3. Filled Rect A with Half Width for Corner A
         Position & Size of RectA & B will be same in Top-Right & Bottom-Left Finders
         whereas it will change in the Top-Left.
         */
        int innerRectAX,innerRectAY,innerRectBX, innerRectBY, innerRectWidth;
        // // Let us calculate the (x,y) & width for inner Rectangles first.
        innerRectWidth=this.finderWidth/2; // OuterFrame
        if(this.finderLocation != 0){
            innerRectAX = innerRectWidth;
            innerRectAY = 0;
            
            innerRectBX = 0;
            innerRectBY = innerRectWidth;
        }else{
            // This will be Top-Left Finder
            innerRectAX = 0;
            innerRectAY = 0;
            innerRectBX = innerRectWidth;
            innerRectBY = innerRectWidth;
        }

        /***
         *  Now we can start drawing the OuterFrame with DataColor (i.e. DarkModule)
         * */ 
        g.setColor(style.finderColor);
        // 1. Outer Round Rect with Full Finder Width
        g.fillRoundRect(0, 0, this.finderWidth, this.finderWidth, this.finderWidth / 2, this.finderWidth / 2);
        // 2. Overlap the Corner A with Rect A
        g.fillRect(innerRectAX, innerRectAY, innerRectWidth, innerRectWidth);//
        //g.fillRect(this.finderWidth / 2, 0, this.finderWidth / 2, this.finderWidth / 2);
        // 3. Overlap the Corner B with Rect B
        g.fillRect(innerRectBX, innerRectBY, innerRectWidth, innerRectWidth);//
        //g.fillRect(0, this.finderWidth / 2, this.finderWidth / 2, this.finderWidth / 2);//
        
        /***
         * OuterFrame finished, Now Inner Frame with Background Color (i.e. Background Color)
         **/
        // The width & respective (x,y) of innerFrame will change, let us calculate the (x,y) first as following.
        int innerFrameXY = (this.finderWidth - this.innerFrameWidth) / 2;
        // Also need to Reset the Rect A & B (x,y,width) based on innerFrameWidth.
        innerRectWidth=this.innerFrameWidth/2; // innerFrame
        if(this.finderLocation != 0){
            innerRectAX = innerFrameXY+ innerRectWidth;
            innerRectAY = innerFrameXY ; // This wont be '0' for the innerFrame
            innerRectBX = innerFrameXY;
            innerRectBY = innerFrameXY+ innerRectWidth;
        }else{
            // This will be Top-Left Finder
            innerRectAX = innerFrameXY;
            innerRectAY = innerFrameXY;
            innerRectBX = innerFrameXY + innerRectWidth;
            innerRectBY = innerFrameXY + innerRectWidth;
        }

        // 1. Outer Round Rect with Full InnerFrameWidth
        g.setColor(style.backgroundColor);
        // g.fillRect(innerFrameMargin, innerFrameMargin, this.innerFrameWidth,
        // this.innerFrameWidth);
        g.fillRoundRect(innerFrameXY, innerFrameXY, this.innerFrameWidth, this.innerFrameWidth,
                this.innerFrameWidth / 2, this.innerFrameWidth / 2);
        // 2. Overlap above Round Rect Corner with Rect A & B
        g.fillRect(innerRectAX, innerRectAY, innerRectWidth, innerRectWidth);//
        g.fillRect(innerRectBX, innerRectBY, innerRectWidth, innerRectWidth);//
        
        /***
         *  Drawing the Ball now with Data Color. 
         *  For now we will use the default shape for the ball and ignore the StyleConfig.finderBallShape
         ***/
        int ballXY = (this.finderWidth - this.ballWidth) / 2;
        innerRectWidth=this.ballWidth/2; // ball
        if(this.finderLocation != 0){
            innerRectAX = ballXY+ innerRectWidth;
            innerRectAY = ballXY ; // This wont be '0' for the innerFrame
            innerRectBX = ballXY;
            innerRectBY = ballXY+ innerRectWidth;
        }else{
            // This will be Top-Left Finder
            innerRectAX = ballXY;
            innerRectAY = ballXY;
            innerRectBX = ballXY + innerRectWidth;
            innerRectBY = ballXY + innerRectWidth;
        }
        g.setColor(style.finderBallColor);
        //1. Outer
        g.fillRoundRect(ballXY, ballXY, this.ballWidth, this.ballWidth, this.ballWidth / 2, this.ballWidth / 2);
        //2. Corner A
        g.fillRect(innerRectAX, innerRectAY, innerRectWidth, innerRectWidth);//
        //3. Corner B
        g.fillRect(innerRectBX, innerRectBY, innerRectWidth, innerRectWidth);//
        //Finish!
        g.dispose();
        return image;
    }

    public BufferedImage toCircleImage() {
        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, finderWidth, finderWidth);
        g.setColor(style.finderColor);
        g.fillOval(0, 0, this.finderWidth, this.finderWidth);
        int innerFrameMargin = (this.finderWidth - this.innerFrameWidth) / 2;
        int ballMargin = (this.finderWidth - this.ballWidth) / 2;
        g.setColor(style.backgroundColor);
        g.fillOval(innerFrameMargin, innerFrameMargin, this.innerFrameWidth, this.innerFrameWidth);
        g.setColor(style.finderBallColor);
        g.fillOval(ballMargin, ballMargin, this.ballWidth, this.ballWidth);
        g.dispose();
        return image;
    }

    public BufferedImage toRightCircleImage() {
        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, finderWidth, finderWidth);
        g.setColor(style.finderColor);
        g.fillOval(0, 0, this.finderWidth, this.finderWidth);
        g.fillRect(this.finderWidth / 2, 0, this.finderWidth / 2, this.finderWidth);

        int innerFrameMargin = (this.finderWidth - this.innerFrameWidth) / 2;
        int ballMargin = (this.finderWidth - this.ballWidth) / 2;
        g.setColor(style.backgroundColor);
        g.fillOval(innerFrameMargin, innerFrameMargin, this.innerFrameWidth, this.innerFrameWidth);
        g.fillRect(innerFrameMargin + this.innerFrameWidth / 2, innerFrameMargin, this.innerFrameWidth / 2,
                this.innerFrameWidth);
        g.setColor(style.finderBallColor);
        g.fillOval(ballMargin, ballMargin, this.ballWidth, this.ballWidth);
        g.fillRect(ballMargin + ballWidth / 2, ballMargin, this.ballWidth / 2, this.ballWidth);

        return image;
    }

    

    public BufferedImage toHexagoneImage() {
        Polygon outer = this.toPolygon(0);
        Polygon inner = this.toPolygon(1);
        Polygon ball = this.toPolygon(2);

        BufferedImage image = new BufferedImage(this.finderWidth, this.finderWidth, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = QRCode.of(image);
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, finderWidth, finderWidth);
        g.setColor(style.finderColor);
        g.fillPolygon(outer);
        g.setColor(style.backgroundColor);
        g.fillPolygon(inner);
        g.setColor(style.finderBallColor);
        g.fillPolygon(ball);
        return image;
    }

    /**
     * Rendering the QR Code data must not overlap the Functional Patterns of the QR
     * Code. This method helps to determine for if provided location (x,y) is part
     * of the FinderElement or not.
     * 
     * @param x position 'x' of the QR data module. (from the encoded byte[][] data)
     * @param y position 'y' of the QR data module. (from the encoded byte[][] data)
     * @return true if the provided (x,y) are part of the FinderElement, else return
     *         False.
     */
    public boolean contains(int x, int y) {
        boolean result = false;
        // This msg is used for debug only. it has no role in core logic.
        StringBuffer msg = new StringBuffer();
        msg.append("Contains-(" + x + "," + y + ") In FinderElement[" + this.finderLocation + "]");
        // The position (OuterFrameX, outerFrameY) of each FinerElement is different.
        // Range for the FinderElement Body will be different respective to the
        // finderLocation.
        switch (this.finderLocation) {
        case 0:
            // Top-Left FinderElement, wherein (x,y) will be always = (0,0)
            // Body Range is upto FinderWidth.
            if (x < this.finderWidth && y < this.finderWidth) {
                result = true;
            }
            break;
        case 1:
            // Top-Right FinderElement wherein outerFrameY=0 whereas X
            // Top-Left Corner Position = outerFrameX, outerFrameY
            // Top-Right Corner Position = outerFrameX + finderWidth, 0
            // Bottom-Left Corner Position = OuterFrameX, OuterFrameY + finderWidth
            if (x >= this.outerFrameX && y < this.finderWidth) { // outerFrameY will be always = 0,
                result = true;
            }
            break;
        case 2:
            // Bottom-Left FinderElement wherein OuterFrameX will be always 0.
            // Top-Left Corner Position = 0, OuterFrameY
            // Top-Right Corner Position = finderWidth, outerFrameY
            // Bottom-Left Corner Position = 0, (OuterFrameY + finderWidth)
            // Bottome-Right Corner Position = [finderWidth, (OuterFrameY + finderWidth)
            if (y >= this.outerFrameY && x < this.finderWidth) {
                result = true;
            }
            break;
        }
        msg.append("==>Result: " + result);
        // log(msg.toString());
        return result;
    }

    private Polygon toPolygon(int type) {
        int x = 0, y = 0, width = 0;
        int innerFrameMargin = (this.finderWidth - this.innerFrameWidth) / 2;
        int ballMargin = (this.finderWidth - this.ballWidth) / 2;
        String info = "Default";
        switch (type) {
        case 0:
            x = 0;
            y = 0;
            width = finderWidth;
            info = "Outer-Frame";
            break;
        case 1:
            x = innerFrameMargin;
            y = innerFrameMargin;
            width = innerFrameWidth;
            info = "Inner-Frame";
            break;
        case 2:
            x = ballMargin;
            y = ballMargin;
            width = ballWidth;
            info = "Finder-Ball";
        }
        Hexagon hex = new Hexagon(x, y, width, info);
        log(hex.getInfo());
        hex.setFaceCount(this.style.numberOfFaces);
        return hex.getHexagon();
    }

    public void setStyle(StyleConfig style) {
        this.style = style;
        switch (this.finderLocation) {
        // Top-Left Finder Pattern
        case 0:
            this.finderShape = style.topLeftFinderShape;
            //log("Top-Left Shape:" + this.finderShape);

            break;
        // Top-Right Finder Patterm
        case 1:
            this.finderShape = style.topRightFinderShape;
            //log("Top-Right Shape:" + this.finderShape);
            break;
        // Bottom-Left
        case 2:
            this.finderShape = style.bottomLeftFinderShape;
            //log("Bottom-Left Shape:" + this.finderShape);
            break;
        default:
            // Do Nothing here
        }
        // this.createFinder();
    }
   

    public StyleConfig getStyleConfig() {
        return this.style;
    }

    private void log(String msg) {
        //System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS + ":-" + msg);
    }

    public static void test(String args[]) {
        if (args.length != 5) {
            System.out.println("Usage com.iwant.qr.render.FinderElement <version> <size> <location> <x> <y>");
            System.exit(0);
        }
        int version = 2;
        int size = 250;
        int location = 0;
        int x = 0;
        int y = 0;

        version = Integer.parseInt(args[0]);
        size = Integer.parseInt(args[1]);
        location = Integer.parseInt(args[2]);
        x = Integer.parseInt(args[3]);
        y = Integer.parseInt(args[4]);

        FinderElement finder = new FinderElement(version, size, location);
        System.out.println(finder);
        System.out.println(finder.contains(x, y));
    }
}
