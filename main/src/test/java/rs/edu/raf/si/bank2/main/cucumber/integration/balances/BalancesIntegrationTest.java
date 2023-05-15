package rs.edu.raf.si.bank2.main.cucumber.integration.balances;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.core.options.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/balance-integration/balances.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "rs.edu.raf.si.bank2.main.cucumber.integration.balances")
public class BalancesIntegrationTest {}
