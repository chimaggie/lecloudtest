package com.lecloud.api.test;

import com.faradayfuture.test.commons.rest.runner.DefaultRestTestClass;
import com.faradayfuture.test.commons.rest.runner.RestTestGroupJsonPath;
import junitparams.JUnitParamsRunner;
import org.junit.runner.RunWith;

/**
 * Created by maggie on 4/19/16.
 */
@RunWith(JUnitParamsRunner.class)
@RestTestGroupJsonPath("account-management.json")
public class AccountManagementTest extends DefaultRestTestClass {

}
