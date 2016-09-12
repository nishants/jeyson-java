package social.amoeba.jeyson.wiremock.request;

import org.junit.Test;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static social.amoeba.TestSupport.jsonRequestBody;
import static social.amoeba.TestSupport.xmlRequestBody;
import static social.amoeba.jeyson.wiremock.request.JSON.parseJSON;
import static social.amoeba.jeyson.wiremock.request.XML.parseXML;

public class RequestReaderTest {
  @Test
  public void parseJsonRequest() throws IOException {
    Map actual        = RequestReader.read(jsonRequestBody("{'body' : {'message' : 'hello'}}")),
        expected      = parseJSON("{'body' : {'message' : 'hello'}}");

    assertThat(actual, is(expected));
  }

  @Test
  public void parseXMLRequest() throws IOException {
    Map actual        = RequestReader.read(xmlRequestBody("<body><message>hello</message></body>")),
        expected      = parseXML("<body><message>hello</message></body>");

    assertThat(actual, is(expected));
  }

}