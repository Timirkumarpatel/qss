package io.itpl.qss.render;

import java.awt.*;

public class StyleConfig {
    /**
     * QR Code background color. This refers to the color of the Light module of QR Data.
     */
    public Color backgroundColor;
    /**
     * Color to render the QR Code Data, or in other words Dark Modules.
     */
    public Color dataColorPrimary;
    /**
     *  dataColorSecondary is being used only when dataColorMode is DATA_COLOR_MULTICOLOR.
     */
    public Color dataColorSecondary;
    /**
     * Usually data modules will be rendered using one Color - dataColorPrimry.
     * We can change rendering to use additional color wherein data modules will be rendered in alternative mode (odd-even formulat).
     * User DATA_COLOR_MULTICOLOR for multi color mode. 
     */
    public int dataColorMode;
    /**
     * Standard, single color data module rendering using dataColorPrimary.
     */
    public static final int DATA_COLOR_STANDARD = 0;
    /**
     * To render the Data Modules in alternative colors of dataColorPrimary and dataColorSecondary based on odd-even position.
     */
    public static final int DATA_COLOR_MULTICOLOR = 1;
    /**
     * In case the logo is added as overly of the QR Image, the Logo Background can be differenciated with different background to meet the branding needs.
     * 
     */
    public Color logoBackgroundColor;
    /**
     * Logo background shape can be made optional by setting this to false. If it is set to true, then logoBackground shape will be rendered. 
     */
    public Color finderColor;
    /**
     * finderBallColor can be used to modify th Finder Ball Color.
     */
    public Color finderBallColor;
    /**
     * You can enable/disable the logo background frame. by default it is true.
     */
    public boolean bDrawLogoBackground = true;
    /**
     * Finder style to draw them in standard SQURE module.
     */
    public final static int FINDER_SQURE = 0;
    /**
     * Finder style to draw them in SQURE module with rounded cornders.
     */
    public final static int FINDER_ROUNDED_SQUARE = 1;
    /**
     * Finder style to draw them in Circle shape.
     */
    public final static int FINDER_CIRCLE = 2;
    /**
     * Finder style to draw them in Hexagone shape.
     */
    public final static int FINDER_HEXAGON = 3;
    /**
     * No's of faces to be counted while drawing the custom hexagone shape.
     * */
    public static int numberOfFaces = 6;
    /**
     * Finder style to draw Squre wherein two opposite corners are Curved and Other two are Standard.
     */
    public final static int FINDER_OPP_ROUND_CORNERS = 4;
    /**
     * Shape of the finder ball wherein DEFAULT shape is same as Outer Frame shape.
     * in order to maintain ratio of 1:1:3:1:1 there are limited options available for ball shape. 
     * Although, you can have CIRCLE shape addition to the Default shape.
     */
    public int finderBallShape;
    /**
     * BALL_DEFAULT means ball shape will be same as Outer Frame Shape.
     */
    public final static int BALL_DEFAULT = 0;
    /**
     * Draw Ball in CIRCLE Shape.
     */
    public final static int BALL_CIRCLE = 1;
    /**
     * Draw data in stndard square modules.
     */
    public final static int DATA_SHAPE_SQUARE = 0;
    /**
     *  Draw module in Circle Shape.
     */
    public final static int DATA_SHAPE_CIRCLE = 1;
    /**
     *  Draw data in eclipse shape.
     */
    public final static int DATA_SHAPE_OVAL = 2;
    /**
     * Merge the Continuous dark modules into vertical Bar having rounded ends.
     * */
    public final static int DATA_SHAPE_ROUNDED_BARS = 3;
    /**
     * Merge the Continuous dark modules into irregular area having rounded ends and corners.
     * */
    public final static int DATA_SHAPE_ROUNDED_LINEAR = 4;
    /**
     * Merge the Continuous dark modules into irregular area having diamond ends and rounded corners.
     * */
    public final static int DATA_SHAPE_DIAMOND_LINEAR = 5;
    /**
     * To change the Top-Left Finder Shape. liner 
     */
    public int topLeftFinderShape;
    /**
     * To change the Top-Right Finder Shape.
     */
    public int topRightFinderShape;
    /**
     * To change the Bottom-Left Finder Shape.
     */
    public int bottomLeftFinderShape;
    /**
     * To change the data module Shape.
     */
    public int dataBlockShape;
    /**
     *  Not used.
     */
    public float logoBkgColorTransparency = 0.5f;

    private StyleConfig() {
        
    }
    /**
     * Create a default instance of stylesheet with default colors and rendering style.
     * The Obtained instance can be used further for customizations in style.
     * @return StyleConfig
     */
    public static StyleConfig getDefaultStyleConfig(){

        StyleConfig defaultStyle = new StyleConfig();
        defaultStyle.backgroundColor = Color.WHITE;
        
        defaultStyle.dataColorPrimary = Color.BLACK;
        defaultStyle.dataColorSecondary = Color.DARK_GRAY;
        defaultStyle.dataColorMode = StyleConfig.DATA_COLOR_MULTICOLOR;
        defaultStyle.dataBlockShape = StyleConfig.DATA_SHAPE_ROUNDED_BARS;
        
        defaultStyle.finderColor = defaultStyle.dataColorPrimary;
        defaultStyle.topLeftFinderShape = StyleConfig.FINDER_ROUNDED_SQUARE;
        defaultStyle.topRightFinderShape = StyleConfig.FINDER_ROUNDED_SQUARE;
        defaultStyle.bottomLeftFinderShape = StyleConfig.FINDER_ROUNDED_SQUARE;

        defaultStyle.finderBallColor = defaultStyle.dataColorPrimary;
        defaultStyle.finderBallShape = StyleConfig.BALL_DEFAULT;

        defaultStyle.bDrawLogoBackground = true;
        defaultStyle.logoBkgColorTransparency = 0.5f;
        defaultStyle.logoBackgroundColor = new Color(165, 197, 100, 125);
        
        

        return defaultStyle;
    }

}
