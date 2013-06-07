package com.tomkp.moxy;

import com.tomkp.moxy.annotations.Moxy;

import java.lang.reflect.Method;

public class MoxyDataFactory {



    public MoxyData createMoxyData(Class<?> testClass, Method method) {

        MoxyData moxyData = new MoxyData();

        Moxy moxyMethodAnnotation = method.getAnnotation(Moxy.class);
        if (moxyMethodAnnotation != null) {
            moxyData.add(moxyMethodAnnotation);
        }
        addParentMoxies(moxyData, testClass);

        validateMoxyData(moxyData);

        return moxyData;
    }


    private void addParentMoxies(MoxyData moxyData, Class<?> claz) {
        Moxy moxy = claz.getAnnotation(Moxy.class);
        if (moxy != null) {
            moxyData.add(moxy);
        }
        Class<?> superclass = claz.getSuperclass();
        if (superclass != null) {
            addParentMoxies(moxyData, superclass);
        }
    }



    private void validateMoxyData(MoxyData moxyData) {
        int fileCount = moxyData.getFileCount();
        int responseCount = moxyData.getResponseCount();

        if (responseCount > 0 && fileCount > 0) {
            throw new MoxyException("You must annotate your test with either 'responses' or 'files', but not both");
        }
    }


}
