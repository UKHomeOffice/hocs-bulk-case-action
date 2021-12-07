package uk.gov.digital.ho.hocs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static net.logstash.logback.argument.StructuredArguments.value;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CloseCaseTest {

    @Mock
    private HttpClient client;

    @Mock
    private CloseCase closeCase;

    @Mock
    private Logger logger;

    @Mock
    private HttpResponse response;

    private String basicAuth = "basicAuth";

    @BeforeEach
    public void setup() {
        doCallRealMethod().when(closeCase).setLogger(any());
        closeCase.setLogger(logger);
    }

    @Test
    public void testReadInvalidUUID() {
        doCallRealMethod().when(closeCase).processLine(any());
        closeCase.processLine("notauuid");
        verify(logger).error("Invalid uuid {}", "notauuid", value("exception", "INVALID_UUID"));
        verify(closeCase, never()).callCloseEndpoint(any(), any());
    }

    @Test
    public void testReadGoodUUID() {
        doCallRealMethod().when(closeCase).processLine(any());
        when(closeCase.callCloseEndpoint(any(), any())).thenReturn("output");
        closeCase.processLine("58f7179b-d88e-47f6-9867-da648ba89c10,,,,,");
        verify(closeCase).callCloseEndpoint("58f7179b-d88e-47f6-9867-da648ba89c10", null);
        verify(logger).info("Response: output");
    }

    @Test
    public void testCallEndPointSucessfully() throws IOException, InterruptedException {
        when(response.body()).thenReturn("body");
        when(client.send(any(), any())).thenReturn(response);

        CloseCase closeCaseReal = new CloseCase("http://workflow", "xAuthGroups",
                "xAuthUserId", 0, "filePath", "basicAuth", client);

        closeCaseReal.callCloseEndpoint("1bf5f228-84e3-44f2-9655-5fc3df8350b4", client);

        var request = HttpRequest.newBuilder(
                URI.create("http://workflow/case/close/" + "1bf5f228-84e3-44f2-9655-5fc3df8350b4"))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("accept", "application/json")
                .header("X-Auth-Groups", "xAuthGroups")
                .header("X-Auth-UserId", "xAuthUserId")
                .header("Authorization", getBasicAuth())
                .build();

        verify(client).send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    public void testCallEndPointUnsucessfully() throws IOException, InterruptedException {
        IOException exception = new IOException();
        doThrow(exception).when(client).send(any(), any());

        CloseCase closeCaseReal = new CloseCase("http://workflow", "xAuthGroups",
                "xAuthUserId", 0, "filePath", "basicAuth", client);

        closeCaseReal.setLogger(logger);

        closeCaseReal.callCloseEndpoint("1bf5f228-84e3-44f2-9655-5fc3df8350b4", client);

        verify(logger).error("Error processing uuid {}", "1bf5f228-84e3-44f2-9655-5fc3df8350b4", value("exception", exception));
    }

    @Test
    public void testFileReading() {
        doCallRealMethod().when(closeCase).readFile(any());
        closeCase.readFile("src/test/resources/testinput.csv");
        verify(closeCase).processLine("caseUuid,,,,");
        verify(closeCase).processLine("ffd2d712-8ecd-4029-ba68-ff865fe96d9a,,,,");
        verify(closeCase).processLine("88de8a2b-593a-41c4-8080-cbd8d12c3148,,,,");
        verify(closeCase).processLine("2b5cd9d3-8964-47af-9f23-a86c81e449b3,,,,");
        verify(closeCase).processLine("cf099158-41cf-4ca8-909c-24ab9b0cebd5,,,,");
    }

    private String getBasicAuth() {
        return String.format("Basic %s", Base64.getEncoder().encodeToString(basicAuth.getBytes(StandardCharsets.UTF_8)));
    }

}
