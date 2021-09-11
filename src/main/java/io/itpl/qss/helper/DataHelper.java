package io.itpl.qss.helper;



import io.itpl.qss.exception.QREncoderException;
import io.itpl.qss.utils.QRUtils;

import java.util.Hashtable;

public class DataHelper {
	private final static String ALIAS = "DataHelper";
	/**
	 * * Generate a String for to embed the WIFI Connection with QR Code. 
	 * @param SSID Name of the WIFI SSID
	 * @param password Password for connecting to the given SSID.
	 * @param security Security type of the SSID (i.e. WEB/WPA etc.)
	 * @return String contains the link to WIFI SSID for quick connection using QR Scan.
	 * **/
	public static String getWifiConnectionString(String SSID, String password, String security){
        //WIFI:S:<SSID>;T:<WPA|WEP|>;P:<password>;H:<true|false|>;
        StringBuffer data = new StringBuffer();
        data.append("WIFI:S:");
        data.append(SSID);
        data.append(";T:");
        data.append(security);
        data.append(";P:");
        data.append(password);
        data.append(";H:false;");
        log("WIFI-STRING:"+data.toString());
        return data.toString();
    }
	/**
	 * * Generate a String for QR Code encoding which can trigger SMS to given number  with preset text msg upon scanning. 
	 * @param number The Target Number to initiate the SMS.
	 * @param msg Predefined SMS test to be sent upon QR Scan.
	 * @return String with SMS format
	 * */
	public static String createForSMS(String number,String msg) {
		//SMSTO:+1123456:This is a SMS stored in a QR Code!
		return "SMSTO:"+number+":"+msg;
	}
	/**
	 * * Generate a String for QR Code encoding which can trigger the Make a Call to given number upon scanning. 
	 * @param number The Target Number to initiate the quick call.
	 * @return String with Quick call link format
	 */
	public static String createForTel(String number) {
		return "TEL:"+number;
	}
	/**
	 * Transform the given location info (i.e. Latitude and Longitude ) to the Location link data. The User will be able to see the given location on Map upon scanning the QR Code. 
	 * *
	 * @param _lat Latitude value of the location.
	 * @param _long Longitude value of the location.
	 * @return String with geo link
	 */
	public static String creteForLocation(double _lat, double _long) {
		//geo:40.71872,-73.98905,100
		return "geo:"+_lat+","+_long;
	}
	/**
	 * Transform the given contactInfo Map to the VCard 4.0 object. The QR Code scanning will response as a VCF data which can be instantly saved by user.
	 *  The naming convention for the keys is listed as below,
	 *  <ul>
	 * <li>"Name" - Name of the Contact (Mandatory)
	 * <li>"LName" - Last Name of Family name (linked to FN element of VCard)
	 * <li>"Company" - Name of the Organisation or Company the contact belongs to. Linked to "ORG" element.
	 * <li>"Designation" - Designation of the person in given company.
	 * <li>"Work-Phone" - The Telephone number mapped to "TEL:TYPE:work" element
	 * <li>"Home-Phone" - Secondary Contact number mapped to "TEL:TYPE:home" element
	 * <li>"Email" - Email address of the Contact
	 * </ul>
	 * @param contactInfo (Hashtable object with key and value both are string) with format as described.
	 * @return The VCard4.0 in String format (UTF-8)
	 * @throws com.iwantunlimited.qss.exception.QREncoderException in case data is missing or having incorrect format.
	 * 
	 * */
	public static String createVCardFrom(Hashtable<String,String> contactInfo) throws QREncoderException {
		
		StringBuffer data = new StringBuffer();
		data.append("BEGIN:VCARD\n");
		data.append("VERSION:4.0");
		//N:Public;John;Quinlan;Mr.;Esq.
		// N:Stevenson;John;Philip,Paul;Dr.;Jr.,M.D.,A.C.P
		if(contactInfo.containsKey("Name")) {
			data.append("N:"+contactInfo.get("Name")+";\n");
		}else
			throw new QREncoderException("Invalid QR Data, First Name missing");
		//FN:Mr. John Q. Public\, Esq
		if(contactInfo.containsKey("LName")) {
			data.append("FN:"+contactInfo.get("LName")+";\n");
		}else
			data.append("FN:"+contactInfo.get(" ;")+";\n");
		/*
		 * Example: A property value consisting of an organizational name,
   		organizational unit #1 name, and organizational unit #2 name.

           ORG:ABC\, Inc.;North American Division;Marketing

		 */
		if(contactInfo.containsKey("Company")) {
			data.append("ORG:"+contactInfo.get("Company")+";\n");
		}
		//TITLE:Research Scientist
		if(contactInfo.containsKey("Designation")) {
			data.append("TITLE:"+contactInfo.get("Designation")+"\n");
		}
		//TEL;VALUE=uri;TYPE=home:tel:+33-01-23-45-67
		//TEL;VALUE=uri;PREF=1;TYPE="voice,home":tel:+1-555-555-5555;ext=5555
		if(contactInfo.containsKey("Work-Phone")) {
			data.append("TEL;VALUE=uri;TYPE=voice,work:tel:"+contactInfo.get("Work-Phone")+"\n");
		}
		if(contactInfo.containsKey("Home-Phone")) {
			data.append("TEL;VALUE=uri;TYPE=voice,home:tel:"+contactInfo.get("Home-Phone")+"\n");
		}
		
		//EMAIL;TYPE=work:jqpublic@xyz.example.com
		//EMAIL;PREF=1:jane_doe@example.com
		if(contactInfo.containsKey("Email")) {
			data.append("EMAIL;TYPE=work:"+contactInfo.get("Email")+"\n");
		}
		data.append("END:VCARD\n");
		return data.toString();
	}
	private static void log(String msg) {
    	System.out.println(QRUtils.getCurrentTimeStamp()+":"+ALIAS+":-"+msg);
    }
}
