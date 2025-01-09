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

public class MonthTest {
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
    void shouldSubmitApplicationWithValidMonth() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldShowSuccessNotification();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithEmptyMonth() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        paymentPurchasePage.fillMonthField("");
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithNonExistentMonth() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        paymentPurchasePage.fillMonthField(DataHelper.nonExistentMonthNumber);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithInvalidMonthFormat() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        paymentPurchasePage.fillMonthField(DataHelper.monthNumberInvalid);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithLongMonth() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        paymentPurchasePage.fillMonthField(DataHelper.longMonthInvalid);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}
