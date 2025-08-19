package com.pahana.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailUtil {
    public static class Config {
        public final String host;
        public final int port;
        public final boolean tls;
        public final boolean ssl;
        public final String username;
        public final String password;
        public final String from;

        public Config(String host, int port, boolean tls, boolean ssl, String username, String password, String from) {
            this.host = host;
            this.port = port;
            this.tls = tls;
            this.ssl = ssl;
            this.username = username;
            this.password = password;
            this.from = from;
        }
    }

    public static Config loadFromEnv() {
        String host = getenvOr("MAIL_HOST", "smtp.gmail.com");
        int port = Integer.parseInt(getenvOr("MAIL_PORT", "587"));
        boolean tls = Boolean.parseBoolean(getenvOr("MAIL_TLS", "true"));
        boolean ssl = Boolean.parseBoolean(getenvOr("MAIL_SSL", "false"));
        String user = getenvOr("MAIL_USER", "").trim();
        String pass = getenvOr("MAIL_PASS", "");
        String from = getenvOr("MAIL_FROM", user).trim();
        if (from.isEmpty()) {
            from = "no-reply@pahanaedu.local";
        }
        return new Config(host, port, tls, ssl, user, pass, from);
    }

    private static String getenvOr(String key, String def) {
        String v = System.getenv(key);
        return v != null && !v.isEmpty() ? v : def;
    }

    public static void sendHtml(Config cfg, String to, String subject, String html) throws MessagingException {
        InternetAddress fromAddr = new InternetAddress(cfg.from, true);
        InternetAddress[] toAddrs = InternetAddress.parse(to, true);

        Properties props = new Properties();
        props.put("mail.smtp.auth", String.valueOf(cfg.username != null && !cfg.username.isEmpty()));
        props.put("mail.smtp.starttls.enable", String.valueOf(cfg.tls));
        props.put("mail.smtp.host", cfg.host);
        props.put("mail.smtp.port", String.valueOf(cfg.port));
        if (cfg.ssl) {
            props.put("mail.smtp.socketFactory.port", String.valueOf(cfg.port));
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                if (cfg.username == null || cfg.username.isEmpty())
                    return null;
                return new PasswordAuthentication(cfg.username, cfg.password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(fromAddr);
        message.setRecipients(Message.RecipientType.TO, toAddrs);
        message.setSubject(subject);
        message.setContent(html, "text/html; charset=UTF-8");
        Transport.send(message);
    }
}
