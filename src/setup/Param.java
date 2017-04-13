package setup;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Scanner;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

//PKG端代码

public class Param {
	
	
	public static void main(String[] args) throws Exception{
		
		//椭圆曲线生成过程。
		System.out.println("请输入安全参数：");
		Scanner sc = new Scanner(System.in);
		
		int r = sc.nextInt();
		int q = sc.nextInt();
	    
		TypeACurveGenerator pg = new TypeACurveGenerator(r, q);
		PairingParameters typeAParams = pg.generate();
		Pairing pairing= PairingFactory.getPairing(typeAParams);
		
		OutputStream  bw = null;
		byte[] b =null;
		
		//将生成的椭圆双曲线写在 Ecc.properties文件中。
		String s1 = "Ecc.properties";
		bw = new FileOutputStream(s1);
		bw.write(typeAParams.toString().getBytes());
		bw.close();
		
		System.out.println("椭圆双曲线生成完成，已将生成的椭圆双曲线参数写在Ecc.properties文件中");
		
		
		//产生系统主私钥，将其写在MasterKey.txt文件中。
		System.out.println("生成主私钥开始");
		Element mk = pairing.getZr().newRandomElement().getImmutable();
		String s2 = "MasterKey.txt";
		bw = new FileOutputStream(s2);
		b = mk.toBytes();
		bw.write(b);
		bw.close();
		System.out.println("生成主私钥完整，已将主私钥写在MasterKey.txt文件中");
		
		//产生系统参数P，Q和Pub并且将P，Q分别写在P.txt，Q.txt和Pub.txt文件中
		Element P = pairing.getG1().newRandomElement().getImmutable();
		Element Q = pairing.getG1().newRandomElement().getImmutable();
		Element Pub = P.powZn(mk).getImmutable();
		
		String s3 = "P.txt";
		String s4 ="Q.txt";
		String s = "Pub.txt";
		bw = new FileOutputStream(s3);
		b=P.toBytes();
		bw.write(b);
		bw.close();

		bw=new FileOutputStream(s4);
		b=Q.toBytes();
		bw.write(b);
		bw.close();
		
		bw=new FileOutputStream(s);
		b=Pub.toBytes();
		bw.write(b);
		bw.close();
	   
		System.out.println("参数和主公钥生成完成且分别写在P.txt，Q.txt和Pub.txt文件中");	
		
		//生成用户的私钥SK1和SK2，生成后分别写在SK1.txt和SK2.txt文件中，为后续方便操作，我们将对身份的二个哈希H1和H2也写下文件中
		System.out.println("为用户生成私钥开始");
		System.out.println("请输入用户的身份：");
		String ID = sc.next();
		byte[] bt1=(ID+"0").getBytes();
	    byte[] bt2=(ID+"1").getBytes();
		
	    Element H1= pairing.getG1().newElement().setFromHash(bt1, 0, bt1.length).getImmutable();
	    Element H2= pairing.getG1().newRandomElement().setFromHash(bt2, 0, bt1.length).getImmutable();
	    Element SK1= H1.powZn(mk).getImmutable();
	    Element SK2= H2.powZn(mk).getImmutable();

	    String s5 = "H1.txt";
		String s6 ="H2.txt";
		String s7 = "SK1.txt";
		String s8 ="SK2.txt";
		
		bw=new FileOutputStream(s5);
		b=H1.toBytes();
		bw.write(b);
		bw.close();
		
		bw=new FileOutputStream(s6);
		b=H2.toBytes();
		bw.write(b);
		bw.close();
		
		bw=new FileOutputStream(s7);
		b=SK1.toBytes();
		bw.write(b);
		bw.close();
		
		bw=new FileOutputStream(s8);
		b=SK2.toBytes();
		bw.write(b);
		bw.close();
		System.out.println("用户的私钥生成完成，已将用户私钥分别写在SK1.txt和SK2.txt中，对用户身份的哈希分别写在H1.txt和H2.txt文件中");
		
		//打印椭圆双曲线，参数P,Q和用户私钥信息
		System.out.println("椭圆双曲线信息：");
		System.out.println(typeAParams.toString());
		System.out.println("主私钥信息：");
		System.out.println(mk);
		System.out.println("公开的参数信息：");
		System.out.println("P="+P);
		System.out.println("Q="+Q);
		System.out.println("Pub="+Pub);
		System.out.println("H1="+H1);
		System.out.println("H2="+H2);
		System.out.println("用户私钥信息：");
		System.out.println("SK1="+SK1);
		System.out.println("SK2="+SK2);
		
		System.out.println(pairing.pairing(Q, P));
		
	}

}
