/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codehaus.groovy.grails.plugins.spring.ws;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;
import org.codehaus.groovy.grails.commons.GrailsClass;

/**
 * Grails artefact handler for Endpoint classes.
 *
 * @author Russ Miles (russ@russmiles.com)
 */

// TODO Make these Groovy if possible!
public class EndpointArtefactHandler extends ArtefactHandlerAdapter {

	public static final String TYPE = "Endpoint";

    public EndpointArtefactHandler() {
        super(TYPE, GrailsClass.class, DefaultGrailsEndpointClass.class, null);
    }

    public boolean isArtefactClass(Class clazz) {
        // class shouldn't be null and should ends with Endpoint suffix
        return (clazz != null && clazz.getName().endsWith("Endpoint"));
    }
}

