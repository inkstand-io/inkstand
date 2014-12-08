package li.moskito.inkstand.jcr.util;

import li.moskito.scribble.ScribbleRule;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class JCRContentHandlerTest {

    @ClassRule
    public static ScribbleRule SCRIBBLE = new ScribbleRule();
    private JCRContentHandler subject;

    @Before
    public void setUp() throws Exception {
        this.subject = new JCRContentHandler(SCRIBBLE.getJcrSession().getAdminSession());
    }

    @Test
    public void testStartDocument() throws Exception {
        this.subject.startDocument();
        // nothing happends here...
    }

    @Test
    public void testEndDocument() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testCharacters() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testJCRContentHandler() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testStartElement() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

    @Test
    public void testEndElement() throws Exception {
        throw new RuntimeException("not yet implemented");
    }

}
