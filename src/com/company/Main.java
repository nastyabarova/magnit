package com.company;
import java.sql.*;
import java.io.*;
import java.lang.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.w3c.dom.*;

public class Main {
    private int N;
    private String DB_URL;
    private String DB_Driver;

    public int getN(){
        return N;
    }
    public void setN(int N){
        this.N=N;
    }

    public String get_DB_URL(){
        return DB_URL;
    }

    public void set_DB_URL(String DB_URL){
        this.DB_URL=DB_URL;
    }

    public String get_DB_DriverL(){
        return DB_Driver;
    }

    public void set_DB_Driver(String DB_Driver){
        this.DB_Driver=DB_Driver;
    }

    public static void main(String[] args) throws IOException, TransformerException{
        Main param=new Main();
        param.setN(1048576);
        String dir = System.getProperty("user.dir").replaceAll("\\\\", "/")+"/db/test";
        param.set_DB_URL("jdbc:h2:/"+dir);
        param.set_DB_Driver("org.h2.Driver");
        try {
            Class.forName(param.get_DB_DriverL());
            Connection connection = DriverManager.getConnection(param.get_DB_URL());
            Statement statement = connection.createStatement();

            //2.
            statement.execute("Truncate table test");
            for (int i=0; i<param.getN(); i++){
                String sql="INSERT INTO test (field) VALUES ("+(i+1)+")";
                statement.executeUpdate(sql);
            }
            System.out.println("Таблица заполнена");

            //3.
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("entries");
                doc.appendChild(rootElement);

                Element entry = doc.createElement("entry");
                rootElement.appendChild(entry);

                String sql = "SELECT field FROM test";
                ResultSet res = statement.executeQuery(sql);
                while(res.next()){
                    int field  = res.getInt("field");
                    String str_field = Integer.toString(field);
                    Element field1 = doc.createElement("field");
                    field1.appendChild(doc.createTextNode(str_field));
                    entry.appendChild(field1);
                }

                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(new File("xml\\1.xml"));
                transformer.transform(source, result);
                System.out.println("1.xml создан");
            } catch (ParserConfigurationException | TransformerException pce) {
                pce.printStackTrace();
            }
            connection.close();
            statement.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("JDBC драйвер не найден");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ошибка SQL");
        }
            //4.
            TransformerFactory transformerFactory=TransformerFactory.newInstance();
            Source xslDoc=new StreamSource("xml/transform.xsl");
            Source xmlDoc=new StreamSource("xml/1.xml");
            String outputFileName="xml/2.xml";
            OutputStream htmlFile=new FileOutputStream(outputFileName);
            Transformer transformer=transformerFactory.newTransformer(xslDoc);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlDoc, new StreamResult(htmlFile));
            System.out.println("2.xml создан");

            //5.
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            try {
                builder = factory.newDocumentBuilder();
                Document doc = builder.parse(outputFileName);
                doc.getDocumentElement().normalize();
                NodeList nodeList = doc.getElementsByTagName("entry");
                ArrayList<Integer> fields = new ArrayList<>();

                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node nNode = nodeList.item(i);
                    Element eElement = (Element) nNode;
                    Integer int_field = Integer.valueOf(eElement.getAttribute("field"));
                    fields.add(int_field);
                }
                long sum=0;
                for (int i=0; i<fields.size(); i++){
                    sum=sum+fields.get(i);
                }
                System.out.println("Сумма чисел от 1 до "+ param.getN()+" равна "+sum);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
