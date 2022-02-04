package com.example.clock;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@RunWith(AndroidJUnit4.class)
public class LocalDateTimeTransformationTest {
    @Test
    public void localTimeAlgorithmCorrectnessTest(){
        // 21.01.2022 10:00 UTC
        String expectedResult = "21.01.2022 10:00";
        long baseUTCTime = 1642759200000L;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime ldt = LocalDateTime.ofEpochSecond(baseUTCTime / 1000L, 0, ZoneOffset.UTC);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());

        long baseLocalTime = zdt.toEpochSecond() * 1000L;
        String finalResult = zdt.format(dtf);
        assertThat(expectedResult, equalTo(finalResult));
        Assert.assertNotEquals(baseUTCTime, baseLocalTime);
    }

    public long getLocalMillis(long UTCMillis){
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        LocalDateTime ldt = LocalDateTime.ofEpochSecond(UTCMillis / 1000L, 0, ZoneOffset.UTC);
        ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());

        return zdt.toEpochSecond() * 1000L;
    }


}
