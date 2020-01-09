package org.thoth.email.embedded.image;

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

public class EmbeddedImageTest {

    public EmbeddedImageTest() {
    }

    protected String now, hostname;

    protected Properties outlook;

    @BeforeEach
    public void setUp() throws Exception {
        now = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a").format(new Date());
        hostname = InetAddress.getLocalHost().getHostName();
        outlook = new Properties();
        outlook.load(this.getClass().getResourceAsStream("/smtp-tls-outlook.properties"));
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
                        sp.append("<h1>Metadata</h1>");
                        sp.append("<p>Time: "+now+"</p>");
                        sp.append("<p>From: "+hostname+"</p>");

                        sp.append("<h1>Image</h1>");
                        sp.append("<p>And so it begins!</p>");
                        sp.append("<p><img src=\"cid:" + cid + "\" /></p>");
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
                from.setPersonal("Thoth Email");
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
