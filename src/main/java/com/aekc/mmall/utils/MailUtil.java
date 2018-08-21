package com.aekc.mmall.utils;

import com.aekc.mmall.beans.Mail;
import com.aekc.mmall.exception.CustomException;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailUtil.class);

    public static boolean send(Mail mail) {

        String from = "";
        int port = 25;
        String host = "";
        String pass = "";
        String nickname = "";

        HtmlEmail email = new HtmlEmail();
        try {
            email.setHostName(host);
            email.setCharset("UTF-8");
            for(String str : mail.getReceivers()) {
                email.addTo(str);
            }
            email.setFrom(from, nickname);
            email.setSmtpPort(port);
            email.setAuthentication(from, pass);
            email.setSubject(email.getSubject());
            email.setMsg(mail.getMessage());
            email.send();
            LOGGER.info("{} 发送邮件到 {}", from, StringUtils.join(mail.getReceivers(), ','));
            return true;
        } catch (EmailException e) {
            LOGGER.error(from + "发送邮件到" + StringUtils.join(mail.getReceivers(), ',') + "失败", e);
            return false;
        }
    }
}
