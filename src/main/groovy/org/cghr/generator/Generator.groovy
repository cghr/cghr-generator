package org.cghr.generator

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
import org.apache.commons.lang3.StringEscapeUtils

/**
 * Created by ravitej on 25/3/14.
 */
class Generator {

    Handlebars handlebars


    Generator(Handlebars handlebars) {
        this.handlebars = handlebars
    }

    String generate(String templateLocation, Map context) {


        Template template = handlebars.compile(templateLocation)
        return StringEscapeUtils.unescapeHtml4(template.apply(context))

    }

}
