package it.tiw.util;

import it.tiw.beans.Studente;

import java.util.List;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.*;

public class Util {

    public Util() {}

    public static int countStatoValutazioneValues(List<Studente> iscrittiPerAppello, String state){
        int count = 0;
        for(Studente studente : iscrittiPerAppello){
            if(studente.getStato_valutazione().equalsIgnoreCase(state)){
                count+=1;
            }
        }
        return count;
    }
    public static void sendEmail(String recepient) {
        final String fromEmail = "ish993956@gmail.com";
        final String toEmail = recepient;
        final String appPassword = "nubn fwsm vqco pwcy";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("New Grades Have Been Published");
            message.setContent(
                    "Dear Student, new grades have been published.!<br>" +
                            "<a href='http://localhost:8080/tiw/'>Click here to open the portal</a>",
                    "text/html"
            );


            Transport.send(message);
            System.out.println("✅ Email sent successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
