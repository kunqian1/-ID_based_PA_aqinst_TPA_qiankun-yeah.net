package verifying;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Scanner;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;


public class Chal {
	
	public static Pairing pairing = PairingFactory.getPairing("Ecc.properties");
	
	public static LinkedHashSet<Integer> generateChal1(int a,int n) throws IOException {
		
		LinkedHashSet<Integer> lhs =new LinkedHashSet<Integer>();
		Random ne = new Random();
		while(lhs.size()<a){
			int  Z =ne.nextInt(n)+1;
			lhs.add(Z);
		}
		
		OutputStreamWriter bw = null;
		bw = new OutputStreamWriter(new FileOutputStream("I.txt"));
		bw.write(lhs.toString());
		bw.flush();
		bw.close();
		
		return lhs;
		
	}
	
	public static Element generateChal2() throws IOException {
		
		
		Element z = pairing.getZr().newRandomElement();
		
		OutputStream  bw = null;
		bw = new FileOutputStream("z.txt");
		bw.write(z.toBytes());
		bw.close();
		
		return z;
		
	}
	
	public static void main(String[] args) throws IOException {
		
		Scanner in = new Scanner(System.in);
		System.out.println("请输入文件分块的大小和要验证的数据块数目：");
		int a = in.nextInt();
		int b = in.nextInt();
		
		System.out.println("生成挑战信息开始");
		generateChal1(b,a);
		generateChal2();
		System.out.println("生成挑战信息结束,挑战信息分别写在I.txt和z.txt文件中");
		
		
	}
	
}
