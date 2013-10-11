package com.tomkp.moxy.profile;

import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;
import com.tomkp.moxy.RequestHandler;
import com.tomkp.moxy.Requests;
import com.tomkp.moxy.helpers.Replacer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public class ProfileRequestHandler implements RequestHandler {


    private final Profile profile;
    private final Replacer replacer;


    public ProfileRequestHandler(Profile profile, Replacer replacer) {
        this.profile = profile;
        this.replacer = replacer;
        Requests.reset();
    }


    @Override
    public void process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {

        Requests.capture(httpServletRequest);

        try {

            profile.configureHttpHeaders(httpServletResponse);

            InputStream inputStream = profile.generateResponse(httpServletRequest);

            if (inputStream != null) {

                // APPLY REPLACEMENTS
                InputSupplier<? extends InputStream> inputSupplier = replacer.replace(profile.getReplacements(), inputStream);

                // WRITE RESPONSE
                writeResponse(httpServletResponse, inputSupplier);

                profile.saveResponses(httpServletRequest, inputSupplier);
            }
            profile.increment();

        } catch (Exception e) {
            throw new RuntimeException("error processing request", e);
        }
    }



    private void writeResponse(HttpServletResponse httpServletResponse, InputSupplier<? extends InputStream> inputSupplier) throws IOException {
        ByteStreams.copy(inputSupplier.getInput(), httpServletResponse.getOutputStream());
    }


}
