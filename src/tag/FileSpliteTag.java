package tag;

import hash.MapToHash;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import java.io.File;  
import java.io.IOException;
import java.io.InputStream;  
import java.io.FileInputStream;  
import java.io.OutputStream;  
import java.io.FileOutputStream;  
import java.util.Properties;  
import java.util.Scanner;

/*
 * �����û��˴���
 * �û�����Ҫ������
 * 1.�ָ��ļ�
 * 2.��ÿ����������Ӧ����֤��ǩ
 * ע�⣺1.������ĳ����о��д�����IO������������Ϊ����֮��Ĳ�������ÿһ��ûÿһ��������ɵ���֤��ǩ�鶼�ֱ�����һ����Ӧ���ļ���������ͱ�ǩ�������
 *     2.Ϊ�˼��ٶ�IO���Ĳ���������Ҳ���Բ������ļ����ļ������������ɵ��ļ����ǩд��һ���ļ��С�
 *     3.���÷���1���ϴ����ƶ���ʽ�������ļ�+������ǩ���ļ��������÷���2.�ϴ����ƶ˵���ʽΪ�����ļ�����ǩ�ļ�����
 *     4.����Ϊ����֮����֤�Ĳ��������÷���1����ʽ��
 */


public class FileSpliteTag {
	
	public static Pairing pairing = PairingFactory.getPairing("Ecc.properties");
	
	
	public static void main(String[] args) throws Exception {
		
		Element[] Y = new Element[4];
		String[]  S = {"P.txt","Q.txt","SK1.txt","SK2.txt"};
		for(int i=0;i<4;i++) {
			
			String s = S[i];
			File f = new File(s);
			InputStream in = null;
			
			in = new FileInputStream(f);
			byte b[] = new byte[(int) f.length()];
			in.read(b);
			
				
			Element U=pairing.getG1().newRandomElement();
			U.setFromBytes(b);
			Y[i]=U.getImmutable();
			
			in.close();
			
		}
		
		String sourceFilePath = "C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + "Desktop" + File.separator + "fg"+ File.separator +"qw.txt";  
	    
		int partFileLength = 0;
	    String ID = null;
	    Scanner in = new Scanner(System.in);
	    System.out.println("���û��������:");
	    ID = in.nextLine();
	    System.out.println("�������ļ��ֿ�Ĵ�С:");
	    partFileLength = in.nextInt();
	    
	    
	    //����r��R,Ϊ����֮���������Rд��R.txt�ļ��С�
	   
	    Element r= pairing.getZr().newRandomElement().getImmutable();
		Element R = Y[0].powZn(r).getImmutable();
		
		byte[] b =null;
		OutputStream  os = null;
		String s = "R.txt";
		os = new FileOutputStream(s);
		b = R.toBytes();
		os.write(b);
		os.close();
		
		System.out.println("��ʼ�ֿ��������ǩ");
		generateTag(sourceFilePath,partFileLength,ID,R,r,Y);
	    System.out.println("��ǩ���ɽ���");
	    
	}
	
	
	public static void generateTag(String sourceFilePath,int partFileLength,String ID,Element R,
			Element r,Element[] Y) throws IOException{
		
		File sourceFile = null;  
        File targetFile = null;  
        InputStream ips = null;  
        OutputStream ops = null;  
        OutputStream configOps = null;
        Properties partInfo = null;
        byte[] buffer = null;  
        int partNumber = 1;  
        
        sourceFile = new File(sourceFilePath);
        ips = new FileInputStream(sourceFile);
        
        configOps = new FileOutputStream(new File("C:" + File.separator + "Users" + File.separator +
        		"qiankun" + File.separator + "Desktop"  + File.separator + "fg"  + File.separator+ "config.properties"));
        
        buffer = new byte[partFileLength];
        int tempLength = 0;  
        partInfo = new Properties();
        
        while((tempLength = ips.read(buffer,0,partFileLength)) != -1){  
        	//�ļ����·��
        	String targetFilePath = "C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
        			"Desktop"  + File.separator+"fg"+ File.separator + "FileSplite" + File.separator+"part_" + (partNumber);  
            //�ļ�����֤��ǩ��·��
        	String targetFileTagPath ="C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
        			"Desktop"  + File.separator+"fg"+ File.separator + "FileTagSplite" + File.separator+"partTag_" + (partNumber);
           
            //д����־��Ϣ
        	partInfo.setProperty((partNumber++)+"",targetFilePath);
        	//�ļ���洢
        	targetFile = new File(targetFilePath);  
            ops = new FileOutputStream(targetFile);
            ops.write(buffer,0,tempLength);//����Ϣд����Ƭ�ļ�  
            ops.close();//�ر���Ƭ�ļ�  
            
            
            //��ǩ�����
            
            Element[] t = new Element[2];
            t=MapToHash.Hash(targetFilePath, ID, R,  partNumber-1+"");
    		Element m1 = pairing.getZr().newRandomElement();
    		m1.setFromBytes(buffer);
    		Element m=m1.getImmutable();
    		Element y0=Y[1].powZn(m).getImmutable();
    		Element y1=Y[1].powZn(r).getImmutable();
    		Element y2=Y[2].powZn(t[0].getImmutable()).getImmutable();
    		Element y3 =Y[3].powZn(t[1].getImmutable()).getImmutable();
    		Element T=y0.mul(y1).mul(y2).mul(y3);
    		
    		//��ǩ��洢
    		targetFile = new File(targetFileTagPath);  
    		ops = new FileOutputStream(targetFile);
            ops.write(T.toBytes());
            ops.close();
            
        }
        
        partInfo.setProperty("name",sourceFile.getName());   
        partInfo.store(configOps,"ConfigFile");
        ips.close();
        
	
	}
	
}
		

