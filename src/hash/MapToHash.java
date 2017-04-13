package hash;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;

public class MapToHash {
	
	public static  Pairing pairing = PairingFactory.getPairing("Ecc.properties");
	
	//哈希函数
	
	public static String SHA256(final String strText) {
		
		
		String strResult = null;  
		
		if (strText != null && strText.length() > 0) {
			
			try{
				
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.update(strText.getBytes());
				byte byteBuffer[] = messageDigest.digest();
				StringBuffer strHexString = new StringBuffer();
				
				for (int i = 0; i < byteBuffer.length; i++){
					
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					
					if (hex.length() == 1) {
						
						strHexString.append('0');}
					
					strHexString.append(hex);
					
				}
				
				strResult = strHexString.toString();
				
			}
			
			catch (NoSuchAlgorithmException e) {
				
				e.printStackTrace();}
			
		}
		
		return strResult;
		
	}  

		/*参数表示：
		s1表示文件块的地址
		ID表示用户的身份；
		R是群中的一个元素
		index：表示文件块索引

		函数返回二个群中的元素H(文件块||身份||R||0)，H(index||ID||1)
		 */  

		public static Element[] Hash(String s1,String ID,Element R,String index) throws IOException {
			
			Element[] Q = new Element[2];
			
			BufferedReader br = new BufferedReader(new FileReader(s1));  
			String data = br.readLine();
			String tr ="";
			while( data!=null){
				tr+=data;
				data = br.readLine();}
			tr=tr+ID+R.toString()+"0";
			tr=SHA256(tr);
			
			Q[0]= pairing.getZr().newRandomElement();
			Q[0].setFromBytes(tr.getBytes());
			Q[0].getImmutable();
			
			tr=index+ID+"1";
			tr=SHA256(tr);
			
			Q[1]= pairing.getZr().newRandomElement();
			Q[1].setFromBytes(tr.getBytes());
			Q[1].getImmutable();
			
			return Q;
			
		}

		public static Element Hash(String index, String ID) {
			
			Element Q= null;
			
			String tr ="";
			tr=index+ID+"1";
			tr=SHA256(tr);
			
			Q= pairing.getZr().newRandomElement();
			Q.setFromBytes(tr.getBytes());
			Q.getImmutable();
			
			return Q;
			
		}
		

}


