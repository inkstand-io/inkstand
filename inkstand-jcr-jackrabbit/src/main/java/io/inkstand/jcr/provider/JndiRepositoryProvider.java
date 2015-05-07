/*
 * Copyright 2015 Gerald Muecke, gerald.muecke@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.inkstand.jcr.provider;

import javax.annotation.Priority;
import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.jcr.Repository;

import io.inkstand.jcr.JndiRepository;
import io.inkstand.jcr.RepositoryProvider;

/**
 * Provides a JCR {@link Repository} that is available as JNDI resource with the name &quot;java:/jcr/local&quot;
 * 
 * @author <a href="mailto:gerald@inkstand.io">Gerald M&uuml;cke</a>
 * 
 */
@Priority(3)
public class JndiRepositoryProvider implements RepositoryProvider {

	//TODO find a generic way to point to a the JCR Repo via JNDI
    @Resource(mappedName = "java:/jcr/local/node01")
    private Repository repository;

    @Override
    @Produces
    @JndiRepository
    public Repository getRepository() {
        return repository;
    }
}
