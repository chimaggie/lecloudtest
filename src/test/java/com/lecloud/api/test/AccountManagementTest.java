package com.lecloud.api.test;

import com.lecloud.api.framework.ApiTestSuite;
import com.lecloud.api.framework.ApiTestSuiteParameterProvider;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.naming.TestCaseName;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by maven on 4/18/16.
 */
@RunWith(JUnitParamsRunner.class)
public class AccountManagementTest {
    private static ApiTestSuite suite = ApiTestSuite.build(AccountManagementTest.class);

    @BeforeClass
    public static void beforeClass() throws Exception {
        suite.beforeClassConfig();
    }

    @Test
//    @Parameters(source = ApiTestSuiteParameterProvider.class)
    @Parameters(method = "params")
    @TestCaseName("test - {0}")
    public void apiTest(String apiTestName) throws Exception {
        suite.beforeTestConfig();
        suite.asserting(suite.getTests().get(apiTestName));
        suite.afterTestConfig();
    }

    private Object params() {
        return suite.getTests().keySet().toArray(new Object[]{});
    }

    @AfterClass
    public static void afterClass() throws Exception {
        suite.afterClassConfig();
    }
}
