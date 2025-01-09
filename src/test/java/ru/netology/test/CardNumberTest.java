package ru.netology.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPurchasePage;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.*;

public class CardNumberTest {
    private PaymentPurchasePage paymentPurchasePage;

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
        Configuration.pageLoadTimeout = 30000; // Таймаут загрузки страницы
        Configuration.headless = true;
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
    void cleanTables() {
        SQLHelper.clearTables();
    }

    @Test
    void shouldSubmitApplicationApprovedCard() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldShowSuccessNotification();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationDeclinedCard() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberDeclined);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldShowErrorNotification();
        assertEquals("DECLINED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationEmptyCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField("");
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationInvalidCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberInvalid);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationAllZeroCardNumber() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberAll0);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}
