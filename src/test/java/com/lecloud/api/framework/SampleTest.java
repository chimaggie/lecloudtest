package com.lecloud.api.framework;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.ConversionFailedException;
import junitparams.converters.Converter;
import junitparams.converters.Param;
import junitparams.naming.TestCaseName;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created by hongyuechi on 4/15/16.
 */
@RunWith(JUnitParamsRunner.class)
public class SampleTest {
    private static PersonTests pt;

    public SampleTest() throws Exception {
        ObjectMapper om = new ObjectMapper();
        try {
            this.pt = om.readValue(this.getClass().getResourceAsStream("sample-test.json"), PersonTests.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Test
    @Parameters({"test1", "test2"})
    @TestCaseName("test {0}")
    public void testConvert(@Param(converter = TestConverter.class) TestPerson person) {
        assertThat(person.name, equalTo(person.assertName));
        assertThat(person.age, equalTo(person.assertAge));
    }

    public static class TestConverter implements Converter<Param, TestPerson> {
        public void initialize(Param param) {
        }

        public TestPerson convert(Object o) throws ConversionFailedException {
            for (PersonTest t : SampleTest.pt.getTests()) {
                if (t.name.equals(o)) {
                    return t.person;
                }
            }
            return null;
        }
    }

    public static class PersonTests {
        private List<PersonTest> tests;

        public List<PersonTest> getTests() {
            return tests;
        }

        public void setTests(List<PersonTest> tests) {
            this.tests = tests;
        }
    }

    public static class PersonTest {
        String name;
        TestPerson person;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TestPerson getPerson() {
            return person;
        }

        public void setPerson(TestPerson person) {
            this.person = person;
        }
    }

    public static class TestPerson {
        private String name;
        private Integer age;
        private String assertName;
        private Integer assertAge;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getAssertName() {
            return assertName;
        }

        public void setAssertName(String assertName) {
            this.assertName = assertName;
        }

        public Integer getAssertAge() {
            return assertAge;
        }

        public void setAssertAge(Integer assertAge) {
            this.assertAge = assertAge;
        }
    }

}
