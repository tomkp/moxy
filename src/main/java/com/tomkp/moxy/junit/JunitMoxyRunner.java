package com.tomkp.moxy.junit;

import com.tomkp.moxy.MoxyTestRunner;
import com.tomkp.moxy.jetty.EmbeddedJetty;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JunitMoxyRunner extends BlockJUnit4ClassRunner {


    private static final Logger LOG = LoggerFactory.getLogger(JunitMoxyRunner.class);


    private Class<?> testClass;


    public JunitMoxyRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
    }


    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        long start = System.currentTimeMillis();
        LOG.info(testClass + "." + method.getName() + "");

        MoxyTestRunner runner = new MoxyTestRunner(new EmbeddedJetty());
        runner.initialise(testClass, method.getMethod());

        super.runChild(method, notifier);

        runner.end();

        long end = System.currentTimeMillis();
        LOG.info("" + (end - start) + "ms");
    }


}