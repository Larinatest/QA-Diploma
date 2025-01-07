package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;

import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.PaymentPurchasePage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.netology.data.DataHelper.ownerInvalid;
import static ru.netology.data.SQLHelper.clearTables;

public class OwnerTest {
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

    // Оплата тура дебетовой картой, без указания владельца
    @Test
    void shouldNotSubmitApplicationEmptyInput() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillOwnerField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, без указания владельца
    @Test
    void shouldNotSubmitApplicationCreditCardEmptyInput() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillOwnerField("");
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationRequiredField();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, недействительный владелец (спец.символы)
    @Test
    void shouldNotSubmitApplicationInvalidOwner() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillOwnerField(ownerInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, недействительный владелец (спец.символы)
    @Test
    void shouldNotSubmitApplicationCreditCardInvalidCardOwner() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillOwnerField(ownerInvalid);
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, фамилия кириллицей
    @Test
    void shouldNotSubmitApplicationWrongFormatSurNameInCyrillic() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameRu());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, фамилия кириллицей
    @Test
    void shouldNotSubmitApplicationCreditCardWrongFormatSurNameInCyrillic() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameRu());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    // Оплата тура дебетовой картой, двойная фамилия через дефис латинскими буквами
    @Test
    void shouldNotSubmitApplicationWrongFormatHyphenatedDoubleSurnameInLatinLetters() {
        paymentPurchasePage.openCardPaymentPage();
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getPaymentStatus());
    }

    // Оплата тура кредитной картой, двойная фамилия через дефис латинскими буквами
    @Test
    void shouldNotSubmitApplicationCreditCardWrongFormatHyphenatedDoubleSurnameInLatinLetters() {
        paymentPurchasePage.openCreditCardPaymentPage();
        paymentPurchasePage.fillOwnerField(DataHelper.getOwnerFullNameEn());
        fillOtherFieldsByValidInfo();

        paymentPurchasePage.shouldHaveErrorNotificationWrongFormat();
        assertNull(new SQLHelper().getCreditRequestStatus());
    }

    private void fillOtherFieldsByValidInfo() {
        paymentPurchasePage.fillCardNumberField(DataHelper.getCardNumberSign16());     //случайная карта
        paymentPurchasePage.fillMonthField(DataHelper.getMonth(1));       //число месяца следующего за текущим
        paymentPurchasePage.fillYearField(DataHelper.getYear(1));       //число года следующего за текущим
        paymentPurchasePage.fillCvcCvvField(DataHelper.getCVC());
        paymentPurchasePage.clickContinueButton();
    }
}

