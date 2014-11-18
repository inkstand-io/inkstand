package li.moskito.test.jcr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public final class JCRAssert {

    private JCRAssert() {
    }

    /**
     * Asserts the equality of a property value of a node with an expected value
     * 
     * @param node
     *            the node containing the property to be verified
     * @param propertyName
     *            the property name to be verified
     * 
     * @param actualValue
     *            the actual value that should be compared to the propert node
     * 
     * @throws RepositoryException
     */
    public static void assertStringPropertyEquals(final Node node, final String propertyName, final String actualValue)
            throws RepositoryException {
        assertTrue("Node " + node.getPath() + " has no property " + propertyName, node.hasProperty(propertyName));
        final Property prop = node.getProperty(propertyName);
        assertEquals("Property type is not STRING ", PropertyType.STRING, prop.getType());
        assertEquals(actualValue, prop.getString());
    }

    /**
     * Asserts that an item, identified by it's unique id, is not found in the repository session.
     * 
     * @param session
     *            the session to be searched
     * @param itemId
     *            the item expected not to be found
     * @throws RepositoryException
     */
    public static void assertItemNotExist(final Session session, final String itemId) throws RepositoryException {
        try {
            session.getNodeByIdentifier(itemId);
            fail("ItemNotFoundException expected");
        } catch (final ItemNotFoundException e) {
            // this was expected
        }
    }
}
