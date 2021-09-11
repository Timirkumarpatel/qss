package io.itpl.qss.encoder;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.itpl.qss.exception.QREncoderException;


import java.util.EnumMap;
import java.util.Map;


public class QREncoder {
	
	
    public int version = 2;
    public int size = 25;
    public ErrorCorrectionLevel errorCorrectionLevel = ErrorCorrectionLevel.L;
    public byte [][] encodedData;
    
    public static final int MAX_DATA_CAPACITY = 4296;
    public static final int MIN_VERSION = 1;
    public static final int MAX_VERSION = 40;
    
    public QREncoder(){
        encodedData = new byte[this.size][this.size];
    }
    public QREncoder(int version,int size, int errorLevel){
        this.version = version;
        this.size = size;
        switch(errorLevel) {
	        case 0:
	        	this.errorCorrectionLevel = ErrorCorrectionLevel.L;
	        	break;
	        case 1:
	        	this.errorCorrectionLevel = ErrorCorrectionLevel.M;
	        	break;
	        case 2:
	        	this.errorCorrectionLevel = ErrorCorrectionLevel.Q;
	        	break;
	        case 4:
	        	this.errorCorrectionLevel = ErrorCorrectionLevel.H;
	        	break;
        }
        encodedData = new byte[this.size][this.size];
    }
    private BitMatrix encode(String data){
		try{
			Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
			hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
			hintMap.put(EncodeHintType.QR_VERSION, this.version);
			hintMap.put(EncodeHintType.MARGIN, 0); /* default = 4 */
			hintMap.put(EncodeHintType.ERROR_CORRECTION, this.errorCorrectionLevel);

			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, this.size,
						size, hintMap);
			return byteMatrix;

		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
    } 
    public boolean encodeQRData(String data){
        BitMatrix byteMatrix = encode(data);
        if(byteMatrix!=null){
            int iWidth = byteMatrix.getWidth(); 
            for (int i = 0; i < iWidth; i++) {
                for (int j = 0; j < iWidth; j++) {
                    //System.out.print(byteMatrix.get(i, j)?"|1":"|0");
                    if (byteMatrix.get(i, j)) {
                        encodedData[i][j] = 1;
                    }else{
                        encodedData[i][j] = 0;
                    }
                }
            }
            
            return true;
        }else{
            return false;
        }   
    }
    public byte[][] getEncodedQRMetrix(){
        return this.encodedData;
    }
   
    /**
     * @param dataLength - Size of the data to be encoded in QR Code.
     * @param errorCorrectionLevel - Required error correction level.
     * @return integer with QR Size value
     * @throws QREncoderException in case of mismatch of QR data and version.
     * */
    public static int getCompatibleVersion(int dataLength, int errorCorrectionLevel)throws QREncoderException {
    	// The QR Version & Respective data capacity matrix.
    	//logger.trace("Calculating QR Version for Data Size:{}, errorCorrectionLevel:{}",dataLength,errorCorrectionLevel);
    	 if(dataLength > MAX_DATA_CAPACITY) 
    		 throw new QREncoderException ("Data is too big for encoding:"+dataLength + "MAX:["+MAX_DATA_CAPACITY+"]");
    	 if(errorCorrectionLevel <0 || errorCorrectionLevel >3) 
    		 new QREncoderException("Invalid Data Correction Level:"+errorCorrectionLevel);
    	 
    	 int capacityMatrix [][] = new int [][]{ 	
	    									{25,20,10,10},
	    									{47,38,29,20},
	    									{77,61,47,35},
	    									{114,90,67,50},
	    									{154,122,87,64},
	    									{195,154,108,84},
	    									{224,178,125,93},
	    									{279,221,157,122},
	    									{335,262,189,143},
	    									{395,311,221,174},
	    									{468,366,259,200},
	    									{535,419,296,227},
	    									{619,483,352,259},
	    									{667,528,376,283},
	    									{758,600,426,321},
	    									{854,656,470,365},
	    									{938,734,531,408},
	    									{1046,816,574,452},
	    									{1153,909,644,493},
	    									{1249,970,702,557},
	    									{1352,1035,742,587},
	    									{1460,1134,823,640},
	    									{1588,1248,890,672},
	    									{1704,1326,963,744},
	    									{1853,1451,1041,779},
	    									{1990,1542,1094,864},
	    									{2132,1637,1172,910},
	    									{2223,1732,1263,958},
	    									{2369,1839,1322,1016},
	    									{2520,1994,1429,1080},
	    									{2677,2113,1499,1150},
	    									{2840,2238,1618,1226},
	    									{3009,2369,1700,1307},
	    									{3183,2506,1787,1394},
	    									{3351,2632,1867,1431},
	    									{3537,2780,1966,1530},
	    									{3729,2894,2071,1591},
	    									{3927,3054,2181,1658},
	    									{4087,3220,2298,1774},
	    									{4296,3391,2420,1852}
    									
    									};
		for(int row=0;row<capacityMatrix.length;row++) {
			if(capacityMatrix[row][errorCorrectionLevel]>= dataLength+1) {
				//System.out.println("Matched QR Version:{} with Capacity{} agained required size:{}",(row+1),capacityMatrix[row][errorCorrectionLevel],dataLength);
				
				return (row + 2)<=40 ? row+2 : 40 ;	
			}
		}	
    	return -1;
    }
    public static void test(String []args) {
    	try {
	    	int input = Integer.parseInt(args[0]);
	    	int error = Integer.parseInt(args[1]);
	    	int version = getCompatibleVersion(input,error);
	    	System.out.println("version is:"+version);
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
    }

}
