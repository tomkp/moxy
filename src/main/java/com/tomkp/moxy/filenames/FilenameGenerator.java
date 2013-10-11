package com.tomkp.moxy.filenames;

import com.tomkp.moxy.profile.Profile;

import javax.servlet.http.HttpServletRequest;

public interface FilenameGenerator {

    public String generate(HttpServletRequest request, Profile profile);

}
