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

import com.hida.model.DefaultSetting;
import com.hida.model.PidTest;
import com.hida.service.PropertiesLoaderService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.testng.Assert;

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
    
    @Autowired
    private PropertiesLoaderService propertiesService_;
    
    private PidTest pidTest_ = new PidTest();
    
    @Value("${defaultSetting.path}")
    private String defaultSettingPath_;

    private MockMvc mockedContext_;

    private DefaultSetting defaultSetting_;
        
    
    @BeforeClass
    public void setup() throws IOException {
        mockedContext_ = MockMvcBuilders.webAppContextSetup(webAppContext_).build();
        initDefaultSetting();
    }

    @Test
    public void testMint() throws Exception {
        MvcResult result = mockedContext_.perform(get("/Minter/mint/10")
                .accept("application/json"))
                .andExpect(status().isCreated())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Assert.assertEquals("", content);
    }
    
    private void initDefaultSetting() throws IOException{
        defaultSetting_ = propertiesService_.readPropertiesFile(defaultSettingPath_);
    }

}
