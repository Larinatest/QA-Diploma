package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;

import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static ru.netology.data.DataHelper.cardNumberApproved;
import static ru.netology.data.DataHelper.cardNumberDeclined;
import static ru.netology.data.SQLHelper.clearTables;

public class CardPaymentTest {
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

    // Оплата тура дебетовой картой, со статусом "APPROVED"
    @Test
    void shouldApproveBuyingTourCardApproved() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberApproved);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveSuccessNotification();
        assertEquals("APPROVED", new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, со статусом "APPROVED"
    @Test
    void shouldApproveBuyingTourCreditCardApproved() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberApproved);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveSuccessNotification();
        assertEquals("APPROVED", new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, со статусом "DECLINED"
    @Test
    void shouldDeclinedBuyingTourCardDeclined() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberDeclined);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertEquals("DECLINED", new SQLHelper().getPaymentStatus());
    }

    //Оплата тура кредитной картой, со статусом "DECLINED"
    @Test
    void shouldDeclinedBuyingTourCreditCardDeclined() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillCardNumberField(cardNumberDeclined);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotification();
        assertEquals("DECLINED", new SQLHelper().getCreditRequestStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}