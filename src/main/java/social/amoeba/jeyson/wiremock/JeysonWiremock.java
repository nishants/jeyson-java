package social.amoeba.jeyson.wiremock;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import social.amoeba.jeyson.Jeyson;
import social.amoeba.jeyson.Json;
import wiremock.com.fasterxml.jackson.databind.ObjectMapper;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JeysonWiremock extends ResponseDefinitionTransformer {

  @Override
  public ResponseDefinition transform(Request request,
                                      ResponseDefinition responseDefinition,
                                      FileSource files,
                                      Parameters parameters) {

    String bodyFileName         = responseDefinition.getBodyFileName();
    String templatesPath        = files.getPath();
    Map requestBody             = Collections.emptyMap();
    boolean isJsonFileResponse  = bodyFileName == null ? false : bodyFileName.endsWith(".json"),
            isJsonRequest       = request.getHeaders().getContentTypeHeader().mimeTypePart().equalsIgnoreCase("application/json");

    ResponseDefinitionBuilder response =
        new ResponseDefinitionBuilder()
            .like(responseDefinition);

    if(isJsonFileResponse){
      try {
        if(isJsonRequest){
          requestBody = Json.parse(new String(request.getBody()), Map.class);
        }
        response.withHeader("Content-Type", "application/json");
        response = response.withBody(parse(requestBody, templatesPath, readFile(templatesPath, bodyFileName)));
      }  catch (Exception e) {
        String errorMessage = "************* Jeyson Error *******************"  + System.getProperty("line.separator");
        errorMessage += "bodyFile : :bodyFile, templatesPath :  :templatesPath" + System.getProperty("line.separator");
        errorMessage += e.getMessage();
        System.err.println(errorMessage);
      }
    }
    return response.build();
  }

  private String parse(Map requestBody, String templatesPath, String template) throws URISyntaxException, NoSuchMethodException, ScriptException, IOException {
    template = Json.stringify(Json.parse(template, Map.class));
    HashMap scope = new HashMap();
    HashMap reqeust = new HashMap();
    reqeust.put("body", requestBody);
    scope.put("request", reqeust);

    Map compiled = new Jeyson(templatesPath).compile(scope, template);
    return new ObjectMapper().writeValueAsString(compiled);
  }

  private String readFile(String path, String filename) throws IOException {
    return Json.stringify(new ObjectMapper().readValue(new File(path, filename), Map.class).get("body"));
  }


  public String getName() {
    return "Jeyson";
  }
}