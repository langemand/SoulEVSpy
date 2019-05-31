package com.evranger.elm327.commands.filters;

import com.evranger.elm327.commands.Response;
import com.evranger.elm327.commands.ResponseFilter;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Pierre-Etienne Messier <pierre.etienne.messier@gmail.com> on 2015-10-25.
 */
public class RegularExpressionResponseFilter implements ResponseFilter {
    private Pattern mPattern = null;

    public RegularExpressionResponseFilter(String regularExpression) {
        mPattern = Pattern.compile(regularExpression);
    }

    public RegularExpressionResponseFilter(Pattern pattern) {
        mPattern = pattern;
    }

    @Override
    public void onResponseReceived(Response response) {
        List<String> lines = response.getLines();

        ListIterator<String> it = lines.listIterator();
        while(it.hasNext()) {
            String line = it.next();

            Matcher m = mPattern.matcher(line);
            if(m.matches()) {
                final int groupCount = m.groupCount();
                if( groupCount == 1) {
                    it.set(m.group(1));
                }
                else if( groupCount > 1) {
                    throw new RuntimeException("Unsupported pattern match with more than one group");
                }
            }
            else {
                it.remove();
            }
        }
    }
}