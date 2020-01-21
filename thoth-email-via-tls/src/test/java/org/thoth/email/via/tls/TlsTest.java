package org.thoth.email.via.tls;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TlsTest {

    public TlsTest() {
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

        // html part
        {
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("<html><body>"
                + "<p>Time: "+now+"</p>"
                + "<p>From: "+hostname+"</p>"
                + "</body></html>"
                , "UTF8", "html");
            content.addBodyPart(textPart);
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
