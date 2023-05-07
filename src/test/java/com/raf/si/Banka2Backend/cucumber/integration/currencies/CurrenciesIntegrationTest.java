package com.raf.si.Banka2Backend.cucumber.integration.currencies;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/currency-integration/currencies.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.raf.si.Banka2Backend.cucumber.integration.currencies")
public class CurrenciesIntegrationTest {}
