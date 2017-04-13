package verifying;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Scanner;

import hash.MapToHash;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class Verifying {
	
	public static Pairing pairing = PairingFactory.getPairing("Ecc.properties");

	public static void main(String[] args) throws IOException{
		
		Element[] Y = new Element[7];
		String[]  S = {"P.txt","Q.txt","H1.txt","H2.txt","U4.txt","Pub.txt","R.txt"};
		for(int i=0;i<7;i++){
			
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
		
		//得到证明信息1
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
		   
		  //得到证明信息2
		   Element[] D = new Element[3];
		   String[] DD ={"z.txt","U2.txt","U3.txt"};
		   
		   for(int i=0;i<3;i++){
			   
			   File f = new File(DD[i]);
			   InputStream in = null;
			   in = new FileInputStream(f);
	           byte b[] = new byte[(int) f.length()];
               in.read(b);
               
               Element U=pairing.getZr().newRandomElement();
			   U.setFromBytes(b);
			   D[i]=U.getImmutable();
			   
			   in.close();
			   
		   }
		   
		   
		   LinkedHashMap<Integer,Element> map = new LinkedHashMap<Integer,Element>();
		   Object[] ob =lsh.toArray();
		   for(int i=0;i<lsh.size();i++){
			   
			   map.put((Integer) ob[i],(D[0].pow(new BigInteger(ob[i]+""))).getImmutable());}
		   
		   
		   System.out.println("恢复信息完成,请输入用户身份：");
		   Scanner sc =new Scanner(System.in);
		   String ID=sc.nextLine();
		 
		   System.out.println("开始为用户进行计算任务");
		   
		   Element X1= pairing.getZr().newZeroElement();
		   for(int i=0;i<lsh.size();i++){
			   
			   X1= D[0].pow(new BigInteger(ob[i]+"")).mul(MapToHash.Hash(ob[i]+"",ID)).add(X1);}
		   
		   Element T1=null;
		   Element T2=null;
		   Element T3=null;
		   
		   T1=pairing.pairing(Y[1], Y[0]).powZn(D[2]).getImmutable();
		   T3=pairing.pairing(Y[1], Y[4]).getImmutable();
		   T2=pairing.pairing(Y[2].powZn(D[1]).mul(Y[3].powZn(X1)),Y[5]).getImmutable();
		   
		   Element W2=T1.mul(T2).mul(T3);

		 
		
		    OutputStreamWriter bw1 = null;
			bw1 = new OutputStreamWriter(new FileOutputStream("W2.txt"));
			bw1.write(W2.toString());
			bw1.flush();
			bw1.close();
			
			System.out.println("计算完成,已经将计算结果写入到“W2.txt”文件中");
	
	}
}
