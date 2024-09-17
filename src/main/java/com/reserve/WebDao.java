package com.reserve;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.reserve.WebController.*;
import com.reserve.WebController.Reserve;
import com.reserve.WebController.ReserveDate;

@Service
public class WebDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    WebDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //    予約をDBに登録
    public void add(Reserve reserve) {
        String sql = "INSERT INTO reserve(id, date, time, menu, name, email, tel) VALUES(?, ?, ?, ?, ?, ?, ?)";
        int number = jdbcTemplate.update(sql, reserve.id(), reserve.date(), reserve.time(), reserve.menu(), reserve.name(),
                reserve.email(), reserve.tel());
    }

    public List<ReserveDate> search(LocalDate date) {
        String sql = "SELECT * FROM RESERVE WHERE date = ?";
        List<ReserveDate> reserveDates = jdbcTemplate.query(sql,new DataClassRowMapper<>(ReserveDate.class),date);
        return reserveDates;
    }

    public Reserve idFind(String id) {
        String sql = "SELECT * FROM reserve WHERE id = ?";
        try {
            Reserve reserve = jdbcTemplate.queryForObject(sql,new DataClassRowMapper<>(Reserve.class),id);
            return reserve;
        } catch (IncorrectResultSizeDataAccessException e) {
            return null;
        }
    }


    //DBから予約情報の削除
    public void cancel(String id) {
        String sql = "DELETE FROM reserve WHERE id = ?";
        int delete = jdbcTemplate.update(sql, id);
    }
}
