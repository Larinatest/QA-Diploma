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

public class MonthTest {
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

    // Оплата тура дебетовой картой, номер месяца меньше двух цифр
    @Test
    void shouldNotSubmitApplicationWrongFormatOneDigitMonth() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер месяца меньше двух цифр
    @Test
    void shouldNotSubmitApplicationWrongFormatCreditCardOneDigitMonth() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillMonthField(DataHelper.getOneNumber());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер месяца больше двух цифр (123)
    @Test
    void shouldNotSubmitApplicationLongMonthNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(longMonthInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер месяца больше двух цифр (123)
    @Test
    void shouldNotSubmitApplicationCreditCardLongMonthNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillMonthField(longMonthInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }
    // Оплата тура дебетовой картой, ввод номера пустого месяца
    @Test
    void shouldNotSubmitApplicationEmptyInput() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, ввод номера пустого месяца
    @Test
    void shouldNotSubmitApplicationCreditCardEmptyInput() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillMonthField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер месяца "00"
    @Test
    void shouldNotSubmitApplicationMonthNumberAll0() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(monthAndYearNumbersIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationInvalidCard();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер месяца "00"
    @Test
    void shouldNotSubmitApplicationCreditCardMonthNumberAll0() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillMonthField(monthAndYearNumbersIsAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationInvalidCard();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, невалидный номер месяца (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidMonthNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(monthNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, невалидный номер месяца (спец.символы)
    @Test
    void shouldNotSubmitApplicationCreditCardInvalidMonthNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillMonthField(monthNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, невалидный номер месяца (13)
    @Test
    void shouldNotSubmitApplicationNonExistentMonthNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(nonExistentMonthNumber);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationInvalidCard();
        assertNull(SQLHelper.getPaymentStatus());
    }

    // Оплата тура кредитной картой, невалидный номер месяца (13)
    @Test
    void shouldNotSubmitApplicationCreditCardNonExistentMonthNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillMonthField(nonExistentMonthNumber);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationInvalidCard();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    public void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign16());
        paymentPurchasePage.fillYearField(DataHelper.getYear(25));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC(3));
        paymentPurchasePage.clickContinueButton();
    }
}