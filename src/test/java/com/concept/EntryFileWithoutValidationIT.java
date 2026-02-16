package com.concept;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EntryFileWithoutValidationIT extends BaseIT {

    @DynamicPropertySource
    static void configureTempDirectory(DynamicPropertyRegistry registry) {
        registry.add("csv.validation.enabled", () -> false);
    }

    @Test
    public void assert_that_csv_returns_expected_json_and_is_OK() throws Exception {

        String ipAddress = configureIpLookupResponse("""
                {
                  "countryCode": "GB",
                  "isp": "IBM"
                }
                """);

        MockMultipartFile file = new MockMultipartFile("file", "EntryFile.txt", "text/plain",
                """
                10b2f1c9-3710-48e4-a5b8-7d40952525bc|JUDK3E|Mia Williams|Likes Peach|Rides A Scooter|FOO|BAR
                
                |X9LCL2||Likes Peach|Drives an SUV|1.5|11.5
                """.getBytes());

        MvcResult resultActions = mockMvc.perform(multipart("/api/upload-entry-file").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = """
                [
                  {"Name":"Mia Williams","Transport":"Rides A Scooter"},
                  {"Transport":"Drives an SUV","Top Speed":11.5}
                ]
                """;

        JSONAssert.assertEquals(expectedJson, resultActions.getResponse().getContentAsString(), false);

        assertDatabaseEntry("GB", "IBM", HttpStatus.OK, ipAddress);
    }
}
