package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;

import java.lang.reflect.Method;

public class TestSessionFactory {



    public TestSession createTestSession(Class<?> testClass, Method method) {

        TestSession testSession = new TestSession();

        Moxy moxyMethodAnnotation = method.getAnnotation(Moxy.class);
        if (moxyMethodAnnotation != null) {
            testSession.add(moxyMethodAnnotation);
        }
        addParentMoxies(testSession, testClass);

        validateMoxyData(testSession);

        return testSession;
    }


    private void addParentMoxies(TestSession testSession, Class<?> claz) {
        Moxy moxy = claz.getAnnotation(Moxy.class);
        if (moxy != null) {
            testSession.add(moxy);
        }
        Class<?> superclass = claz.getSuperclass();
        if (superclass != null) {
            addParentMoxies(testSession, superclass);
        }
    }



    private void validateMoxyData(TestSession testSession) {
        int fileCount = testSession.getFileCount();
        int responseCount = testSession.getResponseCount();

        if (responseCount > 0 && fileCount > 0) {
            throw new MoxyException("You must annotate your test with either 'responses' or 'files', but not both");
        }
    }


}
