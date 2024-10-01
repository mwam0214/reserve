package com.reserve;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// import java.time.DayOfWeek;
// import java.util.HashMap;
// import java.util.Map;

import org.springframework.mail.MailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class WebController {
    private final WebDao dao;
    private final MailSender mailSender;

  

    WebController(WebDao dao, MailSender mailSender) {
        this.dao = dao;
        this.mailSender = mailSender;
    }

    //////グローバル変数///////////////////////////////////////////////////////////////////////////
    LocalDate localDate = LocalDate.now();
    //        予約可能なのは当日より2日後の日付から
    LocalDate reserveDays = localDate.plusDays(2);

    //    Formで日付を取得し、date.htmlに遷移させる。
    LocalDate date;
    LocalTime[] reserveTimeList = {
            LocalTime.of(10, 0),
            // LocalTime.of(10, 30),
            LocalTime.of(11, 0),
            // LocalTime.of(11, 30),
            LocalTime.of(12, 0),
            // LocalTime.of(12, 30),
            LocalTime.of(13, 0),
            // LocalTime.of(13, 30),
            LocalTime.of(14, 0),
            // LocalTime.of(14, 30),
            LocalTime.of(15, 0),
            // LocalTime.of(15, 30),
            LocalTime.of(16, 0),
            // LocalTime.of(16, 30),
            LocalTime.of(17, 0),
            // LocalTime.of(17, 30),
            LocalTime.of(18, 0),
            // LocalTime.of(18, 30),
            LocalTime.of(19, 0),
            // LocalTime.of(19, 30),
            LocalTime.of(20, 0),
            // LocalTime.of(20, 30),
            LocalTime.of(21, 0)
        };


    record ReserveDate(LocalDate date, LocalTime time) {
    }

    List<ReserveDate> reserveDates = new ArrayList<>();
////////////////////////////////////////////////////////////////////////////////////////////

    //    ホーム画面表示
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        model.addAttribute("reserveDays", reserveDays);
        return "home";
    }

    //////予約登録//////////////////////////////////////////////////////////////////////////////
    //    home.htmlで予約したい日付を入力
    @GetMapping("/day")
    public String day(@RequestParam("date") LocalDate date, Model model) {
        this.date = date;
        model.addAttribute("date", date);
        reserveDates = dao.search(date);
        model.addAttribute("circle", reserveDates);
        model.addAttribute("timeList", reserveTimeList);

        // 予約済み時間を取得
        List<LocalTime> bookedTimes = new ArrayList<>();
        for (ReserveDate reserveDate : reserveDates) {
            if (reserveDate.date.equals(date)) {
                bookedTimes.add(reserveDate.time);
            }
        }

        // 予約可能な時間をフィルタリング
        List<LocalTime> availableTimes = new ArrayList<>();
        for (LocalTime time : reserveTimeList) {
            if (!bookedTimes.contains(time)) {
                availableTimes.add(time);
            }
        }

        model.addAttribute("availableTimes", availableTimes);
        model.addAttribute("judge", ReservableDate(date));
        return "date";
    }

    record Reserve(String id, LocalDate date, LocalTime time, String menu, String name, String email, String tel) {
    }

    //    予約フォーム
    @PostMapping("/reserve")
    public String reserve(@RequestParam("time") LocalTime time, @RequestParam("menu") String menu,
                          @RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("tel") String tel, Model model) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        Reserve reserve = new Reserve(id, date, time, menu, name, email, tel);
        System.out.println(time);
        dao.add(reserve);
        model.addAttribute("id", id);
        model.addAttribute("date", date);
        model.addAttribute("time", time);
        model.addAttribute("menu", menu);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("tel", tel);
        MailCreate mailCreate = new MailCreate(mailSender);
        mailCreate.reserveMail(reserve);
        return "completion";
    }
    /////////////////////////////////////////////////////////////////////////////////////////////////

    //home.htmlで入力された予約番号（予約の確認、変更、削除に使用）
    String reserveId;

    //　home.htmlから入力された予約番号から予約の情報をDBより検索し出力する
    @GetMapping("/info")
    public String info(@RequestParam("id") String id, Model model) {
        reserveId = id;
        Reserve reserve = dao.idFind(id);
        if (reserve != null) {
            model.addAttribute("reserve", reserve);
            model.addAttribute("name", reserve.name());
            model.addAttribute("reserveDays", reserveDays);
            return "reserveInfo";
        } else {
            System.out.println("reserve = null　メッセージ：データベースに入力された予約番号の情報がありません。");
            return "notReserve";
        }
    }


    public List<String> ReservableDate(LocalDate date) {
        List<LocalTime> judge = new ArrayList<>();
//        ユーザに選択された日付とDBにある日付が同じ時、reserveTimeListと同じ時間がある場合にjudge配列に時間を追加
//        reserveDatesの中身がnullの場合、judgeの中身もnullになる
        for (int i = 0; i < reserveDates.size(); i++) {
            if (date.isEqual(reserveDates.get(i).date)) {
                for (int j = 0; j < reserveTimeList.length; j++) {
                    if (reserveTimeList[j].equals(reserveDates.get(i).time)) {
                        judge.add(reserveDates.get(i).time);
                    }
                }
            }
        }
//        judgeに格納された時間とreserveTimeListに同一の時間があるかを判定
        List<String> judge2 = new ArrayList<>();
        for (int i = 0; i < reserveTimeList.length; i++) {
            if (judge.contains(reserveTimeList[i])) {
                judge2.add("×");
            } else {
                judge2.add("予約可能です");
            }
        }
        for (int i = 0; i < judge2.size(); i++) {
            System.out.println(reserveTimeList[i] + "：" + judge2.get(i));
            System.out.println("------------------------------");
        }
        return judge2;
    }

    //////////////////////////////////////////　予約キャンセル //////////////////////////////////////////
    //    reserveInfo.htmlから予約のキャンセルを処理
    @GetMapping("/reserveDelete")
    public String reserveDelete() {
        dao.cancel(reserveId);
        return "reserveCancel";
    }
}