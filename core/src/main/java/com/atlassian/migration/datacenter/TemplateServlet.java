/*
 * Copyright 2020 Atlassian
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

package com.atlassian.migration.datacenter;

import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.sal.api.websudo.WebSudoSessionException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collections;

/**
 * Creates the servlet which renders the soy template containing the frontend SPA bundle.
 */
public class TemplateServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(TemplateServlet.class);

    private final SoyTemplateRenderer templateRenderer;
    private final WebSudoManager webSudoManager;

    public TemplateServlet(final SoyTemplateRenderer templateRenderer,
                           final WebSudoManager webSudoManager) {
        this.templateRenderer = templateRenderer;
        this.webSudoManager = webSudoManager;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            webSudoManager.willExecuteWebSudoRequest(request);
            render(response);
        } catch (WebSudoSessionException exception) {
            webSudoManager.enforceWebSudoProtection(request, response);
        } catch (IOException exception) {
            logger.error("Unable to render template", exception);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void render(HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.TEXT_HTML);

        templateRenderer.render(
                response.getWriter(),
                "com.atlassian.migration.datacenter.jira-plugin:dc-migration-assistant-templates",
                "dcmigration.init",
                Collections.emptyMap()
        );
    }
}
