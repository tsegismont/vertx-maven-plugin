package io.reactiverse.vertx.maven.plugin.mojos;

import org.junit.Test;

/**
 * @author <a href="http://escoffier.me">Clement Escoffier</a>
 */
public class InitializeMojoTest {


    @Test
    public void testSkip() throws Exception {
        InitializeMojo mojo = new InitializeMojo();
        mojo.skip = true;
        mojo.execute();
    }

}
