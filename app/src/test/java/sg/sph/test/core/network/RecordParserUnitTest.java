package sg.sph.test.core.network;

import com.google.gson.JsonParseException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import sg.sph.test.core.network.data.Record;
import sg.sph.test.core.network.parser.RecordParser;

import static junit.framework.TestCase.*;

@RunWith(MockitoJUnitRunner.class)
public class RecordParserUnitTest
{
  private RecordParser mParser = new RecordParser();

  @Test
  public void testNull() throws JSONException
  {
    List<Record> result = mParser.parse(null);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testNoRecord() throws JSONException
  {
    List<Record> result = mParser.parse("{}");
    assertTrue(result.isEmpty());
  }

  @Test
  public void testEmptyRecords() throws JSONException
  {
    List<Record> result = mParser.parse("{\"result\":{\"records\":[]}}");
    assertTrue(result.isEmpty());
  }

  @Test
  public void testRecods() throws JSONException
  {
    List<Record> result = mParser.parse("{\"result\":{\"records\":[{\"quarter\":\"2008-Q3\", \"volume_of_mobile_data\":\"0.85\"}]}}");
    assertEquals(1, result.size());
    assertEquals("2008-Q3", result.get(0).getQuarter());
    assertEquals(0.85, result.get(0).getVolume());
  }

  @Test(expected = JSONException.class)
  public void testInvalidJSON() throws JSONException
  {
    mParser.parse(".{}");
  }
}
