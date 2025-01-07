package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SQLHelper.clearTables;

public class YearTest {
    private PaymentPurchasePage paymentPurchasePage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080/");
        paymentPurchasePage = new PaymentPurchasePage();
    }

    @AfterEach
    public void cleanTables() {
        clearTables();
    }

    // Оплата тура дебетовой картой, номер года меньше 2 цифр
    @Test
    void shouldNotSubmitApplicationWrongFormatShortYearNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер года меньше 2 цифр
    @Test
    void shouldNotSubmitApplicationCreditCardWrongFormatShortYearNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер года больше 2 цифр
    @Test
    void shouldNotSubmitApplicationFullYearNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField(DataHelper.getFullYearNumber(0));
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер года больше 2 цифр
    @Test
    void shouldNotSubmitApplicationCreditCardFullYearNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField(DataHelper.getFullYearNumber(0));
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер года не указан
    @Test
    void shouldNotSubmitApplicationEmptyInput() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер года не указан
    @Test
    void shouldNotSubmitApplicationCreditCardEmptyInput() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер года "00"
    @Test
    void shouldNotSubmitApplicationYearNumberAll0() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField(monthAndYearNumbersIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер года "00"
    @Test
    void shouldNotSubmitApplicationCreditCardYearNumberAll0() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField(monthAndYearNumbersIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, невалидный номер года (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidYearNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField(yearNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, невалидный номер года (спец.символы)
    @Test
    void shouldNotSubmitApplicationCreditCardInvalidYearNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField(yearNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, прошедший год "23"
    @Test
    void shouldNotSubmitApplicationLastYear() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillYearField(getYear(-1));
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, прошедший год "23"
    @Test
    void shouldNotSubmitApplicationCreditCardLastYear() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillYearField(getYear(-1));
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign16());     //случайная карта
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));       //число месяца следующего за текущим
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}

