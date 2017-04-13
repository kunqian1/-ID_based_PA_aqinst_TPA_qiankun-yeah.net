package setup;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Scanner;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Pairing;
import it.unisa.dia.gas.jpbc.PairingParameters;
import it.unisa.dia.gas.plaf.jpbc.pairing.PairingFactory;
import it.unisa.dia.gas.plaf.jpbc.pairing.a.TypeACurveGenerator;

//PKG�˴���

public class Param {
	
	
	public static void main(String[] args) throws Exception{
		
		//��Բ�������ɹ��̡�
		System.out.println("�����밲ȫ������");
		Scanner sc = new Scanner(System.in);
		
		int r = sc.nextInt();
		int q = sc.nextInt();
	    
		TypeACurveGenerator pg = new TypeACurveGenerator(r, q);
		PairingParameters typeAParams = pg.generate();
		Pairing pairing= PairingFactory.getPairing(typeAParams);
		
		OutputStream  bw = null;
		byte[] b =null;
		
		//�����ɵ���Բ˫����д�� Ecc.properties�ļ��С�
		String s1 = "Ecc.properties";
		bw = new FileOutputStream(s1);
		bw.write(typeAParams.toString().getBytes());
		bw.close();
		
		System.out.println("��Բ˫����������ɣ��ѽ����ɵ���Բ˫���߲���д��Ecc.properties�ļ���");
		
		
		//����ϵͳ��˽Կ������д��MasterKey.txt�ļ��С�
		System.out.println("������˽Կ��ʼ");
		Element mk = pairing.getZr().newRandomElement().getImmutable();
		String s2 = "MasterKey.txt";
		bw = new FileOutputStream(s2);
		b = mk.toBytes();
		bw.write(b);
		bw.close();
		System.out.println("������˽Կ�������ѽ���˽Կд��MasterKey.txt�ļ���");
		
		//����ϵͳ����P��Q��Pub���ҽ�P��Q�ֱ�д��P.txt��Q.txt��Pub.txt�ļ���
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
	   
		System.out.println("����������Կ��������ҷֱ�д��P.txt��Q.txt��Pub.txt�ļ���");	
		
		//�����û���˽ԿSK1��SK2�����ɺ�ֱ�д��SK1.txt��SK2.txt�ļ��У�Ϊ����������������ǽ�����ݵĶ�����ϣH1��H2Ҳд���ļ���
		System.out.println("Ϊ�û�����˽Կ��ʼ");
		System.out.println("�������û�����ݣ�");
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
		System.out.println("�û���˽Կ������ɣ��ѽ��û�˽Կ�ֱ�д��SK1.txt��SK2.txt�У����û���ݵĹ�ϣ�ֱ�д��H1.txt��H2.txt�ļ���");
		
		//��ӡ��Բ˫���ߣ�����P,Q���û�˽Կ��Ϣ
		System.out.println("��Բ˫������Ϣ��");
		System.out.println(typeAParams.toString());
		System.out.println("��˽Կ��Ϣ��");
		System.out.println(mk);
		System.out.println("�����Ĳ�����Ϣ��");
		System.out.println("P="+P);
		System.out.println("Q="+Q);
		System.out.println("Pub="+Pub);
		System.out.println("H1="+H1);
		System.out.println("H2="+H2);
		System.out.println("�û�˽Կ��Ϣ��");
		System.out.println("SK1="+SK1);
		System.out.println("SK2="+SK2);
		
		System.out.println(pairing.pairing(Q, P));
		
	}

}
