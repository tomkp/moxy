package com.tomkp.moxy.filenames;

import com.tomkp.moxy.profile.Profile;

import javax.servlet.http.HttpServletRequest;

public class DefaultFilenameGenerator implements FilenameGenerator {

    @Override
    public String generate(HttpServletRequest request, Profile profile) {
        String filename = null;
        if (profile.getFiles().size() == 1) {
            filename = profile.getFiles().get(0);
        } else if (!profile.getFiles().isEmpty()) {
            filename = profile.getFiles().get(profile.getIndex());
        }

        if (filename != null && profile.isIndexed()) {
            filename = filename.replaceAll("\\$", String.valueOf(profile.getIndex() + 1));
        }
        return filename;
    }
}
