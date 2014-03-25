package org.cghr.generator.db
import com.github.jknack.handlebars.Handlebars
import com.github.jknack.handlebars.Template
/**
 * Created by ravitej on 19/3/14.
 */
class DbStructureGenerator {

    Handlebars handlebars
    String templateLocation

    DbStructureGenerator(Handlebars handlebars, String templateLocation) {
        this.handlebars = handlebars
        this.templateLocation = templateLocation
    }

    String generate(Map context) {

        Template template = handlebars.compile(templateLocation);
        String generated = template.apply(context);
        return generated

    }


}
