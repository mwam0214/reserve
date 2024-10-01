package com.reserve;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
public class MailCreate {
        private final MailSender mailSender;

    @Autowired
    public MailCreate(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    // private MailSender mailSender;
    // MailCreate(MailSender mailSender) {
    //     this.mailSender = mailSender;
    // }

    //        予約完了時に予約情報をメールアドレスに送信する
    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

    public void reserveMail(WebController.Reserve reserve) {
        simpleMailMessage.setFrom("aiuuuuuto@gmail.com");
        simpleMailMessage.setTo(reserve.email());
        simpleMailMessage.setSubject("【aiu-to】ご予約完了通知メール");
//        改行コード
        final String LINE_SEPARATOR = System.getProperty("line.separator");
        simpleMailMessage.setText(reserve.name() + " 様" + LINE_SEPARATOR +
                "aiu-toをご利用いただきありがとうございます。" + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "ご予約いただいた内容をメールにて送信いたします。" + LINE_SEPARATOR +
                "■予約番号：" + reserve.id() + LINE_SEPARATOR +
                "■ご予約日時：" + reserve.date() + LINE_SEPARATOR +
                "■ご予約時間：" + reserve.time() + LINE_SEPARATOR +
                "■ご予約メニュー：" + reserve.menu() + LINE_SEPARATOR +
                LINE_SEPARATOR +
                "※予約番号で公式サイトから予約内容の確認も可能です。" + LINE_SEPARATOR +
                "当日お会いできることを楽しみにしております。");
        try {
            mailSender.send(simpleMailMessage);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
