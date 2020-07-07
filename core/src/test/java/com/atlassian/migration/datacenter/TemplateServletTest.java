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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.ws.rs.core.MediaType.TEXT_HTML;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TemplateServletTest {

    private TemplateServlet sut;

    @Mock
    private SoyTemplateRenderer mockRenderer;

    @Mock
    private WebSudoManager mockWebSudoManager;

    @Spy
    private HttpServletResponse mockResp;

    @Mock
    private HttpServletRequest mockReq;

    @BeforeEach
    public void init() {
        sut = new TemplateServlet(mockRenderer, mockWebSudoManager);
    }

    @Test
    public void itShouldReturnHtmlPageWhenSuccessful() {

        try {
            sut.doGet(mockReq, mockResp);
            verify(mockResp).setContentType(TEXT_HTML);
            verify(mockWebSudoManager, never()).enforceWebSudoProtection(mockReq, mockResp);
        } catch (IOException ioe) {
            fail();
        }
    }

    @Test
    public void itShouldEnforceWebSudoWhenNotAdmin() {
        doThrow(WebSudoSessionException.class).when(mockWebSudoManager).willExecuteWebSudoRequest(any());

        try {
            final TemplateServlet sut = this.sut;
            sut.doGet(mockReq, mockResp);

            verify(mockWebSudoManager).enforceWebSudoProtection(mockReq, mockResp);
        } catch (IOException ioe) {
            fail();
        }
    }
}