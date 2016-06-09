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

package org.grails.plugins.springws;

import org.grails.core.AbstractInjectableGrailsClass;
import javax.xml.transform.TransformerConfigurationException;

/**
 * Default class for Spring Web Services endpoint artefacts.
 *
 * @author Russ Miles (russ@russmiles.com)
 * @author Ivo Houbrechts (ivo@houbrechts-it.be)
 *
 */
public class DefaultGrailsEndpointClass extends AbstractInjectableGrailsClass {

	public DefaultGrailsEndpointClass(Class clazz) throws TransformerConfigurationException {
		super(clazz, "Endpoint");
	}
}