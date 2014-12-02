package li.moskito.test.rules;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import junit.framework.AssertionFailedError;

import org.apache.commons.io.IOUtils;
import org.junit.rules.TemporaryFolder;

/**
 * A rule for creating an external file in a temporary folder with a specific content. If no content is defined an empty
 * file will be created
 * 
 * @author Gerald Muecke, gerald@moskito.li
 */
public class ExternalFile extends ExternalResource {

    private URL contentUrl;
    private TemporaryFolder folder;
    private String filename;
    private File file;
    private boolean forceContent;

    /**
     * Creates an ExternalFile in the specified temporary folder with the specified filename
     * 
     * @param folder
     * @param filename
     */
    public ExternalFile(TemporaryFolder folder, String filename) {
        this.folder = folder;
        this.filename = filename;
    }

    @Override
    protected void before() throws Throwable {
        this.file = folder.newFile(filename);
        if (this.forceContent && this.contentUrl == null) {
            throw new AssertionFailedError("ContentUrl is not set");
        } else if (this.contentUrl != null) {
            InputStream is = this.contentUrl.openStream();
            OutputStream os = new FileOutputStream(this.file);
            IOUtils.copy(is, os);
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(os);
        }
    }

    @Override
    protected void after() {
        if (this.file != null) {
            this.file.delete();
        }

    }

    /**
     * Defines the classpath resource from where the content of the file should be retrieved
     * 
     * @param pathToResource
     * @return
     */
    public ExternalFile fromClasspathResource(String pathToResource) {
        ClassLoader ccl = Thread.currentThread().getContextClassLoader();
        if (ccl != null) {
            this.contentUrl = ccl.getResource(pathToResource);
        } else {
            this.contentUrl = getClass().getResource(pathToResource);
        }
        return this;
    }

    /**
     * Defines, that the external file must not be empty, which means, the rule enforces, the contentUrl is set.
     * 
     * @return
     */
    public ExternalFile withContent() {
        this.forceContent = true;
        return this;
    }

    /**
     * Defines the resource by URL from where the content of the file should be retrieved
     * 
     * @param resource
     * @return
     */
    public ExternalFile fromResource(URL resource) {
        this.contentUrl = resource;
        return this;
    }

    /**
     * Returns the file handle of the external file
     * 
     * @return
     */
    public File getFile() {
        return file;
    }

}
