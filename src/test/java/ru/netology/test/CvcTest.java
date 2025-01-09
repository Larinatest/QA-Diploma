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

public class CvcTest {
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
    void shouldSubmitApplicationWithValidCVC() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldShowSuccessNotification();
        assertEquals("APPROVED", SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithEmptyCVC() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillCvcCvvField("");
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithAllZeroCVC() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillCvcCvvField(DataHelper.cvcIsAll0);
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithInvalidCVC() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillCvcCvvField(DataHelper.cvcInvalid);
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    @Test
    void shouldNotSubmitApplicationWithLongCVC() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(DataHelper.cardNumberApproved);
        fillOtherFieldsByValidInfo();
        paymentPurchasePage.fillCvcCvvField(DataHelper.longCvcNumber);
        paymentPurchasePage.clickContinueButton();
        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(SQLHelper.getPaymentStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
    }
}
