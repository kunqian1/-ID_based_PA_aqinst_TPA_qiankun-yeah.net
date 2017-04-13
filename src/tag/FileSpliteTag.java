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
 * 这是用户端代码
 * 用户端主要工作：
 * 1.分割文件
 * 2.对每个块生成相应的认证标签
 * 注意：1.在下面的程序中具有大量的IO流操作，我们为方便之后的操作，对每一个没每一个快和生成的认证标签块都分别生成一个对应的文件块操作，和标签块操作。
 *     2.为了减少对IO流的操作，我们也可以不生成文件块文件，把所用生成的文件块标签写在一个文件中。
 *     3.采用方法1，上传到云端形式：（块文件+单个标签块文件）。采用方法2.上传到云端的形式为：（文件，标签文件）。
 *     4.我们为方便之后验证的操作，采用方法1的形式。
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
	    System.out.println("请用户输入身份:");
	    ID = in.nextLine();
	    System.out.println("请输入文件分块的大小:");
	    partFileLength = in.nextInt();
	    
	    
	    //生成r和R,为方便之后操作，将R写在R.txt文件中。
	   
	    Element r= pairing.getZr().newRandomElement().getImmutable();
		Element R = Y[0].powZn(r).getImmutable();
		
		byte[] b =null;
		OutputStream  os = null;
		String s = "R.txt";
		os = new FileOutputStream(s);
		b = R.toBytes();
		os.write(b);
		os.close();
		
		System.out.println("开始分块和生产标签");
		generateTag(sourceFilePath,partFileLength,ID,R,r,Y);
	    System.out.println("标签生成结束");
	    
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
        	//文件块的路径
        	String targetFilePath = "C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
        			"Desktop"  + File.separator+"fg"+ File.separator + "FileSplite" + File.separator+"part_" + (partNumber);  
            //文件块认证标签的路径
        	String targetFileTagPath ="C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
        			"Desktop"  + File.separator+"fg"+ File.separator + "FileTagSplite" + File.separator+"partTag_" + (partNumber);
           
            //写入日志信息
        	partInfo.setProperty((partNumber++)+"",targetFilePath);
        	//文件块存储
        	targetFile = new File(targetFilePath);  
            ops = new FileOutputStream(targetFile);
            ops.write(buffer,0,tempLength);//将信息写入碎片文件  
            ops.close();//关闭碎片文件  
            
            
            //标签块计算
            
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
    		
    		//标签块存储
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
		

