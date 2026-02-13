package com.concept;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntryFileValidationIT extends BaseIT {

    @Test
    public void assert_that_csv_returns_expected_json_and_is_OK() throws Exception {

        configureIpLookupResponse("""
                {
                  "countryCode": "GB",
                  "isp": "IBM"
                }
                """);

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt", "text/plain",
                """
                10b2f1c9-3710-48e4-a5b8-7d40952525bc|JUDK3E|Mia Williams|Likes Peach|Rides A Scooter|2.9|36.8
                70f5f8c7-c467-41b6-9ef0-bdf09b465b82|X9LCL2|George Williams|Likes Peach|Drives an SUV|1.5|11.5
                """.getBytes());

        MvcResult resultActions = mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
                [
                  {"Name":"Mia Williams","Transport":"Rides A Scooter","Top Speed":36.8},
                  {"Name":"George Williams","Transport":"Drives an SUV","Top Speed":11.5}
                ]
                """;

        JSONAssert.assertEquals(expectedJson, resultActions.getResponse().getContentAsString(), false);
    }

    @Test
    public void assert_that_country_code_is_FORBIDDEN() throws Exception {

        configureIpLookupResponse("""
                {
                  "countryCode": "US",
                  "isp": "IBM"
                }
                """);

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt", "text/plain", "".getBytes());

        mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Country is blacklisted: US"));
    }

    @Test
    public void assert_that_isp_is_FORBIDDEN() throws Exception {

        configureIpLookupResponse("""
                {
                  "countryCode": "GB",
                  "isp": "AWS"
                }
                """);

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt", "text/plain", "".getBytes());

        mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isForbidden())
                .andExpect(content().string("ISP is blacklisted: AWS"));
    }
}
