package sg.sph.test.core.network.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import sg.sph.test.core.network.data.Record;

public class RecordParser
{
  private static final String PROP_RESULT = "result";
  private static final String PROP_RECORDS = "records";

  public List<Record> parse(String rawData) throws JSONException
  {
    List<Record> result = null;

    JSONObject object = new JSONObject(rawData);
    JSONObject resultObject = object.optJSONObject(PROP_RESULT);
    if (resultObject != null)
    {
      JSONArray recordArray = resultObject.optJSONArray(PROP_RECORDS);

      if (recordArray != null)
      {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Record>>() {}.getType();

        result = gson.fromJson(recordArray.toString(), type);
      }
    }
    return result == null ? new ArrayList<>(0) : result;
  }
}
