package com.raf.si.Banka2Backend.cucumber.integration.usersFailures;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/user-integration/usersFailures.feature")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "com.raf.si.Banka2Backend.cucumber.integration.usersFailures")
public class UserFailuresIntegrationTest {
}
