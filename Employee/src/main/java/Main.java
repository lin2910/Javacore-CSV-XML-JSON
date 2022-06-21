import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // CSV to JSON
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileNameCSV = "data.csv";
        String filenameXML = "data.xml";
        String resultName = "data.json";
        String resultName2 = "data2.json";

        List<Employee> list = parseCSV(columnMapping, fileNameCSV);

        String json = listToJson(list);

        writeString(json, resultName);

        // XML to JSON
        try {
            list = parseXML(filenameXML);
        } catch (Exception e) {
            System.out.println();
        }
        json = listToJson(list);
        writeString(json, resultName2);

        // JSON парсер
        json = readString(resultName2);
        list = jsonToList(json);
        list.stream().forEach(System.out::println);
    }


    public static List<Employee> parseCSV (String[] columnMapping, String fileName) {
        List<Employee> staff = null;
        try (CSVReader csvReader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            staff = csv.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return staff;
    }


    public static String listToJson (List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        return gson.toJson(list, listType);
    }

    public static void writeString (String json, String fileName) {
        try(Writer writer = new FileWriter(fileName)) {
            writer.write(json);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Employee> parseXML (String fileName) throws Exception {
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList nodeList = doc.getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (Node.ELEMENT_NODE == node.getNodeType()) {
                Element element = (Element) node;
                Employee employee = new Employee();
                employee.id = Long.parseLong(getValue(element, "id"));
                employee.firstName = getValue(element, "firstName");
                employee.lastName = getValue(element, "lastName");
                employee.country = getValue(element, "country");
                employee.age = Integer.parseInt(getValue(element, "age"));
                staff.add(employee);
            }
        }
        return staff;
    }

    public static String getValue (Element element, String tag) {
        NodeList nodeList1 = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node1 = (Node) nodeList1.item(0);
        return node1.getNodeValue();
    }



    public static String readString (String fileName) {
        StringBuilder str = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while (reader.ready()) {
                str.append(reader.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public static List<Employee> jsonToList (String json){
        List<Employee> list = new ArrayList<>();
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        list = gson.fromJson(json, listType);
        return list;
    }
}
