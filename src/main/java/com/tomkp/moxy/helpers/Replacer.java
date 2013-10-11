package com.tomkp.moxy.helpers;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class Replacer {

    private static final Logger LOG = LoggerFactory.getLogger(Replacer.class);

    public InputSupplier<? extends InputStream> replace(Map<String, String> replacements, InputStream inputStream) throws IOException {
        LOG.info("replace: '{}'", replacements);
        String str = CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        for (String from : replacements.keySet()) {
            String to = replacements.get(from);
            LOG.info("replace '" + from + "' with '" + to + "'");
            str = str.replaceAll(from, to);
        }
        return ByteStreams.newInputStreamSupplier(str.getBytes("UTF-8"));
    }

}
