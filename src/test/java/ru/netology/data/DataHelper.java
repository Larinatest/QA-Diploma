package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DataHelper {
    public static final String cardNumberApproved = "4444 4444 4444 4441";
    public static final String cardNumberDeclined = "4444 4444 4444 4442";
    public static final String cardNumberAll0 = "0000 0000 0000 0000";
    public static final String cardNumberInvalid = "*/->!<2ab‘?$+)%#";
    public static final String monthAndYearNumbersIsAll0 = "00";
    public static final String nonExistentMonthNumber = "13";
    public static final String monthNumberInvalid = "/1";
    public static final String longMonthInvalid = "123";
    public static final String yearNumberInvalid = "*/";
    public static final String ownerInvalid = "*/";
    public static final String cvcIsAll0 = "000";
    public static final String cvcInvalid = "*/1";
    public static final String longCvcNumber = "1234";

    public static String getCardNumberSign16() {
        Faker faker = new Faker();
        return faker.number().digits(16);
    }

    public static String getMonth(int shift) {
        return  LocalDate.now().plusMonths(shift).format(DateTimeFormatter.ofPattern("MM"));
    }

    public static String getOneNumber() {
        Faker faker = new Faker();
        return faker.number().digits(1);
    }

    public static String getTwoNumber() {
        Faker faker = new Faker();
        return faker.number().digits(2);
    }

    public static String getYear(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yy"));

    }

    public static String getFullYearNumber(int shift) {
        return LocalDate.now().plusYears(shift).format(DateTimeFormatter.ofPattern("yyyy"));
    }

    public static String getOwnerFullNameEn() {
        Faker faker = new Faker(new Locale("en"));
        return faker.name().fullName();
    }

    public static String getOwnerFullNameRu() {
        Faker faker = new Faker(new Locale("ru"));
        return faker.name().lastName();
    }

    public static String getCVC(int i) {
        Faker faker = new Faker();
        return faker.number().digits(3);
    }

    public static String getCardNumberSign15() {
        Faker faker = new Faker();
        return faker.number().digits(15);
    }

    public static String getCardNumberSign17() {
        Faker faker = new Faker();
        return faker.number().digits(17);
    }
    @Value
    public static class CardInfo {
        String cardNumber;
    }

    @Value
    public static class MonthInfo {
        String month;

    }

    @Value
    public static class YearInfo {
        String year;

    }

    @Value
    public static class OwnerInfo {
        String owner;

    }

    @Value
    public static class CvcInfo {
        String cvc;

    }
}

