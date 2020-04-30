/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.tool.spoon;

import java.util.List;
import java.util.Map;

import spoon.SpoonException;
import spoon.processing.Property;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;

/**
 * Fail the build if some code is calling a forbidden method.
 * 
 * @version $Id$
 * @since 9.9RC2
 */
public class ForbiddenInvocationProcessor extends AbstractXWikiProcessor<CtInvocation<?>>
{
    @Property
    private Map<String, List<String>> methods;

    @Override
    public void process(CtInvocation<?> element)
    {
        if (this.methods == null) {
            throw new SpoonException("Processor must be configured with a \"methods\" parameter of type "
                + "\"Map<String, List<String>>\".");
        }

        CtExpression<?> target = element.getTarget();

        if (target != null && target.getType() != null) {
            String type = target.getType().getQualifiedName();
            List<String> methodList = this.methods.get(type);
            if (methodList != null) {
                String method = element.getExecutable().getSimpleName();
                if (methodList.contains(method)) {
                    String message = String.format("Forbidden call to [%s#%s] at %s", type, method,
                        target.getPosition());
                    registerError(message);
                }
            }
        }
    }
}
