package io.itpl.qss.render;


import io.itpl.qss.encoder.QREncoder;
import io.itpl.qss.exception.InvalidQRConfigException;
import io.itpl.qss.exception.QREncoderException;
import io.itpl.qss.utils.QRUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/***
 * This Class represent the Logical model of the QR code and rendering of the QR Code image.
 * 
 * */
public class QRCode {
    private FinderElement topLeft, topRight, bottomLeft;
    private String data;
    private int _version = 2, _size = 25;
    private byte[][] encodedData;
    private float module = 1f;
    private StyleConfig style;
    private final static String alias = "QRCode";
    
    private static final int BLOCK_ISOLATED = 0;
    private static final int BLOCK_MID = 1;
    private static final int BLOCK_BOTTOM_END = 2;
    private static final int BLOCK_TOP_END = 3;
    private static final int BLOCK_LEFT_END = 4;
    private static final int BLOCK_RIGHT_END = 5;
    private static final int BLOCK_CORNER_TOP_LEFT = 6;
    private static final int BLOCK_CORNER_TOP_RIGHT = 7;
    private static final int BLOCK_CORNER_BOTTOM_LEFT = 8;
    private static final int BLOCK_CORNER_BOTTOM_RIGHT = 9;
    private static final int BLOCK_LOCATION_UNKNOWN = 10;
    
    
    public QRCode(String data, int version, int size) throws QREncoderException {
        this(data, version, size,0, StyleConfig.getDefaultStyleConfig());

    }
    /**
     * 
     * @param data Text Data which need to be encoded in QR Code.
     * @param version Version of the QR Code to be used.
     * @param size Size of the QR Code.
     * @param errorLevel level of Error Correction in QR Code.
     * @param style StyleConfig to define the Visual appearance of the QR Code.
     * @throws QREncoderException in case of mistatch of given argument data.
     */
    public QRCode(String data, int version, int size,int errorLevel, StyleConfig style) throws QREncoderException {
        this.data = data;
        this._version = version;
        this._size = size;
        this.style = style;
        topLeft = new FinderElement(_version, _size, 0);
        topRight = new FinderElement(_version, _size, 1);
        bottomLeft = new FinderElement(_version, _size, 2);
        setStyle(style);
        module = topLeft.scale;
        QREncoder encoder = new QREncoder(_version, _size,errorLevel);
        log("QR Encoder with ver( "+_version+") is Encoding the data:["+data.length()+"]-"+data);
        boolean bSuccess = encoder.encodeQRData(data);
        if (bSuccess) {
            encodedData = encoder.getEncodedQRMetrix();
            log("encoded-data-length:" + encodedData.length);
        } else {
            throw new QREncoderException("Invalid Data or Length of the Data");
        }
    }
    /**
     * @return String representation of QRCode properties.
     * */
    public String toString() {
        StringBuffer data = new StringBuffer();
        data.append("QRCOde:====================\n");
        data.append("version:" + this._version + "\n");
        data.append("size:" + this._size + "\n");
        data.append("QRCOde:_____________________\n");

        return data.toString();
    }
    /**
     * @param style StyleConfig object to which will be replaced with current style.
     * */
    public void setStyle(StyleConfig style) {
        log("Style Updated in QRCode:" + style.topLeftFinderShape + ":" + style.topRightFinderShape + ":"
                + style.bottomLeftFinderShape);
        this.style = style;
        topLeft.setStyle(style);
        topRight.setStyle(style);
        bottomLeft.setStyle(style);
    }
    /**
     * @return StyleConfig property of the QRCode object.
     * */

    public StyleConfig getStyleConfig() {
        return this.style;
    }

    private boolean isFunctionalArea(int x, int y) {
        boolean bTopLeft = topLeft.contains(x, y);
        if (bTopLeft) {
            //log("(" + x + "," + y + ") is Part of Top-Left Finder");
            return true;
        }
        boolean bTopRight = topRight.contains(x, y);
        if (bTopRight) {
            //log("(" + x + "," + y + ") is Part of Top-Right Finder");
            return true;
        }
        boolean bBottomLeft = bottomLeft.contains(x, y);
        if (bBottomLeft) {
            //log("(" + x + "," + y + ") is Part of Bottom-Left Finder");
            return true;
        }
        return false;
    }
    /**
     * Render the QRCode to the Image and return it as a BufferedImage.
     * @return BufferedImage of the QRCode. The image will contain only QRCode, without any logo image or margins.
     * @throws InvalidQRConfigException in case failure of QR Data, version and scale.
     * */

    
    public static Graphics2D of(BufferedImage source) {
    	double scale = 1.0; 
    	Graphics2D g = source.createGraphics();
    	AffineTransform transform = g.getTransform();
        log("transformX:"+transform.getScaleX());
        log("transformY:"+transform.getScaleY());
        transform.setToScale(scale, scale);
        
        g.setTransform(transform);
        return g;
    }
    public BufferedImage toImage() throws InvalidQRConfigException {
        log("QRCode-style.dataColorMode:"+this.style.dataColorMode + ", qrImage Size:"+_size);
        // Create empty Image Object with QR Dimension (Here the dimension will not have any margins)
        BufferedImage qrImage = new BufferedImage(_size, _size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = of(qrImage);
        
        g.setColor(style.backgroundColor);
        g.fillRect(0, 0, _size, _size);
        // Let us get the Finder Patter blockes first.
        BufferedImage imgTL = topLeft.toImage();
        BufferedImage imgTR = topRight.toImage();
        BufferedImage imgBL = bottomLeft.toImage();
        // Let us add the FinderBlocks to QR Code Image.
        //log(topLeft.toString());
        //log(topRight.toString());
        g.drawImage(imgTL, topLeft.outerFrameX, topLeft.outerFrameY, null);
        g.drawImage(imgTR, topRight.outerFrameX, topRight.outerFrameY, null);
        g.drawImage(imgBL, bottomLeft.outerFrameX, bottomLeft.outerFrameY, null);
        log("Finished rendering finder patters @scale: "+topLeft.scale +" Finder("+topLeft.outerFrameX+","+topLeft.outerFrameY+","+ imgTL.getWidth() +"),QRCanvas width: "+qrImage.getWidth());
        switch(this.style.dataBlockShape) {
	        case StyleConfig.DATA_SHAPE_ROUNDED_BARS:
	        	drawDataBlocks(g,0);
	        	break;
	        case StyleConfig.DATA_SHAPE_ROUNDED_LINEAR:
	        	drawEdgeWise(g,0);
	        	break;
	        case StyleConfig.DATA_SHAPE_DIAMOND_LINEAR:
	        	drawEdgeWise(g,1);
	        	break;
	    	default:
	    		drawDataBlocks(g);
        }
        g.dispose();
        log("Returning Image with QR Code(" + qrImage.getWidth() + " x " + qrImage.getHeight() + ")");
        return qrImage;

    }
    private void drawDataBlocks(Graphics2D g) {
    	int iWidth = this.encodedData.length;
        
        log("Drawing the QR Data now at scale: "+module + " with Color"+ this.getColorInfo(style.dataColorPrimary));
        g.setColor(style.dataColorPrimary);
        for (int i = 0; i < iWidth; i += module) {
            int currentDataBlock = 0;
            for (int j = 0; j < iWidth; j += module) {
                /***
                 * We need to skip the modules of functional patterns and render only data part.
                 * isFunctionlArea() method will verify and return true if the module is in Functional area.
                 ***/ 
                boolean bSkip = isFunctionalArea(i, j);
                // Dark modules having value = 1, whereas Light modules in White.
                if (this.encodedData[i][j] == 1) {

                    currentDataBlock++;
                    if (!bSkip) {
                        // log("draw-point:[(" + i + "," + j + "::" + module + ")");
                        // g.fillOval(i, j , module,module);
                        if(style.dataColorMode == StyleConfig.DATA_COLOR_MULTICOLOR){
                            // Here we works on odd-even module to switch the primary & secondary color.
                            if(currentDataBlock%2 == 0){
                                g.setColor(style.dataColorPrimary);
                            }else{
                                g.setColor(style.dataColorSecondary);
                            }
                        }
                        // Data Block Shape
                        switch (this.style.dataBlockShape) {
                            case StyleConfig.DATA_SHAPE_OVAL:
                                int start = Math.round(module / 3);
                                g.fillOval(i + start, j, Math.round(module - start), Math.round(module));
                                break;
                            case StyleConfig.DATA_SHAPE_SQUARE:
                                g.fillRect(i, j, Math.round(module), Math.round(module));
                                break;
                            case StyleConfig.DATA_SHAPE_CIRCLE:
                                g.fillOval(i, j, Math.round(module), Math.round(module));
                                break;
                        }

                    }
                }

            }
            // log("\n");
        }
    }
    
    private void drawDataBlocks(Graphics2D g, int pattern) {
    	int iWidth = this.encodedData.length;
        
        log("Drawing the QR Data now at scale: "+module + " with Color"+ this.getColorInfo(style.dataColorPrimary));
        g.setColor(style.dataColorPrimary);
        int i = 0;
        int colNumber = 0;
        //Loop for Columns
        while(i < iWidth) {
            int j = 0;
            
            if(style.dataColorMode == StyleConfig.DATA_COLOR_MULTICOLOR){
                // Here we works on odd-even module to switch the primary & secondary color.
                if(colNumber%2 == 0){
                    g.setColor(style.dataColorPrimary);
                }else{
                    g.setColor(style.dataColorSecondary);
                }
            }
            // Loop for Rows
            while (j < iWidth) {
                /***
                 * We need to skip the modules of functional patterns and render only data part.
                 * isFunctionlArea() method will verify and return true if the module is in Functional area.
                 ***/ 
                boolean bSkip = isFunctionalArea(i, j);
                // Dark modules having value = 1, whereas Light modules in White.
                int rowIncrement = Math.round(module);
                if (this.encodedData[i][j] == 1) {
                    if (!bSkip) {
                        // log("draw-point:[(" + i + "," + j + "::" + module + ")");
                        // g.fillOval(i, j , module,module);
                    	int rowHeight = Math.round(module);
                        int rowWidth = Math.round(module);
                        float widthRatio = 0.80f;
                        int unitMargin = 0;
                        int actualWidth = Math.round(rowWidth * widthRatio)-unitMargin;
                        int x = i;
                        int y = j;
                        //Now need to calculate the Row height until the next light module within a Data Area
                        boolean bNext = true;
                        int nextRowNumber = j + Math.round(module);
                        while(bNext && nextRowNumber < iWidth) {
                        	
                        	if (this.encodedData[i][nextRowNumber] == 1) {
                        		nextRowNumber += Math.round(module);
                        		rowHeight += Math.round(module);
                        		rowIncrement += Math.round(module);
                        		bNext = true;
                        	}else {
                        		bNext = false;
                        	}               	
                        }
                    	g.fillRoundRect(x, y, actualWidth, rowHeight, rowWidth,rowWidth);

                    }
                }
                j+=rowIncrement;
            }
            i+=module;
            colNumber++;
        }	
    }
    private void drawEdgeWise(Graphics2D g,int endType) {
    	int iWidth = this.encodedData.length;
        
        log("Drawing the QR Data now at scale: "+module + " with Color"+ this.getColorInfo(style.dataColorPrimary));
        g.setColor(style.dataColorPrimary);
        int size = Math.round(module);
        for (int i = 0; i < iWidth; i += module) {
            int currentDataBlock = 0;
            for (int j = 0; j < iWidth; j += module) {
                /***
                 * We need to skip the modules of functional patterns and render only data part.
                 * isFunctionlArea() method will verify and return true if the module is in Functional area.
                 ***/ 
                boolean bSkip = isFunctionalArea(i, j);
                // Dark modules having value = 1, whereas Light modules in White.
                if (this.encodedData[i][j] == 1) {

                    currentDataBlock++;
                    if (!bSkip) {
                        
                        // Detect the Edge Location
                        g.setColor(style.dataColorPrimary);
                        int dataLocation = findDataBlockLocation(i,j);//detectEdgeType(i,j);
                        if(dataLocation==-1) {
                        	//log("Edge Detection Failed:-1:("+i+","+j+")@"+module+"["+encodedData.length+"]");
                        	continue;
                        }
                        switch (dataLocation) {
                            case QRCode.BLOCK_ISOLATED:
                            	if(endType==0)
                            		g.fillOval(i, j, Math.round(module), Math.round(module));
                            	else {
                            		Hexagon block = new Hexagon(i, j, size, "Data");
                                    block.setFaceCount(4);
                                    g.fillPolygon(block.getHexagon());
                            	}
                                break;
                            
                            case QRCode.BLOCK_CORNER_TOP_LEFT:
                            	g.fillArc(i, j,size, size, 90, 90);
                            	g.fillRect(i, (j+size/2),size, size/2);
                            	g.fillRect((i+size/2), j,size/2, size/2);
                            	break;
                            case QRCode.BLOCK_CORNER_TOP_RIGHT:
                            	g.fillArc(i, j,size, size, 0, 90);
                            	g.fillRect(i, j,size/2, size);
                            	g.fillRect((i+size/2), (j+size/2),size/2, size/2);
                            	break;
                            case QRCode.BLOCK_CORNER_BOTTOM_RIGHT:
                            	g.fillArc(i, j,size, size, 270, 90);
                            	g.fillRect(i, j, size/2,size);
                            	g.fillRect((i+size/2), j, size/2,size/2);
                            	break;
                            case QRCode.BLOCK_CORNER_BOTTOM_LEFT:
                            	g.fillArc(i, j,size, size, 180, 90);
                            	g.fillRect(i, j, size, size/2);
                            	g.fillRect((i+size/2), (j+size/2), size/2, size/2);
                            	break;
                            case QRCode.BLOCK_MID:
                            	g.fillRect(i, j, Math.round(module), Math.round(module));
                            	//g.setColor(this.style.backgroundColor);
                            	//g.drawRect(i, j, size, size);
                                break;
                            
                            case QRCode.BLOCK_LEFT_END:
                            	g.fillRect(i+(size/2), j, (size/2), size);
                                //g.fillArc(i, j, Math.round(module), Math.round(module),90,180);
                            	if(endType==0)
                            		g.fillOval(i, j, size*2, size);
                            	else {
                            		Hexagon leftEnd = new Hexagon(i, j, size, "Left-End");
                                    leftEnd.setFaceCount(4);
                                    g.fillPolygon(leftEnd.getHexagon());
                            		
                            	}
                            	
                                break;
                            case QRCode.BLOCK_RIGHT_END:
                            	g.fillRect(i, j, (size/2), size);
                                //g.fillArc(i, j, Math.round(module), Math.round(module),270,180);
                            	//New
                            	if(endType==0) {
                            		g.fillOval(i-size, j, size*2, size);
                            	}else {
                            		Hexagon rightEnd = new Hexagon(i, j, size, "Right-End");
                                    rightEnd.setFaceCount(4);
                                    g.fillPolygon(rightEnd.getHexagon());
                            		
                            	}
                                //g.fillOval(i-size, j, size*2, size);
                            	
                                break;
                            case QRCode.BLOCK_TOP_END:
                            	g.fillRect(i, j+(size/2), size, (size/2));
                            	if(endType==0)
                            		g.fillArc(i, j, Math.round(module), Math.round(module),0,180);
                            	else {
	                                Hexagon hex = new Hexagon(i, j, size, "Top-End");
	                                hex.setFaceCount(4);
	                                g.fillPolygon(hex.getHexagon());
                            	}
                                break;
                            case QRCode.BLOCK_BOTTOM_END:
                            	g.fillRect(i, j, size, (size/2));
                            	if(endType==0)
                            		g.fillArc(i, j, Math.round(module), Math.round(module),180,180);
                            	else {
	                                Hexagon end = new Hexagon(i, j, size, "Bottom-End");
	                                end.setFaceCount(4);
	                                g.fillPolygon(end.getHexagon());
                            	}
                                break;
                            case QRCode.BLOCK_LOCATION_UNKNOWN:
                            	g.setColor(Color.RED);
                            	g.fillOval(i, j, Math.round(module), Math.round(module));
                            	g.setColor(this.style.dataColorPrimary);
                            	
                            	
                        }

                    }
                }

            }
            // log("\n");
        }
    }
    private void printData() {
    	StringBuffer data = new StringBuffer();
    	for(int i=0;i<this.encodedData.length;i+=module) {
    		data.append(encodedData[i][0]+",");
    	}
    	data.append("\n");
    	for(int i=0;i<this.encodedData.length;i+=module) {
    		for(int j=1;j<encodedData.length;j+=module) {
    			data.append(encodedData[i][j]+",");
    		}
    		data.append("\n");
    	}
    	
    	save(data.toString());
    }
    private int findDataBlockLocation(int i,int j) {
    	int nType = -1;
    	int rowUp = -1 ;
    	int rowDown = -1;
    	int colPrev = -1;
    	int colNext = -1;
    	int size = Math.round(module);
    	
    	
    	boolean hasPrevColumn = (i>0);
    	boolean hasNextColumn = (i<encodedData.length-size);
    	boolean hasPrevRow = (j>0);
    	boolean hasNextRow = (j<encodedData.length-size);
    	
    	int prevRowIndex = j-size;
		int nextRowIndex = j + size;
		int prevColIndex = i-size;
		int nextColIndex = i + size;
		
    	// Let us do it for the First Column
    	if(!hasPrevColumn) {
    		if(hasPrevRow && hasNextRow) {
    			// 1.1 Top End -> Possible
    			// 1.2 Bottom End -> Possible
    			// 1.3 Left End -> Possible
    			// 1.4 Right End -> Not Possible
    			// 1.5 Mid Blocks -> Possible
    			// 1.6 Isolated Blocks -> Possible
    			// 1.7 Top Left Corner -> Possible
    			// 1.8 Bottom Left Corner -> Possible
    			// 1.9 Top Right Corner -> Not Possible
    			// 1.10 Bottom Right Corner -> Not Possible
    			
    			rowUp = this.encodedData[i][prevRowIndex] ;
            	rowDown = this.encodedData[i][nextRowIndex];
            	colNext = this.encodedData[nextColIndex][j];
            	// Case 1.1 -> Top End
    			//	|0 0|
    			//	|1 0|
    			//	|1 0|
    			if(rowUp == 0 && rowDown == 1 && colNext == 0) {
    				return QRCode.BLOCK_TOP_END;
    			}
    			// Case 1.2 -> Bottom End
    			//	|1 0|
    			//	|1 0|
    			//	|0 0|
    			if(rowUp == 1 && rowDown == 0 && colNext == 0) {
    				return QRCode.BLOCK_BOTTOM_END;
    			}
    			// Case 1.3 -> Left End
    			//	|0 0|
    			//	|1 1|
    			//	|0 0|
    			if(rowUp == 0 && rowDown == 0 && colNext == 1) {
    				return QRCode.BLOCK_LEFT_END;
    			}
    			// Case 1.4 Right End -> Not Possible
    			// Case 1.5.1 -> Middle Blocks - Variant 1
    			// |1 1|
    			// |1 0|
    			// |1 0|
    			if(rowUp == 1 && rowDown == 1 && colNext == 0) {
    				return QRCode.BLOCK_MID;
    			}
    			// Case 1.5.2 -> Middle Blocks - Variant 2
    			// |1 1|
    			// |1 0|
    			// |1 0|
    			if(rowUp == 1 && rowDown == 1 && colNext == 1) {
    				return QRCode.BLOCK_MID;
    			}
    			
            	// Case 1.6 -> Isolated blocks
            	// 	|0	0|
				//	|1	0|
				//	|0	0|
    			if(rowUp == 0 && rowDown == 0 && colNext == 0) {
    				return QRCode.BLOCK_ISOLATED;
    			}
    			// Case 1.7 -> Top_Left_Corner
            	// 	|0	0|
				//	|1	1|
				//	|1	0|
    			if(rowUp == 0 && rowDown == 1 && colNext == 1) {
    				return QRCode.BLOCK_CORNER_TOP_LEFT;
    			}
    			// Case 1.8 -> Bottom left Corner
    			//	|1 0|
    			//	|1 1|
    			//	|0 0|
    			if(rowUp == 1 && rowDown == 0 && colNext == 1) {
    				return QRCode.BLOCK_CORNER_BOTTOM_LEFT;
    			}
            	// 1.9 Top Right Corner -> Not Possible
    			// 1.10 Bottom Right Corner -> Not Possible	
    		}
    	}
    	// Let us do it for the Last Column
    	if(!hasNextColumn) {
    		if(hasNextRow) {//hasPrevRow && hasNextRow) {
    			// 1.1 Top End -> Possible
    			// 1.2 Bottom End -> Possible
    			// 1.3 Left End -> Not Possible
    			// 1.4 Right End ->  Possible
    			// 1.5 Mid Blocks -> Possible
    			// 1.6 Isolated Blocks -> Possible
    			// 1.7 Top Left Corner -> Not Possible
    			// 1.8 Bottom Left Corner -> Not Possible
    			// 1.9 Top Right Corner ->  Possible
    			// 1.10 Bottom Right Corner -> Possible
    			rowUp = this.encodedData[i][prevRowIndex] ;
            	rowDown = this.encodedData[i][nextRowIndex];
            	colPrev = this.encodedData[prevColIndex][j];
            	// Case 1.1 -> Top End
    			//	|0 0|
    			//	|0 1|
    			//	|0 1|
    			if(rowUp == 0 && rowDown == 1 && colPrev == 0) {
    				return QRCode.BLOCK_TOP_END;
    			}
    			// Case 1.2 -> Bottom End
    			//	|0 1|
    			//	|0 1|
    			//	|0 0|
    			if(rowUp == 1 && rowDown == 0 && colPrev == 0) {
    				return QRCode.BLOCK_BOTTOM_END;
    			}
    			// Case 1.3 -> Left End - Not Possible
    			// Case 1.4 Right End ->  Possible
    			//  |0 0|
    			//	|1 1|
    			//	|0 0|
    			if(rowUp == 0 && rowDown == 0 && colPrev == 1) {
    				return QRCode.BLOCK_RIGHT_END;
    			}
    			// Case 1.5.1 -> Middle Blocks - Variant 1
    			// |0 1|
    			// |0 1|
    			// |0 1|
    			if(rowUp == 1 && rowDown == 1) {
    				return QRCode.BLOCK_MID;
    			}
    			// Case 1.6 -> Isolated blocks
            	// 	|0	0|
				//	|0	1|
				//	|0	0|
    			if(rowUp == 0 && rowDown == 0 && colPrev == 0) {
    				return QRCode.BLOCK_ISOLATED;
    			}
    			// Case 1.7 -> Top_Left_Corner --> Not Possible
    			// Case 1.8 -> Bottom left Corner--> Not Possible
            	// Case 1.9 Top Right Corner
    			//	|0 0|
    			//	|1 1|
    			//	|0 1|
    			if(rowUp == 0 && rowDown == 1 && colPrev == 1) {
    				log("BLOCK_CORNER_TOP_RIGHT:");
    				log("("+i+","+j+"):"+"CP:"+colPrev+",CN:"+colNext+ ",RU:"+rowUp +",RD:"+rowDown);
    				return QRCode.BLOCK_CORNER_TOP_RIGHT;
    			}

    			// Case 1.10 Bottom Right Corner 
    			//	|0 1|
    			//	|1 1|
    			//	|0 0|
    			if(rowUp == 1 && rowDown == 0 && colPrev == 1) {
    				return QRCode.BLOCK_CORNER_BOTTOM_RIGHT;
    			}
    		}else {
    			// last Column & Last Row
    			// Possible Scenarios
    			// 1. Bottom End
    			// 2. Right End
    			// 3. Bottom Right Corner
    			// 4. Isolated 
    			
    			// Case 1: Bottom End
    			// |0 1|
    			// |0 1|
    			
    			rowUp = this.encodedData[i][prevRowIndex] ;
            	colPrev = this.encodedData[prevColIndex][j];
            	log("Last Corner:["+i+","+j+"]");
            	
    			log("CP:"+colPrev+",CN:"+colNext+ ",RU:"+rowUp +",RD:"+rowDown);
            	if(rowUp == 1 && colPrev == 0) {
    				return QRCode.BLOCK_BOTTOM_END;
    			}
    			// Case 2: Right End
    			// |0 0|
    			// |1 1|
    			if(rowUp == 0 && colPrev == 1) {
    				return QRCode.BLOCK_RIGHT_END;
    			}
    			// Case 3: Bottom Right Corner
    			// |0 1|
    			// |1 1|
    			if(rowUp == 1 && colPrev == 1) {
    				return QRCode.BLOCK_CORNER_BOTTOM_RIGHT;
    			}
    			// Case 3: Isolated
    			// |0 0|
    			// |0 1|
    			if(rowUp == 0 && colPrev == 0) {
    				return QRCode.BLOCK_ISOLATED;
    			}
    		}//End Last Corner BLock (Last Col & Last Row)
    	}// End Last Column Section
    	// Let us do it for the First Row
    	if(!hasPrevRow && (hasNextColumn && hasPrevColumn)) {
    		//hasPrevRow && hasNextRow) {
			// 1.1 Top End -> Possible
			// 1.2 Bottom End -> Not Possible
			// 1.3 Left End -> Possible
			// 1.4 Right End ->  Possible
			// 1.5 Mid Blocks -> Possible
			// 1.6 Isolated Blocks -> Possible
			// 1.7 Top Left Corner -> Possible
			// 1.8 Bottom Left Corner -> Not Possible
			// 1.9 Top Right Corner ->  Possible
			// 1.10 Bottom Right Corner -> Not Possible
    		
        	rowDown = this.encodedData[i][nextRowIndex];
        	colPrev = this.encodedData[prevColIndex][j];
        	colNext = this.encodedData[nextColIndex][j];
    		// Case 1.1 Top End
        	// |0 1 0|
        	// |0 1 0|
        	if(colPrev == 0 && colNext == 0 && rowDown == 1) {
        		return QRCode.BLOCK_TOP_END;
        	}
        	// Case 1.3 Left End
        	// |0 1 1|
        	// |0 0 0|
        	if(colPrev == 0 && colNext == 1 && rowDown == 0) {
        		return QRCode.BLOCK_LEFT_END;
        	}
        	// Case 1.4 Right End
        	// |1 1 0|
        	// |0 0 0|
        	if(colPrev == 1 && colNext == 0 && rowDown == 0) {
        		return QRCode.BLOCK_RIGHT_END;
        	}
        	// Case 1.5 Mid Blocks
        	// |1 1 1|
        	// |0 0 0|
        	if(colPrev == 1 && colNext == 1) {//
        		return QRCode.BLOCK_MID;
        	}
	    	// Case 1.6 Isolated Blocks
	    	// |0 1 0|
	    	// |0 0 0|
	    	if(colPrev == 0 && colNext == 0 && rowDown == 0) {//
	    		return QRCode.BLOCK_ISOLATED;
	    	}
	    	// Case 1.7 Top Left
	    	// |0 1 1|
	    	// |0 1 0|
	    	if(colPrev == 0 && colNext == 1 && rowDown == 1) {//
	    		return QRCode.BLOCK_CORNER_TOP_LEFT;
	    	}
	    	// Case 1.8 Top Right
	    	// |1 1 0|
	    	// |0 1 0|
	    	if(colPrev == 1 && colNext == 0 && rowDown == 1) {//
	    		return QRCode.BLOCK_CORNER_TOP_RIGHT;
	    	}
	    }//End First Row
    	// Let us do it for the Last Row
    	if(!hasNextRow /*Mens its a last row*/ && (hasNextColumn && hasPrevColumn/*Exclude last column & last row*/)) {
			// 1.1 Top End -> Not Possible
			// 1.2 Bottom End -> Possible
			// 1.3 Left End -> Possible
			// 1.4 Right End ->  Possible
			// 1.5 Mid Blocks -> Possible
			// 1.6 Isolated Blocks -> Possible
			// 1.7 Top Left Corner -> Not Possible
			// 1.8 Bottom Left Corner -> Possible
			// 1.9 Top Right Corner ->  Not Possible
			// 1.10 Bottom Right Corner -> Possible
    		
        	rowUp = this.encodedData[i][prevRowIndex];
        	colPrev = this.encodedData[prevColIndex][j];
        	colNext = this.encodedData[nextColIndex][j];
        	log("The Last Row:"+"CP:"+colPrev+",CN:"+colNext+ ",RU:"+rowUp +",RD:"+rowDown);
        	// Case 1.2 Bottom End
        	// |0 1 0|
        	// |0 1 0|
        	if(colPrev == 0 && colNext == 0 && rowUp == 1) {
        		log("Returned:-BLOCK_BOTTOM_END");
        		return QRCode.BLOCK_BOTTOM_END;
        	}
        	// Case 1.4 Right End
        	// |0 0 0|
        	// |1 1 0|
        	if(colPrev == 1 && colNext == 0 && rowUp == 0) {
        		log("Returned:-BLOCK_RIGHT_END");
        		return QRCode.BLOCK_RIGHT_END;
        	}
        	// Case 1.3 Left End
        	// |0 0 0|
        	// |0 1 1|
        	if(colPrev == 0 && colNext == 1 && rowUp == 0) {
        		log("Returned:-BLOCK_LEFT_END");
        		return QRCode.BLOCK_LEFT_END;
        	}
        	// Case 1.5 Mid BLocks
        	// |0 0 0|
        	// |1 1 1|
        	if(colPrev == 1 && colNext == 1){
        		log("Returned:-BLOCK_MID");
        		return QRCode.BLOCK_MID;
        	}
        	// Case 1.6 Isolated
        	// |0 0 0|
        	// |0 1 0|
        	if(colPrev == 0 && colNext == 0 && rowUp == 0) {
        		log("Returned:-BLOCK_ISOLATED");
        		return QRCode.BLOCK_ISOLATED;
        	}
        	// Case 1.7 Bottom Left
        	// |0 1 0|
        	// |0 1 1|
        	if(colPrev == 0 && colNext == 1 && rowUp == 1) {
        		log("Returned:-BLOCK_CORNER_BOTTOM_LEFT");
        		return QRCode.BLOCK_CORNER_BOTTOM_LEFT;
        	}
        	//2019-09-15 15:20:27.595:QRCode:-The Last Row:CP:0,CN:1,RU:0,RD:-1
        	//2019-09-15 15:20:27.595:QRCode:-Returned:-BLOCK_RIGHT_END
        	//
        	// Case 1.7 Bottom Right
        	// |0 1 0|
        	// |1 1 0|
        	if(colPrev == 1 && colNext == 0 && rowUp == 1) {
        		log("Returned:-BLOCK_CORNER_BOTTOM_RIGHT");
        		return QRCode.BLOCK_CORNER_BOTTOM_RIGHT;
        	}       	
    	}//End Last Row.
    	// Now Inner Area Remaining
    	if(hasNextColumn && hasPrevColumn && hasNextRow && hasPrevRow) {
    		// 1.1 Top End -> Possible
			// 1.2 Bottom End -> Possible
			// 1.3 Left End -> Possible
			// 1.4 Right End ->  Possible
			// 1.5 Mid Blocks -> Possible
			// 1.6 Isolated Blocks -> Possible
			// 1.7 Top Left Corner -> Possible
			// 1.8 Bottom Left Corner -> Possible
			// 1.9 Top Right Corner ->  Possible
			// 1.10 Bottom Right Corner -> Possible
    		rowUp = this.encodedData[i][prevRowIndex];
    		rowDown = this.encodedData[i][nextRowIndex];
    		colPrev = this.encodedData[prevColIndex][j];
        	colNext = this.encodedData[nextColIndex][j];
    		// Case 1.1 Top End
    		// |0 0 0|
    		// |0 1 0|
    		// |0 1 0|
        	if((colPrev==0 && colNext==0)&&(rowUp==0 && rowDown==1)) {
        		return QRCode.BLOCK_TOP_END;
        	}
        	// Case 1.2 Bottom End
    		// |0 1 0|
    		// |0 1 0|
    		// |0 0 0|
        	if((colPrev==0 && colNext==0)&&(rowUp==1 && rowDown==0)) {
        		return QRCode.BLOCK_BOTTOM_END;
        	}
        	// Case 1.3 Left End
    		// |0 0 0|
    		// |0 1 1|
    		// |0 0 0|
        	if((colPrev==0 && colNext==1)&&(rowUp==0 && rowDown==0)) {
        		return QRCode.BLOCK_LEFT_END;
        	}
        	// Case 1.4 Right End
    		// |0 0 0|
    		// |1 1 0|
    		// |0 0 0|
        	if((colPrev==1 && colNext==0)&&(rowUp==0 && rowDown==0)) {
        		return QRCode.BLOCK_RIGHT_END;
        	}
        	// Case 1.5 Mid End
    		// |0 1 0|
    		// |1 1 1|
    		// |0 1 0|
        	if((colPrev==1 && colNext==1)||(rowUp==1 && rowDown==1)) {
        		return QRCode.BLOCK_MID;
        	}
        	// Case 1.6 Isolated blocks
    		// |0 0 0|
    		// |0 1 0|
    		// |0 0 0|
        	if((colPrev==0 && colNext==0)&&(rowUp==0 && rowDown==0)) {
        		return QRCode.BLOCK_ISOLATED;
        	}
        	// Case 1.7 Top Left
    		// |0 0 0|
    		// |0 1 1|
    		// |0 1 0|
        	if((colPrev==0 && colNext==1)&&(rowUp==0 && rowDown==1)) {
        		return QRCode.BLOCK_CORNER_TOP_LEFT;
        	}
        	// Case 1.8 Top Right
    		// |0 0 0|
    		// |1 1 0|
    		// |0 1 0|
        	if((colPrev==1 && colNext==0)&&(rowUp==0 && rowDown==1)) {
        		return QRCode.BLOCK_CORNER_TOP_RIGHT;
        	}
        	// Case 1.9 Bottom Left
    		// |0 1 0|
    		// |0 1 1|
    		// |0 0 0|
        	if((colPrev==0 && colNext==1)&&(rowUp==1 && rowDown==0)) {
        		return QRCode.BLOCK_CORNER_BOTTOM_LEFT;
        	}
        	// Case 1.10 Bottom Right
    		// |0 1 0|
    		// |1 1 0|
    		// |0 0 0|
        	if((colPrev==1 && colNext==0)&&(rowUp==1 && rowDown==0)) {
        		return QRCode.BLOCK_CORNER_BOTTOM_RIGHT;
        	}
    		
    	}
    	return QRCode.BLOCK_LOCATION_UNKNOWN;
    	
    }//findDataBlockLocation()
    
    private static void log(String msg) {
        
        //System.out.println(QRUtils.getCurrentTimeStamp()+":"+alias + ":-" + msg);

    }
    private void save(String data) {
        String path = "/Volumes/iWant/QR/iWant_QR/images/qrdata.txt";
        File logFile = new File(path);
        
        try {
        	logFile.createNewFile();
        	BufferedWriter log = new BufferedWriter(new FileWriter(logFile,true));
        	log.write(data);
        	log.flush();
        	log.close();
        }catch(Exception e) {
        	e.printStackTrace();
        }
        
    }
    
    private String getColorInfo(Color c) {
    	StringBuffer data = new StringBuffer();
    	// generate color string with RGB
    	int r = c.getRed();
    	int g = c.getGreen();
    	int b = c.getBlue();
    	
    	data.append("R:"+r);
    	data.append(" G:"+g);
    	data.append(" B:"+b);
    	
    	return data.toString();
    }



}