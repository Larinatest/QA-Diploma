package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.netology.data.DataHelper.cardNumberAll0;
import static ru.netology.data.DataHelper.cardNumberInvalid;
import static ru.netology.data.SQLHelper.clearTables;

public class CardNumberTest {
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

    // Оплата тура дебетовой картой, номер карты меньше 16 цифр
    @Test
    void shouldNotSubmitApplicationWrongFormatShortCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign15());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер карты меньше 16 цифр
    @Test
    void shouldNotSubmitApplicationWrongFormatShortCreditCardNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign15());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер карты больше 16 цифр
    @Test
    void shouldNotSubmitApplicationLongCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign17());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер карты больше 16 цифр
    @Test
    void shouldNotSubmitApplicationLongCreditCardNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign17());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, ввод пустого номера карты
    @Test
    void shouldNotSubmitApplicationEmptyInput() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, ввод пустого номера карты
    @Test
    void shouldNotSubmitApplicationCreditCardEmptyInput() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, номер карты "0000 0000 0000 0000"
    @Test
    void shouldNotSubmitApplicationCardAll0() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, номер карты "0000 0000 0000 0000"
    @Test
    void shouldNotSubmitApplicationCreditCardAll0() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberAll0);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, невалидный номер карты (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, невалидный номер карты (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidCreditCardNumber() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}
