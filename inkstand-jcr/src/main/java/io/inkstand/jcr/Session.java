package io.inkstand.jcr;

/**
 * Extension interface for {@link javax.jcr.Session} that supports Java 7 {@link AutoCloseable}.
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
public interface Session extends javax.jcr.Session, AutoCloseable {
}
