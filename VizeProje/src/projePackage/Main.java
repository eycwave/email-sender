package projePackage;
// Kullanılan IDE package adresi belirtilmesini istiyorsa package adresi yazılmalı.

// Gerekli kütüphaneler eklenmeli.
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
/* "javax.mail.jar" & "activation-1.1.1.jar" dosyalarını Build etmek gerekiyor.
 	Aksi takdirde mail gönderme işlemi gerçekleştirilemez.
 	Proje dosyası içerisinde "lib" dosyasında ilgili .jar dosyaları mevcut. */

class MailGonderici {

	private static final String HOST = "smtp.gmail.com";
	private static final String PORT = "587";
	private static final String GONDEREN_MAIL = // "MAIL_@ADRESI"
	private static final String SIFRE = // "SMTP_Şifresi"
										
	// Göndericinin mail hesabından SMTP ayarlarının açılması gerekiyor.
	// Gonderen_MAIL ve SIFRE bölümlerindeki bilgileri değiştirerek gönderici hesabını değiştirebilirsiniz.

	protected static void mailGonder(String[] alicilar, String konu, String mesaj) {

		// Mail gönderme fonksiyonu belirtilmiş olan alıcılara, belirlenen konuyu ve mesajı iletir.

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", HOST);
		props.put("mail.smtp.port", PORT);

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(GONDEREN_MAIL, SIFRE);
			}
		});

		try {

			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(GONDEREN_MAIL));
			List<InternetAddress> toAddresses = new ArrayList<InternetAddress>();
			for (String recipient : alicilar) {
				toAddresses.add(new InternetAddress(recipient));
			}
			msg.setRecipients(Message.RecipientType.TO, toAddresses.toArray(new InternetAddress[0]));
			msg.setSubject(konu);
			msg.setText(mesaj);
			Transport.send(msg);

		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

}

class MailIslemleri extends MailGonderici {

	private static boolean mailKontrol(String email) {
		
		/*
		 * Eğer geçerli mail adresi girilmemiş ise mail gönderimi yapan hesaba, mesaj
		 * göndermeye çalıştığı mail adresinin geçersiz olduğuna dair mail gelir.
		 */
	
		String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		Pattern pattern = Pattern.compile(emailRegex);
		return pattern.matcher(email).matches();	
	}

	private static void mailAliciIslemleri(List<String> baslikListe, String konu, String mesaj) {

		List<String> alicilarList = new ArrayList<>();

		// Belirtilen başlığa sahip liste içerisindeki 'mail adresleri' ayrı bir listeye eklenir.
		for (String kelime : baslikListe) {
			String[] kelimeler = kelime.split("	");
			for (String k : kelimeler) {
				if (mailKontrol(k)) {
					alicilarList.add(k);
				}
			}
		}

		// Mail adreslerinin kontrolü sonrasında liste boş kalır ise uyarı verir.
		if (alicilarList.isEmpty()) {
			System.out.println("Geçerli e-posta adresi bulunmamaktadır!");
		}

		// Mail adreslerinin tutulduğu liste Array formatına çevirilip alicilar dizisinde tutulur.
		String[] alicilar = alicilarList.toArray(new String[alicilarList.size()]);

		// Kalıtım sayesinde doğrudan mailGonder fonksiyonu çalışır.
		mailGonder(alicilar, konu, mesaj);

	}

	protected static void switchIslemleri(List<String> elitUyelerList, List<String> genelUyelerList) {

		System.out.println("1 - Elit üyelere mail gönderme");
		System.out.println("2 - Genel üyelere mail gönderme");
		System.out.println("3 - Tüm üyelere mail gönderme");

		Scanner scanner = new Scanner(System.in);
		int secim = scanner.nextInt();
		String konu, mesaj;
		scanner.nextLine();
		// Kullanıcının yaptığı seçime bağlı mailAliciIslemleri fonksiyonuna ilgili bilgiler gönderilir.
		switch (secim) {
		case 1:
			if (elitUyelerList.size() == 0) {
				System.out.println("Elit Üye bulunmamakta!");
				break;
			} else {
				System.out.println("Konu giriniz:");
				konu = scanner.nextLine();
				System.out.println("Mesaj giriniz:");
				mesaj = scanner.nextLine();
				System.out.println("Biraz bekleyiniz...\n");
				mailAliciIslemleri(elitUyelerList, konu, mesaj);
				System.out.println("Elit üyelere Mail Gönderildi!");
				break;
			}

		case 2:
			if (genelUyelerList.size() == 0) {
				System.out.println("Genel Üye bulunmamakta!");
				break;
			} else {
				System.out.println("Konu giriniz:");
				konu = scanner.nextLine();
				System.out.println("Mesaj giriniz:");
				mesaj = scanner.nextLine();
				System.out.println("Biraz bekleyiniz...\n");
				mailAliciIslemleri(genelUyelerList, konu, mesaj);
				System.out.println("Genel üyelere Mail Gönderildi!");
				break;
			}

		case 3:
			if (elitUyelerList.size() == 0 && genelUyelerList.size() == 0) {
				System.out.println("Hiç üye bulunmamakta!");
				break;
			} else {
				System.out.println("Konu giriniz:");
				konu = scanner.nextLine();
				System.out.println("Mesaj giriniz:");
				mesaj = scanner.nextLine();
				System.out.println("Biraz bekleyiniz...\n");
				if(elitUyelerList.size() == 0) {
					mailAliciIslemleri(genelUyelerList, konu, mesaj);
				}
				else if(genelUyelerList.size() == 0) {
					mailAliciIslemleri(elitUyelerList, konu, mesaj);
				}
				else {
					mailAliciIslemleri(elitUyelerList, konu, mesaj);
					mailAliciIslemleri(genelUyelerList, konu, mesaj);					
				}
				System.out.println("Tüm üyelere Mail Gönderildi!");
				break;
			}
		}
		scanner.close();
	}

}

class DosyaIslemleri {

	protected static void dosyaOlustur(String dosyaAdi, String elitBaslik, String genelBaslik) {

		/*
		 * Dosya oluşturma işlemi , dosyanın varlığını kontrol eder, dosya mevcut
		 * değilse dosyayı oluşturup başlıkları ekler.
		 * NOT: Dosya, projenin bulunduğu dosya konumunda oluşur.
		 */

		File file = new File(dosyaAdi);
		if (!file.exists()) {
			try {
				// Dosya oluşturulur.
				file.createNewFile();
				// Dosyaya başlık yazılır.
				FileWriter writer = new FileWriter(file);
				writer.write(elitBaslik + "\n\n\n");
				writer.write(genelBaslik + "\n");
				writer.close();
			} catch (IOException e) {
				System.out.println("Dosya oluşturulurken hata oluştu.");
				e.printStackTrace();
			}
		}
	}

	protected static List<String> dosyadanOku(String dosyaAdi, String baslik) {

		// Dosya okuma işlemi, ilgili başlıkları okur ardından başlıkların altındaki bilgileri listeye ekler.

		List<String> uyeListesi = new ArrayList<>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dosyaAdi));
			String line;
			boolean baslikOkundu = false;

			while ((line = reader.readLine()) != null) {
				if (line.equals(baslik)) {
					baslikOkundu = true;
					continue;
				}
				if (baslikOkundu) {
					if (line.isEmpty()) {
						break;
					}
					uyeListesi.add(line);
				}
			}
			reader.close();
		} catch (IOException e) {
			System.out.println("Dosya okunurken hata oluştu.");
			e.printStackTrace();
		}
		return uyeListesi;
	}

	protected static void dosyayaYaz(String dosyaAdi, String baslik, String uyeAdi, String uyeSoyadi, String uyeEmail) {

		 // Dosyaya yazma işlemi, kullanıcının ilgili başlığa eklemiş olduğu bilgileri başlığın altına yazar.
		 
		try {
			BufferedReader reader = new BufferedReader(new FileReader(dosyaAdi));
			List<String> dosyaIcerigi = new ArrayList<>();
			String line;

			while ((line = reader.readLine()) != null) {
				dosyaIcerigi.add(line);
				if (line.equals(baslik)) {
					dosyaIcerigi.add(uyeAdi + "\t" + uyeSoyadi + "\t" + uyeEmail);
				}
			}

			reader.close();

			BufferedWriter writer = new BufferedWriter(new FileWriter(dosyaAdi));
			for (String content : dosyaIcerigi) {
				writer.write(content + "\n");
			}
			writer.close();

		} catch (IOException e) {
			System.out.println("Dosya işlemleri sırasında bir hata oluştu.");
			e.printStackTrace();
		}
	}

}

public class Main extends DosyaIslemleri {

	private static final String FILE_NAME = "kullanicilar.txt";
	private static final String ELIT_UYELER = "ELİT ÜYELER";
	private static final String GENEL_UYELER = "GENEL ÜYELER";
	private static List<String> elitUyelerList;
	private static List<String> genelUyelerList;

	public static void main(String[] args) {

		// Dosya oluşturma fonksiyonu kalıtım ile çağırılır.
		dosyaOlustur(FILE_NAME, ELIT_UYELER, GENEL_UYELER);

		// Dosyadan oku fonksiyonu kalıtım ile çağırılır ve Elit/Genel üye listelerini oluşturur.
		elitUyelerList = dosyadanOku(FILE_NAME, ELIT_UYELER);
		genelUyelerList = dosyadanOku(FILE_NAME, GENEL_UYELER);

		Scanner scanner = new Scanner(System.in);

		System.out.println("Lütfen bir seçim yapınız:");
		System.out.println("1 - Elit üye ekleme");
		System.out.println("2 - Genel üye ekleme");
		System.out.println("3 - Mail gönderme");
		System.out.println("4-  Üye listesi görüntüleme");
		System.out.println("0 - Çıkış");

		int secim = scanner.nextInt();

		// Kullanıcının yaptığı seçime bağlı ilgili fonksiyonlar çalışır.
		switch (secim) {
		case 1:
			elitUyeEkle();
			break;
		case 2:
			genelUyeEkle();
			break;
		case 3:
			MailIslemleri.switchIslemleri(elitUyelerList, genelUyelerList);
			break;
		case 4:
			listeGoruntule(ELIT_UYELER, elitUyelerList);
			listeGoruntule(GENEL_UYELER, genelUyelerList);
			break;
		case 0:
			System.out.println("Program sonlandırılıyor.");
			break;
		default:
			System.out.println("Geçersiz seçim. Lütfen tekrar deneyin.");
			break;
		}
		scanner.close();
	}

	private static void elitUyeEkle() {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Lütfen elit üyenin adını giriniz: ");
		String uyeAdi = scanner.nextLine();
		System.out.println("Lütfen elit üyenin soyadını giriniz: ");
		String uyeSoyadi = scanner.nextLine();
		System.out.println("Lütfen elit üyenin e-posta adresini giriniz: ");
		String uyeEmail = scanner.nextLine();

		scanner.close();

		// Kalıtım sayesinde doğrudan dosyayaYaz fonksiyonu çalışır.
		dosyayaYaz(FILE_NAME, ELIT_UYELER, uyeAdi, uyeSoyadi, uyeEmail);

		System.out.println("Elit üye başarıyla eklendi.");

	}

	private static void genelUyeEkle() {

		Scanner scanner = new Scanner(System.in);
		System.out.println("Lütfen genel üyenin adını giriniz: ");
		String uyeAdi = scanner.nextLine();
		System.out.println("Lütfen genel üyenin soyadını giriniz: ");
		String uyeSoyadi = scanner.nextLine();
		System.out.println("Lütfen genel üyenin e-posta adresini giriniz: ");
		String uyeEmail = scanner.nextLine();

		scanner.close();

		// Kalıtım sayesinde doğrudan dosyayaYaz fonksiyonu çalışır.
		dosyayaYaz(FILE_NAME, GENEL_UYELER, uyeAdi, uyeSoyadi, uyeEmail);

		System.out.println("Genel üye başarıyla eklendi.");
	}

	private static void listeGoruntule(String Baslik, List<String> Liste) {

		System.out.println(Baslik + " - AD SOYAD MAIL: " + Liste);

	}
}
