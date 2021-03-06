package panels;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import Helper.Emoji;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MicrosoftPanel extends ImagePanel {
    private static final String url = "https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize";
    private static final String key1 = "XXXX";

    public MicrosoftPanel(){
        super("azure.png", 153);
    }

	public void detectFaces() {
		try {
			HttpClient httpclient = HttpClients.createDefault();

			URIBuilder uriBuilder = new URIBuilder(url);

			URI uri = uriBuilder.build();
			HttpPost request = new HttpPost(uri);

			request.setHeader("Content-Type", "application/octet-stream");
			request.setHeader("Ocp-Apim-Subscription-Key", key1);

			FileEntity fileEnt = new FileEntity(new File("test.jpg"));

			request.setEntity(fileEnt);
			HttpResponse response = httpclient.execute(request);
			HttpEntity entity = response.getEntity();

			if (entity != null) {
				String jsonResult = EntityUtils.toString(entity);
				System.out.println("MICROSOFT");
				System.out.println(jsonResult);

				drawToBackground(parseResult(jsonResult));
			}
		}catch (Exception e){
		}
	}


	private ArrayList<Emoji> parseResult(String jsonResult){
		JSONArray json = new JSONArray(jsonResult);
		ArrayList<Emoji> overlays = new ArrayList<>();
		//get all recognized faces
		for (int i = 0; i < json.length(); i++) {
			JSONObject object = json.getJSONObject(i);

			//containes scores of face emotions
			JSONObject scores = object.getJSONObject("scores");

			double max = 0.0;
			int emotion = 0;
			int j = 0;
			for (Iterator iterator = scores.keys(); iterator.hasNext(); ) {
				String key = (String) iterator.next();
				double actual = scores.getDouble(key);
				if (actual > max) {
					max = actual;
					emotion = j;
				}
				j++;
			}

			String filename = "neutral.png";
			switch (emotion) {
				case 0:
					filename = "contempt.png";
					break;
				case 1:
					filename = "surprise.png";
					break;
				case 2:
					filename = "happy.png";
					break;
				case 3:
					filename = "neutral.png";
					break;
				case 4:
					filename = "sad.png";
					break;
				case 5:
					filename = "disgust.png";
					break;
				case 6:
					filename = "anger.png";
					break;
				case 7:
					filename = "fear.png";
					break;

				default:
					break;
			}

			//contains bounding box of faces
			JSONObject faceRectangle = object.getJSONObject("faceRectangle");

			overlays.add(new Emoji(faceRectangle.getInt("left"), faceRectangle.getInt("top"), faceRectangle.getInt("width"), faceRectangle.getInt("height"), filename));
		}

		return overlays;
	}
}