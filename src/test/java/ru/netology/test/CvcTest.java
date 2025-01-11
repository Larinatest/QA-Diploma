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

public class CvcTest {
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

    // Оплата тура дебетовой картой, одна цифра для CVC
    @Test
    void shouldNotSubmitApplicationWrongFormatOneDigitForCvc() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, одна цифра для CVC
    @Test
    void shouldNotSubmitApplicationCreditCardWrongFormatOneDigitForCvc() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, две цифры для CVC
    @Test
    void shouldNotSubmitApplicationWrongFormatTwoDigitsForCvc() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(DataHelper.getTwoNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, две цифры для CVC
    @Test
    void shouldNotSubmitApplicationCreditCardWrongFormatTwoDigitsForCvc() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(DataHelper.getTwoNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер CVC больше 3 цифр
    @Test
    void shouldNotSubmitApplicationLongCvcNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(longCvcNumber);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер CVC больше 3 цифр
    @Test
    void shouldNotSubmitApplicationCreditCardLongCvcNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(longCvcNumber);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, ввод пустого номера CVC
    @Test
    void shouldNotSubmitApplicationEmptyInput() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, ввод пустого номера CVC
    @Test
    void shouldNotSubmitApplicationCreditCardEmptyInput() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер CVC "000"
    @Test
    void shouldNotSubmitApplicationCvcCvvAll0() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(cvcIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер CVC "000"
    @Test
    void shouldNotSubmitApplicationCreditCardCvcAll0() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(cvcIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, невалидный номер CVC (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidCvcNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(cvcInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, невалидный номер CVC (спец.символы)
    @Test
    void shouldNotSubmitApplicationCreditCardInvalidCvcNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCvcCvvField(cvcInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign16());
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(11));
        paymentPurchasePage.fillYearField(DataHelper.getYear(25));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.clickContinueButton();
    }
}