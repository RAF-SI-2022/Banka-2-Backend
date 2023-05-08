package com.raf.si.Banka2Backend.cucumber.integration.futureFailure;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/future-integration/futureFailure.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.raf.si.Banka2Backend.cucumber.integration.futureFailure")
public class FutureFailureIntegrationTest {}
