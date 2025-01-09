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

public class YearTest {
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
    void shouldSubmitApplicationWithValidYear() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldShowSuccessNotification();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithEmptyYear() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillYearField("");
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithAllZeroYear() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillYearField(DataHelper.monthAndYearNumbersIsAll0);
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithInvalidYearFormat() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillYearField(DataHelper.yearNumberInvalid);
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithExpiredYear() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillYearField(DataHelper.getYear(-1));
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationCardExpired();
        assertNull(SQLHelper.getPaymentStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
    }
}
