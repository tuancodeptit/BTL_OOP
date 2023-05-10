import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.nio.file.Files;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;


public class WeatherAPI {  
    private String apiKey;
    public WeatherAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public void getWeatherInformation(String cityName) {
        try {
            URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + apiKey + "&units=metric&lang=vi");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            //kiem tra ket noi
            int responseCode = conn.getResponseCode();

            // 200 OK
            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {

                StringBuilder informationString = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    informationString.append(scanner.nextLine());
                }
                //Close the scanner
                scanner.close();
                //Thư viện JSON đơn giản Thiết lập với Maven được sử dụng để chuyển đổi chuỗi thành JSON
                JSONParser parser = new JSONParser();
                JSONObject dataObject = (JSONObject) parser.parse(String.valueOf(informationString));

                //Lấy thông tin thời tiết từ đối tượng JSON
                JSONObject mainObject = (JSONObject) dataObject.get("main");
                Double temperature = ((Number) mainObject.get("temp")).doubleValue();
                //Double precipitation = ((Number) mainObject.get("rain")).doubleValue();
                JSONObject windObject = (JSONObject) dataObject.get("wind");
                Double humidity = ((Number) mainObject.get("humidity")).doubleValue();
                Double windSpeed = ((Number) windObject.get("speed")).doubleValue();

                System.out.println("Humidity: " + humidity + " %");
                System.out.println("Wind speed: " + windSpeed + " km/h");
                System.out.println("Temperature in " + cityName + " is " + temperature + "°C");
               //System.out.println("Precipitation: " + precipitation + " mm")

                // Cập nhật thông tin thời tiết vào HTML
                String htmlString = "";
                try {
                    htmlString = new String(Files.readAllBytes(Paths.get("./html.html")));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(htmlString);
                doc.select(".value").html(temperature + "°C");
                doc.select(".value2").html(humidity + "%");
                doc.select(".value3").html(windSpeed + "km/h");
                String newHtmlString = doc.outerHtml();

                // Lưu kết quả vào file html.html
                try {
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                            Files.newOutputStream(Paths.get("./html.html")), "UTF-8"));
                    writer.write(newHtmlString);
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }  
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter API key: ");
            String apiKey = scanner.nextLine();
            System.out.print("Enter city name: ");
            String cityName = scanner.nextLine();
            scanner.close();

            WeatherAPI weatherAPI = new WeatherAPI(apiKey);
            weatherAPI.getWeatherInformation(cityName);

            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
//44d944f4fa48537c8e6328e57a9a8bd0
