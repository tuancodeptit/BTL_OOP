import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class three_day {
    public static void main(String[] args) {
        try {
            // Lấy dữ liệu thời tiết từ OpenWeatherMap API
            URL url = new URL(
                    "https://api.openweathermap.org/data/2.5/forecast?q=hanoi&appid=44d944f4fa48537c8e6328e57a9a8bd0&units=metric");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Phân tích cú pháp dữ liệu JSON
            JSONObject jsonObj = new JSONObject(response.toString());
            JSONArray list = jsonObj.getJSONArray("list");

            // Lặp qua thông tin thời tiết cho 3 ngày tiếp theo
            for (int i = 0; i < 3; i++) {
                JSONObject dayObj = list.getJSONObject(i * 8); // mỗi ngày có 8 mục thông tin, nên lấy mục thông tin thứ
                                                               // 0, 8, 16, ...
                long timestamp = dayObj.getLong("dt"); // timestamp thời điểm dự báo
                double temp = dayObj.getJSONObject("main").getDouble("temp"); // nhiệt độ
                JSONArray weatherArr = dayObj.getJSONArray("weather");

                // Cập nhật thông tin thời tiết vào HTML cho mỗi ngày
                String htmlString = "";
                try {
                    htmlString = new String(Files.readAllBytes(Paths.get("./html.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlString);
                doc.select(".day" + (i + 1) + "-temp").html(temp + "°C");
                String newHtmlString = doc.outerHtml();

                // Ghi thông tin vào tệp HTML
                try {
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(Files.newOutputStream(Paths.get("./html.html")), "UTF-8"));
                    writer.write(newHtmlString);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Cập nhật thông tin thời tiết thành công!");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
