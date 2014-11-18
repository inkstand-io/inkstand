package li.moskito.inkstand.jcr;

/**
 * Extension interface for {@link javax.jcr.Session} that supports Java 7 {@link AutoCloseable}.
 * 
 * @author gmuecke
 * 
 */
public interface Session extends javax.jcr.Session, AutoCloseable {
}
