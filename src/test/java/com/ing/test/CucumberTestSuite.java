package com.ing.test;


import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(

        plugin = {"pretty", "html:target/cucumber-reports"},
        features = {"src/test/resources/features"},
        glue = {"com.ing.test.keywords"}
)
public class CucumberTestSuite {

}