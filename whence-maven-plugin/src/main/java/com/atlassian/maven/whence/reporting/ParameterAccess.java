package com.atlassian.maven.whence.reporting;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Takes out the null-ness of BND code and sorts how we want
 */
class ParameterAccess {

    static Attrs attrs(Parameters parameters, String parameterName) {
        Attrs out = new Attrs();
        Attrs in = parameters.get(parameterName);
        if (in == null) {
            return out;
        }
        List<String> keys = new ArrayList<>(in.keySet());
        Collections.sort(keys);
        // I know I should be using a Guava ordering here...but... I couldn't work out how
        List<String> specificKeys = ImmutableList.of("version", "uses:").reverse();
        specificKeys.stream().filter(keys::contains).forEach(specificKey -> {
            keys.remove(specificKey);
            keys.add(0, specificKey);
        });


        for (String key : keys) {
            String value = in.get(key);
            if (value != null) {
                out.put(key, value);
            }
        }
        return out;
    }
}
