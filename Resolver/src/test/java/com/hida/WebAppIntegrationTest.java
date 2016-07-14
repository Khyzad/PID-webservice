/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.hida;

import com.hida.model.Citation;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * An integration test of the entire application to ensure functionality
 *
 * @author lruffin
 */
@WebIntegrationTest("server.port:9000")
@SpringApplicationConfiguration(classes = {Application.class})
@TestPropertySource(locations = "classpath:testConfig.properties")
public class WebAppIntegrationTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private WebApplicationContext webAppContext_;

    private Citation citation_;

    private MockMvc mockedContext_;

    @BeforeClass
    public void setup() throws IOException {
        mockedContext_ = MockMvcBuilders.webAppContextSetup(webAppContext_).build();
        initCitation();
    }

    @Test
    public void testIndex() throws Exception {
        mockedContext_.perform(get("/Resolver/"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInsert() throws Exception {
        // initialize path
        String path = String.format("/Resolver/insert?"
                + "purl=%s&url=%s&erc=%s&who=%s&what=%s&when=%s", citation_.getPurl(), 
                citation_.getUrl(), citation_.getErc(), citation_.getWho(), citation_.getWhat(),
                citation_.getDate());
        
        mockedContext_.perform(get(path))
                .andExpect(status().isCreated());
    }

    private void initCitation() {
        citation_ = new Citation("testPurl",
                "testUrl",
                "testErc",
                "testWho",
                "testWhat",
                "testDate");
    }
}
