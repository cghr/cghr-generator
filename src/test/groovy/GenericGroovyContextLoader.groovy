import org.springframework.beans.factory.groovy.GroovyBeanDefinitionReader
import org.springframework.beans.factory.support.BeanDefinitionReader
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.support.AbstractGenericContextLoader

/**
 * Created by ravitej on 2/5/14.
 */
class GenericGroovyContextLoader extends AbstractGenericContextLoader {


    @Override
    protected BeanDefinitionReader createBeanDefinitionReader(GenericApplicationContext context) {
        return new GroovyBeanDefinitionReader(context)
    }


    @Override
    protected String getResourceSuffix() {
        return null
    }
}
