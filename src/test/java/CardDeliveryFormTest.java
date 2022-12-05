package ru.netology;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;


public class CardDeliveryFormTest {

    @BeforeEach
    void Setup() {
        open("http://localhost:9999/");
        Configuration.headless = true;
    }

    public String setCurrentDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void shouldSubmitValidData() {
        String date = setCurrentDate(3);
        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Встреча успешно забронирована на " + date));
    }

    @Test
    void shouldSetMsgIfCityIsNotInList() {
        String date = setCurrentDate(3);
        $("[data-test-id=city] input").setValue("Нягань");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=city] .input__sub").shouldHave(exactText("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldSetMsgIfDateNotValid() {
        String date = setCurrentDate(1);
        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=date] .input__sub").shouldHave(exactText("Заказ на выбранную дату невозможен"));
    }

    @Test
    void shouldSetMsgIfDateNonexistent() {
        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue("30.02.2023");
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=date] .input__sub").shouldHave(exactText("Неверно введена дата"));
    }

    @Test
    void shouldSetMsgIfEmptyForm() {
        $(".button").click();
        $("[data-test-id=city] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldSetMsgIfNameEmpty() {
        String date = setCurrentDate(3);
        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $("[data-test-id=name] .input__sub").shouldHave(exactText("Поле обязательно для заполнения"));
    }

    @Test
    void shouldSetMsgIfCheckBoxOff() {
        String date = setCurrentDate(10);
        $("[data-test-id=city] input").setValue("Уфа");
        $("[data-test-id=date] input").doubleClick().sendKeys(Keys.DELETE);
        $("[data-test-id=date] input").setValue(date);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $(".button").click();
        $("label.input_invalid").shouldBe(exist);
    }

    @Test
    void shouldSubmitValidDataWithCityListAndCalendar() {
        Configuration.holdBrowserOpen = true;
        int daysToAdd = 7;
        String meetingDate = setCurrentDate(daysToAdd);
        $("[data-test-id=city] input").setValue("ма");
        $x("//*[text()='Ханты-Мансийск']").click();
        $(".icon_name_calendar").click();
        int meetingDateMonth = LocalDate.now().plusDays(daysToAdd).getMonthValue();
        int defaultDateMonth = LocalDate.now().plusDays(3).getMonthValue();
        String meetingDateDay = Integer.toString(LocalDate.now().plusDays(daysToAdd).getDayOfMonth());
        if (meetingDateMonth == defaultDateMonth) {
            $$(".calendar__day").find(exactText(meetingDateDay)).click();
        } else {
            $("[data-step='1']").click();
            $$(".calendar__day").find(exactText(meetingDateDay)).click();
        }
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Встреча успешно забронирована на " + meetingDate));
    }

    @Test
    void shouldSubmitValidDataIfMeetingLastMonth() {
        Configuration.holdBrowserOpen = true;
        int daysToAdd = 25;
        String meetingDate = setCurrentDate(daysToAdd);
        $("[data-test-id=city] input").setValue("ма");
        $x("//*[text()='Ханты-Мансийск']").click();
        $(".icon_name_calendar").click();
        int meetingDateMonth = LocalDate.now().plusDays(daysToAdd).getMonthValue();
        int defaultDateMonth = LocalDate.now().plusDays(3).getMonthValue();
        String meetingDateDay = Integer.toString(LocalDate.now().plusDays(daysToAdd).getDayOfMonth());
        if (meetingDateMonth == defaultDateMonth) {
            $$(".calendar__day").find(exactText(meetingDateDay)).click();
        } else {
            $("[data-step='1']").click();
            $$(".calendar__day").find(exactText(meetingDateDay)).click();
        }
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79112223344");
        $("[data-test-id=agreement]").click();
        $(".button").click();
        $(".notification__content").shouldBe(visible, Duration.ofSeconds(15)).shouldHave(exactText("Встреча успешно забронирована на " + meetingDate));
    }


}
