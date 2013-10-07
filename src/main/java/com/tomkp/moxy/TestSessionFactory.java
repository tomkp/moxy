package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;

public class TestSessionFactory {


    public TestSession createTestSession(Moxy moxy, String path) {
        TestSession testSession = new TestSession(path, moxy);
        testSession.validate();
        return testSession;

    }

}
