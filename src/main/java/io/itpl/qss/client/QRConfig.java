package io.itpl.qss.client;


import io.itpl.qss.encoder.QREncoder;
import io.itpl.qss.exception.InvalidQRConfigException;
import io.itpl.qss.exception.QREncoderException;
import io.itpl.qss.render.StyleConfig;

import java.net.MalformedURLException;

public class QRConfig {

    private int _version;
    private int _size;
    private int qrImageSize;
    private StyleConfig qrStyle;
    private int dataType;
    private String textData;
    private long numericData;
    private Object binaryData;
    private int margin;
    private boolean hasLogo;
    private String logoURL;
    
    private int errorCorrectionLevel = 0;

    public static final int ALPHANUMERIC_DATA = 0;
    public static final int NUMERIC_DATA = 1;
    public static final int BINARY_DATA = 2;
    private static final int BASE_SIZE = 21;
    // Just for the validation purpose.
    private static final int MIN_SIZE = 21;
    // This is estimated maximum size that we may need to produce for high resolution QR Image.
    private static final int MAX_SIZE = 264000; 
    
    public static final int SIZE_SMALL = 10;
    public static final int SIZE_MEDIUM = 40;
    public static final int SIZE_LARGE = 100;
    
    private static final String ALIAS = "QRConfig";
    
    
    
    /**
     * QRConfig encalculates the property of the QR Code. This incldes the QR version, Size and Data that need to be encoded.
     * The only parameter we need is to create the QRConfig is QR Code version. QRCode Properties will be set to default values upon creating the new instance. 
     * @param version
     * @throws InvalidQRConfigException
     */
    private QRConfig(){

        
    }
    public static QRConfig from(String data, int qrSize) throws InvalidQRConfigException,MalformedURLException, QREncoderException {
    	QRConfig config = new QRConfig();
    	config.setTextData(data);
    	config.hasLogo = false;
    	config.setSize(qrSize);
    	config.dataType = 0;
    	
    
    	config.margin = Math.round(config._size*0.1f);// Marked it comment for debugging the issue.
    	config.qrImageSize = config._size + config.margin;
    	config.qrStyle = StyleConfig.getDefaultStyleConfig();
    	//config.log(""+config.toString());
    	return config;  	
    }
    public static QRConfig from(String data, int qrSize,String logoImageURL) throws InvalidQRConfigException,MalformedURLException, QREncoderException{
    	QRConfig config = new QRConfig();
    	config.setLogoURL(logoImageURL);
    	config.setTextData(data);
    	config.setSize(qrSize);
    	config.dataType = 0;
    	
    
    	config.margin = Math.round(config._size*0.1f);// Marked it comment for debugging the issue.
    	config.qrImageSize = config._size + config.margin;
    	config.qrStyle = StyleConfig.getDefaultStyleConfig();
    	//config.log(""+config.toString());
    	return config;  	
    }
    
    
    private void setSize(int inputSize)throws InvalidQRConfigException{
    	int base = ((this._version-1)*4 )+ 21;
    	int prev_Size = this._size;
    	this._size = base * inputSize;
        log("QRConfig - Size updated from "+prev_Size+" => "+this._size);
    }
    /**
     * *
     * @param style instance of the StyleConfing to set the visual appearance of barcode.
     */
    
    public void setStyleConfig(StyleConfig style){
        this.qrStyle = style;
    }
    /**
     * @param text The actual data of which need to be encoded into QR Code.
     * */
    private void setTextData(String text) throws QREncoderException{
    	if(text.length()> QREncoder.MAX_DATA_CAPACITY)
    		throw new QREncoderException("Data is too big (must be less <"+ QREncoder.MAX_DATA_CAPACITY+")");
    	
    	this._version = QREncoder.getCompatibleVersion(text.length(), errorCorrectionLevel);
    	
        this.dataType = QRConfig.ALPHANUMERIC_DATA;
        this.textData = text;
        // We need to add the validation of data length based on the version & error correction level.
    }

    private void setLogoURL(String logo) throws QREncoderException{
    	this.hasLogo = true;
    	this.errorCorrectionLevel = 3;
        this.logoURL = logo;
        
    }
    public boolean validate() throws QREncoderException {
    	int dataLength = this.getAlphanumericData().length();
    	if(this.hasLogo || (this.logoURL !=null && this.logoURL.length()>3)) {
    		this.errorCorrectionLevel = 3;
    	}
    	
    	this._version = QREncoder.getCompatibleVersion(dataLength, errorCorrectionLevel);
    	log("QRConfig::validate()-"+this._version +"(size:"+dataLength+",Error Correction:"+errorCorrectionLevel+")");
    	return true;
    }
    
    public int getQRDataType(){
        return this.dataType;
    }
    public String getAlphanumericData(){
        return this.textData;
    }
    public Object getBinaryData(){
        return this.binaryData;
    }
    public long getNumericData(){
        return this.getNumericData();
    }
    public int getErrorCorrectionLevel() {
    	return this.errorCorrectionLevel;
    }
    public int getQRVersion(){
        return this._version;
    }
    public int getQRSize(){
        return this._size;
    }
    public int getQRImageSize(){
        return this.qrImageSize;
    }
    public int getMargin(){
        return this.margin;
    }
    public StyleConfig getStyleConfig(){
        return this.qrStyle;
    }
    public String getLogoImageURL(){
        return this.logoURL;
    }
    public boolean isLogoRequired(){
        return this.hasLogo;
    }
    public String toString(){
        StringBuffer data = new StringBuffer();
        data.append("QR Config Details:______________\n");
        data.append("Version:"+this._version+"\n");
        data.append("QR Size:"+this._size+"\n");
        data.append("Error Correction:"+this.errorCorrectionLevel+"\n");
        data.append("Margin:"+this.margin+"\n");
        data.append("QR Image Size:"+this.margin+"\n");
        data.append("Data Type:"+this.dataType+"\n");
        data.append("Overly Logo:"+this.hasLogo+"\n");
        data.append("Logo URL:"+this.logoURL+"\n");
        data.append("_______________________________\n");
        
        
        return data.toString();

    }
    private void log(String msg) {
    	//System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS+":-"+msg);
    }
    
}
