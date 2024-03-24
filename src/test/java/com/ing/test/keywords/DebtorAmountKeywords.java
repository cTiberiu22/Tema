package com.ing.test.keywords;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;

public class DebtorAmountKeywords {


    private String xmlContent;
    private double debtorAmount;
    private double sumOfCredits;
    private LocalDate transactionDate;
    private List<String> ibans = new ArrayList<>();
    private List<String> bics = new ArrayList<>();


    /* ============================== GIVEN STEPS ============================== */
    @Given("a XML file used in SEPA area")
    public void loadSEPAXMLFile() throws IOException {
        // Load SEPA XML file
        xmlContent = new String(Files.readAllBytes(Paths.get("src/test/resources/pain.xml")));
    }

    /* ============================== WHEN STEPS ============================== */
    @When("the debtor total amounts is extracted")
    public void extractDebtorAmount() {
        // Extract debtor amount using regex
        Pattern pattern = Pattern.compile("<PmtInf>.*?<CtrlSum>(\\d+\\.\\d+)</CtrlSum>.*?</PmtInf>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            debtorAmount = Double.parseDouble(matcher.group(1));
        }
    }

    @When("the sum of all credits and debtor total amount are extracted")
    public void extractSumOfCredits() {
        // Extract sum of all credits using regex
        Pattern sumPattern = Pattern.compile("<InstdAmt Ccy=\"EUR\">(\\d+\\.\\d+)</InstdAmt>");
        Matcher sumMatcher = sumPattern.matcher(xmlContent);
        while (sumMatcher.find()) {
            sumOfCredits += Double.parseDouble(sumMatcher.group(1));
        }
        // Extract debtor amount using regex
        Pattern totalPattern = Pattern.compile("<PmtInf>.*?<CtrlSum>(\\d+\\.\\d+)</CtrlSum>.*?</PmtInf>", Pattern.DOTALL);
        Matcher totalMatcher = totalPattern.matcher(xmlContent);
        if (totalMatcher.find()) {
            debtorAmount = Double.parseDouble(totalMatcher.group(1));
        }
    }

    @When("the transaction date is extracted")
    public void extractTransactionDate() {
        // Extract transaction date using regex
        Pattern pattern = Pattern.compile("<ReqdExctnDt>(\\d{4}-\\d{2}-\\d{2})</ReqdExctnDt>");
        Matcher matcher = pattern.matcher(xmlContent);
        if (matcher.find()) {
            // Parse the date string to LocalDate
            transactionDate = LocalDate.parse(matcher.group(1), DateTimeFormatter.ISO_LOCAL_DATE);
        } else {
            throw new RuntimeException("Transaction date not found in the XML file.");
        }
    }

    @When("the IBANs are extracted")
    public void extractIBANs() {
        // Extract IBANs using regex
        Pattern pattern = Pattern.compile("<IBAN>(.*?)</IBAN>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);
        while (matcher.find()) {
            ibans.add(matcher.group(1));
        }
    }

    @When("the BICs are extracted")
    public void extractBICs() {
        // Extract BICS using regex
        Pattern pattern = Pattern.compile("<BIC>(.*?)</BIC>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(xmlContent);
        while (matcher.find()) {
            bics.add(matcher.group(1));
        }
    }

    /* ============================== THEN STEPS ============================== */
    @Then("the debtor total amount have at least 2 digits")
    public void validateDebtorAmount() {
        // Validate debtor amount has at least 2 digits
        assertTrue("Debtor amount should have at least 2 digits", debtorAmount >= 0.01);
    }

    @Then("the sum is equal to the debtor total amount")
    public void validateSumEqualToDebtorTotalAmount() {
        // Compare sum of credits with debtor total amount
        Assert.assertEquals(sumOfCredits, debtorAmount, 0.01);// Tolerating small differences due to floating-point arithmetic
    }

    @Then("the transaction date is not in the future")
    public void validateTransactionDateNotInFuture() {
        // Get current date
        LocalDate currentDate = LocalDate.now();

        // Ensure transaction date is not in the future
        Assert.assertFalse("Transaction date is in the future", transactionDate.isAfter(currentDate));
    }


    @Then("the IBANs are valid")
    public void validateIBANs() {
        for (String iban : ibans) {
            Assert.assertTrue("Invalid IBAN: " + iban, isValidIBAN(iban));
        }
    }

    @Then("the BICs are valid")
    public void validateBICs() {
        for (String bic : bics) {
            Assert.assertTrue("Invalid BIC: " + bic, isValidBIC(bic));
        }
    }

    private boolean isValidIBAN(String iban) {
        String ibanPattern = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$";
        Pattern pattern = Pattern.compile(ibanPattern);
        Matcher matcher = pattern.matcher(iban);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isValidBIC(String bic) {
        String bicPattern = "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$";
        Pattern pattern = Pattern.compile(bicPattern);
        Matcher matcher = pattern.matcher(bic);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
}