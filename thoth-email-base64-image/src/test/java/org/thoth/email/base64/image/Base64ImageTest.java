package org.thoth.email.base64.image;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Base64ImageTest {

    public Base64ImageTest() {
    }

    protected String now, hostname;

    protected Properties outlook;

    @BeforeEach
    public void setUp() throws Exception {
        now = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a").format(new Date());
        hostname = InetAddress.getLocalHost().getHostName();
        outlook = new Properties();
        outlook.load(this.getClass().getResourceAsStream("/smtp-tls-pepipost.properties"));
    }

    @Test
    public void a_test() throws Exception {

        // Create MimeMultipart
        MimeMultipart content = new MimeMultipart("related");

        // Create cid for image reference
        String cid = UUID.randomUUID().toString();

        // html part
        {
            MimeBodyPart textPart = new MimeBodyPart();
            StringBuilder sp = new StringBuilder();
            {
                sp.append("<html>");
                {
                    sp.append("<body>");
                    {
                        sp.append("<p>Time: "+now+"</p>");
                        sp.append("<p>From: "+hostname+"</p>");
                        sp.append("<p>Wheel of Time</p>");
                        sp.append("<p><img style=\"border:0px solid blueviolet; vertical-align: bottom;\" alt=\"WOT Logo\" src=\"data:image/jpeg;base64,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\"/></p>");
                    }
                    sp.append("</body>");
                }
                sp.append("</html>");

            }
            textPart.setText(sp.toString(), "UTF8", "html");
            content.addBodyPart(textPart);
        }

        // image part
        {
            MimeBodyPart imagePart = new MimeBodyPart();
            ByteArrayDataSource ds =
                new ByteArrayDataSource(getClass().getResourceAsStream("/kosh.jpg"), "image/jpeg");
            imagePart.setDataHandler(new DataHandler(ds));
            imagePart.setFileName("Kosh.jpg");
            imagePart.setContentID("<" + cid + ">");
            imagePart.setDisposition(MimeBodyPart.INLINE);

            content.addBodyPart(imagePart);
        }

        // properties
        Properties props = new Properties();
        {
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.smtp.host", outlook.getProperty("host"));
            props.setProperty("mail.smtp.port", outlook.getProperty("port"));
            props.setProperty("mail.smtp.starttls.enable", "true");

        }

        Session smtp = null;
        {
            smtp = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                          outlook.getProperty("username")
                        , outlook.getProperty("password")
                    );
                }
            });
            smtp.setDebug(true);
            smtp.setDebugOut(System.out);
        }


        MimeMessage m = new MimeMessage(smtp);
        {
            m.setRecipient(Message.RecipientType.TO, new InternetAddress(outlook.getProperty("to")));
            m.setSubject("thoth-email TLS test " + now);

            InternetAddress from = null;
            {
                from = new InternetAddress(outlook.getProperty("from"));
                from.setPersonal("Thoth Email B64");
                m.setFrom(from);
            }

            InternetAddress reply = null;
            {
                reply = new InternetAddress(outlook.getProperty("reply"));
                m.setReplyTo(new InternetAddress[] {reply});
            }


            m.setContent(content);
        }

        Transport.send(m);
    }

}
