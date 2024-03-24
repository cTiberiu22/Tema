@regression
Feature: Debtor amount

  Scenario: Debtor amount has at least 2 digits
    Given a XML file used in SEPA area
    When the debtor total amounts is extracted
    Then the debtor total amount have at least 2 digits


  Scenario: Debtor amount is equal to the sum of all credits
    Given a XML file used in SEPA area
    When the sum of all credits and debtor total amount are extracted
    Then the sum is equal to the debtor total amount


  Scenario: Transaction date is not in the future
    Given a XML file used in SEPA area
    When the transaction date is extracted
    Then the transaction date is not in the future


  Scenario: IBANs are valid
    Given a XML file used in SEPA area
    When the IBANs are extracted
    Then the IBANs are valid


  Scenario: BICs are valid
    Given a XML file used in SEPA area
    When the BICs are extracted
    Then the BICs are valid


