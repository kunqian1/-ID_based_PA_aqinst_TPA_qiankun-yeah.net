package proof;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;
import hash.MapToHash;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;



public class GenerateProof{
	
	public static Pairing pairing = PairingFactory.getPairing("Ecc.properties");
	 
	
	public static void main(String[] args) throws IOException{
		
		
		Element[] Y = new Element[3];
		String[]  S = {"P.txt","Q.txt","R.txt"};
		for(int i=0;i<3;i++){
			
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
		
		
		String s = "z.txt";
		
		File f = new File(s);
		InputStream in = null;
		in = new FileInputStream(f);
		byte b[] = new byte[(int) f.length()];
		in.read(b);
		
		Element U=pairing.getZr().newRandomElement();
		U.setFromBytes(b);
		Element z=U.getImmutable();
		in.close();
		
		
		//得到证明信息：
		
		 LinkedHashSet<Integer> lsh =new LinkedHashSet<Integer>();
		 BufferedReader br = new BufferedReader(new FileReader("I.txt"));  
		 String data = br.readLine();  
		 String tr ="";
		    while( data!=null){  
		          tr+=data;
		          data = br.readLine(); //接着读下一行  
		    } 
		    
		    String[] c = tr.split(", ");
		    lsh.add(Integer.valueOf((c[0].substring(1))));
		    for(int i=1;i<c.length-1;i++){
		    	int g =Integer.valueOf(c[i]);
		    	 lsh.add(g);
		    }
		    lsh.add(Integer.valueOf((c[c.length-1].substring(0, c[c.length-1].length()-1))));
		 
		 
		    System.out.println("请输入用户身份:");
		    Scanner sc = new Scanner(System.in);
		    String ID = sc.nextLine();
		    
		    System.out.println("开始生成数据拥有性证据");
		    //生成数据拥有证据
		
		 
		 LinkedHashMap<Integer,Element> map = new LinkedHashMap<Integer,Element>();
		 Object[] ob =lsh.toArray();
			for(int i=0;i<lsh.size();i++){
				map.put((Integer) ob[i],(z.pow(new BigInteger(ob[i]+""))).getImmutable());
			}
		
	    Element Y1 = pairing.getG1().newZeroElement();
		Element Y2 = pairing.getZr().newZeroElement();
		Element Y3 = pairing.getZr().newZeroElement();
		Element Y4 = pairing.getZr().newZeroElement();
		Element Y5 = null;
		
		for(int i=0;i<ob.length;i++){
	    	
			//文件块标签地址
			s = "C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
					"Desktop"  + File.separator +"fg"+ File.separator + "FileTagSplite" + File.separator+"partTag_"+ob[i];
	    	//文件标签地址
	    	String e = "C:" + File.separator + "Users" + File.separator + "qiankun" + File.separator + 
	    			"Desktop"  + File.separator+"fg"+ File.separator + "FileSplite" + File.separator+"part_" +ob[i];
			
	    	f = new File(s);
			in = new FileInputStream(f);
		    b = new byte[(int) f.length()];
			in.read(b);
			in.close();
			
			Element M1=pairing.getG1().newRandomElement();
		    M1.setFromBytes(b);
		    M1.getImmutable();
		    Y1=M1.powZn(map.get(ob[i])).add(Y1);
		    
		    Element[] t = new Element[2];
		    t=MapToHash.Hash(e, ID, Y[2], ob[i]+""); 
		    Element we= map.get(ob[i]).mul(t[0]);
		    Y2=we.add(Y2);
		    
		    f = new File(e);
		    in = new FileInputStream(f);
		    b = new byte[(int) f.length()];
		    in.read(b);
			in.close();
			
			Element m1 = pairing.getZr().newRandomElement();
			m1.setFromBytes(b);
			Element m=m1.getImmutable();
			Y3=map.get(ob[i]).mul(m).add(Y3);
			
			Y4=map.get(ob[i]).add(Y4);
			
		}
		
		Element o= pairing.getZr().newRandomElement().getImmutable();
		Y3=Y3.add(o);
		
		Y1.getImmutable();
		Y2.getImmutable();
		Y3.getImmutable();
		
		Y4=Y[2].powZn(Y4);
		
		Y5=pairing.pairing(Y[1], Y[0]).powZn(o);
		Y5.getImmutable();
	
		Element U2 = Y2.getImmutable();
		Element U3 = Y3.getImmutable();
		Element U4 = Y4.getImmutable();
		Element U5 = Y5.getImmutable();
		
		Element W1=U5.mul(pairing.pairing(Y1, Y[0])).getImmutable();
		
		String[] fl = {"U2.txt","U3.txt","U4.txt"};
		Element[] K = {U2,U3,U4};
	    OutputStream  bw = null;
	    
	    for(int i=0;i<3;i++){
	    	
	    	bw = new FileOutputStream(fl[i]);
	    	bw.write(K[i].toBytes());
	    	bw.close();}
	    
	    
	    OutputStreamWriter bw1 = null;
		bw1 = new OutputStreamWriter(new FileOutputStream("W1.txt"));
		bw1.write(W1.toString());
		bw1.flush();
		bw1.close();
		
		
		System.out.println("数据拥有证明信息生成完成，且将发送到用户的信息文件写在”W1.txt”中，将发送给审计者的信息分别写在”U2.txt,U3.txt,U4.txt”文件中");
		System.out.println("W1="+W1);
		
	}
	
}
