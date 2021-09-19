package com.game.utils.mailUtils;

import  java.util.Properties;

import com.game.utils.logUtils.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import  org.springframework.mail.SimpleMailMessage;
import  org.springframework.mail.javamail.JavaMailSenderImpl;

public class MailUtil {
    //发送验证码的方法,to是目标邮箱地址，text是发送的验证码（事先生成）
    public static boolean sendMail (String to,String text) {
        //LogUtil.print("sendMail...util...");
        LogUtil.print("begin sending Email to #"+to+"#");
        JavaMailSenderImpl senderImpl=new JavaMailSenderImpl();
        SimpleMailMessage mailMessage=new SimpleMailMessage();
        Properties prop=new Properties();

        try{
            //设定mail server
            senderImpl.setHost("smtp.163.com");
            // 可以用数组发送多个邮件
            // String[] array = new String[]    {"sun111@163.com","sun222@sohu.com"};
            // mailMessage.setTo(array);
            mailMessage.setTo(to);//收件人
            mailMessage.setFrom( "Oasis2200@163.com" );//发件人
            mailMessage.setSubject( "绿洲2200 新用户注册" );//主题
            mailMessage.setText("验证码：" + text);//内容

            //邮箱和授权码
            senderImpl.setUsername("Oasis2200@163.com");
            senderImpl.setPassword("GESQEPNAAVRIXZPB");

            prop.put("mail.smtp.auth","true");
            prop.put("mail.smtp.timeout","25000");
            senderImpl.setJavaMailProperties(prop);

            //发送邮件
            senderImpl.send(mailMessage);

            LogUtil.print("Send Mail Success");
            return true;
        }catch (Exception e) {
            LogUtil.print("Send Mail Fail");
            LogUtil.print(e.getMessage());
            return false;
        }
    }

}
