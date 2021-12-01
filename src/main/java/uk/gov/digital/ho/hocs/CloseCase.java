package uk.gov.digital.ho.hocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Stream;

import static net.logstash.logback.argument.StructuredArguments.value;

@Service
public class CloseCase {

    private Logger logger = LoggerFactory.getLogger(CloseCase.class);

    private final String workflowAddress;
    private final String xAuthGroups;
    private final String xAuthUserId;
    private final int gapBetweenUpdates;
    private final HttpClient client;
    private final String filePath;
    private final String basicAuth;

    public CloseCase(@Value("${workflow-service}") String workflowAddress,
                     @Value("${x-auth-groups}") String xAuthGroups,
                     @Value("${x-auth-userId}") String xAuthUserId,
                     @Value("${gap-between-updates}") int gapBetweenUpdates,
                     @Value("${file-path}") String filePath,
                     @Value("${hocs.basic-auth}") String basicAuth,
                     HttpClient client){

        this.workflowAddress = workflowAddress + "/case/close/";
        this.xAuthGroups = xAuthGroups;
        this.xAuthUserId = xAuthUserId;
        this.gapBetweenUpdates = gapBetweenUpdates;
        this.client = client;
        this.filePath = filePath;
        this.basicAuth = basicAuth;
    }

    @PostConstruct
    public void runService(){
        readFile(filePath);
    }

    public void readFile(String file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null.");
        }

        Path path = new File(file).toPath();

        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(this::processLine);
        } catch (IOException e) {
            logger.error("Error processing line with exception {}" , e, value("exception", e));
        }
    }

    public void processLine(String line){
        line = line.strip().replace(",", "");
        if(checkIfValidUUID(line)){
            String output = callCloseEndpoint(line, client);
            logger.info("Response: " + output);
            sleep();
        }
        else{
            logger.error("Invalid uuid {}", line, value("exception", "INVALID_UUID"));
        }
    }

    private boolean checkIfValidUUID(String stringUuid){
        try {
            UUID goodUuid = UUID.fromString(stringUuid);
        } catch (Exception e){
            return false;
        }
        return true;
    }

    private void sleep(){
        try {
            Thread.sleep(gapBetweenUpdates);
        } catch (InterruptedException e) {
            logger.error("Sleep error {}", e, value("exception", e));
        }
    }

    public String callCloseEndpoint(String uuid, HttpClient client){
        var request = HttpRequest.newBuilder(
                URI.create(workflowAddress + uuid))
                .PUT(HttpRequest.BodyPublishers.noBody())
                .header("accept", "application/json")
                .header("X-Auth-Groups", xAuthGroups)
                .header("X-Auth-UserId", xAuthUserId)
                .header("Authorization", getBasicAuth())
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException | InterruptedException e) {
            logger.error("Error processing uuid {}" , uuid, value("exception", e));
            return null;
        }
    }

    public void setLogger(Logger logger){
        this.logger = logger;
    }

    private String getBasicAuth() { return String.format("Basic %s", Base64.getEncoder().encodeToString(basicAuth.getBytes(StandardCharsets.UTF_8))); }

}
