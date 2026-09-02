package org.example.simplellmcalculator.controller;

import org.example.simplellmcalculator.entity.CalcRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.example.simplellmcalculator.util.LLMApiKey.API_KEY;

@RestController
@RequestMapping("/calc")
public class CalcController {

  @PostMapping
  public ResponseEntity<String> evaluateExpression(@RequestBody CalcRequest calcRequest) throws Exception{
    final String CHAT_COMPLETIONS_URL =
        "https://open.bigmodel.cn/api/paas/v4/chat/completions";

    String requestBody = """
                {"model":"glm-4.6V","messages":[{"role":"user","content":"%s? Just give me the answer."}]}
                """.formatted(calcRequest.getExpr());
//        String requestBody = """
//                {"model":"glm-4.6V","messages":[{"role":"user","content":"(1+3)*6/3=?"}]}
//                """;


    HttpClient httpClient = HttpClient.newHttpClient();

    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(CHAT_COMPLETIONS_URL))
        .header("Authorization", "Bearer " + API_KEY)
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
        .build();

    HttpResponse<String> response =
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    System.out.println(response.statusCode());
    System.out.println(response.body());

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode rootNode = objectMapper.readTree(response.body());

    String result = rootNode.path("choices").get(0).path("message").path("content").asText().substring(1);
    return ResponseEntity.ok(result);
  }

}
