package org.cghr.generator

import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template

/**
 * Created by ravitej on 25/3/14.
 */
class Generator {


    Handlebars handlebars


    Generator(Handlebars handlebars) {
        this.handlebars=handlebars
    }

    String generate(String templateLocation, Map context) {

        Template template = handlebars.compile(templateLocation)
        return template.apply(context)


    }

}
