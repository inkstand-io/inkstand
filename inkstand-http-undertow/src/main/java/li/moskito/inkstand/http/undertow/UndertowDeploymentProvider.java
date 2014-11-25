package li.moskito.inkstand.http.undertow;

import io.undertow.servlet.api.DeploymentInfo;

import javax.enterprise.inject.Produces;

/**
 * Interface to provide Undertow deployments
 * 
 * @author Gerald Muecke, gerald@moskito.li
 * 
 */
public interface UndertowDeploymentProvider {

    /**
     * Produces the deployment information required by the deployment manager
     * 
     * @return
     */
    @Produces
    DeploymentInfo getDeployment();
}
