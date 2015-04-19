package io.inkstand.http.undertow;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;

import javax.enterprise.inject.Produces;

/**
 * Interface to provide Undertow deployments that are injected and deployed automatically on {@link Undertow}.
 *
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
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
