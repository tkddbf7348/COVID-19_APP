package com.example.covid_19_sangyul;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.amitshekhar.DebugDB;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;


public class COVID_API extends Activity {

    class ITEM {
        private String stdDay; // 기준 일시
        private String city; // 도시
        private String deathCnt; // 사망자 수
        private String defCnt; // 확진자 수
        private String isolIngCnt; // 격리 중
        private String overFlowCnt; // 해외유입 수
        private String localOccCnt; // 지역발생 수

        ITEM() {
            this.setCity("0");
            this.setStdDay("0");
            this.setDeathCnt("0");
            this.setDefCnt("0");
            this.setIsolIngCnt("0");
            this.setOverFlowCnt("0");
            this.setLocalOccCnt("0");
        }

        public String getstdDay() {
            return this.stdDay;
        }
        public void setStdDay(String stdDay) {
            this.stdDay = stdDay;
        }
        public String getCity() {
            return this.city;
        }
        public void setCity(String city) {
            this.city = city;
        }
        public String getDeathCnt() {
            return this.deathCnt;
        }
        public void setDeathCnt(String deathCnt) {
            this.deathCnt = deathCnt;
        }
        public String getDefCnt() {
            return this.defCnt;
        }
        public void setDefCnt(String defCnt) {
            this.defCnt = defCnt;
        }
        public String getIsolIngCnt() {
            return this.isolIngCnt;
        }
        public void setIsolIngCnt(String isolIngCnt) {
            this.isolIngCnt = isolIngCnt;
        }
        public String getOverFlowCnt() {
            return this.overFlowCnt;
        }
        public void setOverFlowCnt(String overFlowCnt) {
            this.overFlowCnt = overFlowCnt;
        }
        public String getLocalOccCnt() {
            return this.localOccCnt;
        }
        public void setLocalOccCnt(String localOccCnt) {
            this.localOccCnt = localOccCnt;
        }

        public void printvalue() {
            System.out.print("도시 : " + this.city +
                    " 사망자 수 : " + this.deathCnt + " " +
                    " 확진자 수 : " + this.defCnt + " " +
                    " 격리자 수 : " + this.isolIngCnt + " " +
                    " 지역 발생 수 : " + this.localOccCnt + " " +
                    " 해외 유입 수 : " + this.overFlowCnt + " " +
                    " 기준 일자 : " + this.stdDay);
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second);

        //db열고 테이블 생성
        DBOpenHelper db = new DBOpenHelper(this);
        db.open();
        db.create();

        try {
            //xml파싱 후 데이터 저장
            parsing_COVID19(db);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button button_parsing_return = (Button) findViewById(R.id.button_parsing_return);

        button_parsing_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //메인으로 넘어감
                Toast.makeText(COVID_API.this, "데이터 저장 완료", Toast.LENGTH_SHORT).show();
                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                 startActivity(intent);
            }
        });

    }

    public void parsing_COVID19(DBOpenHelper db) throws ParserConfigurationException, SAXException, IOException {
        String APIKey = "Y0zjhgf%2B9SgizMTPnOYM1mi3zSqZ7yxVmAscDxZmtxSpkTh7QfOIMMR5xIMZdByfu%2BOj5AXBaNNGzb2m3WXH%2Bg%3D%3D";
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String today = Integer.toString(year)
                + (Integer.toString(month).length() >= 2 ? Integer.toString(month)
                : Integer.toString(0) + Integer.toString(month))
                + (Integer.toString(day).length() >= 2 ? Integer.toString(day)
                : Integer.toString(0) + Integer.toString(day)); // 20210429


        //System.out.println("today : " + today);
        String urlBuilder = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19SidoInfStateJson" +
                "?" + URLEncoder.encode("ServiceKey", "UTF-8") + "=" + APIKey + /*Service Key*/
                "&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8") + /* page number */
                "&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8") + /**/
                "&" + URLEncoder.encode("startCreateDt", "UTF-8") + "=" + URLEncoder.encode(today, "UTF-8") + /* start date */
                "&" + URLEncoder.encode("endCreateDt", "UTF-8") + "=" + URLEncoder.encode(today, "UTF-8");/*URL*//*end date */
        URL url = new URL(urlBuilder);// url 생성

        new Thread() {
            public void run() {
                String xml = "";
                xml = xmlRequest(url); //url 쏴서 값 받아옴

                try {
                    parsingAndInsert(xml, db); //받아온 값 파싱 후 db에 저장
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public String xmlRequest(URL url) {

        String xml = "";

        try {
            //url 연결
            HttpURLConnection conn = null;
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            //System.out.println("Response code: " + conn.getResponseCode());
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            rd.close();
            conn.disconnect();
            xml = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println("xml : " + xml);

        return xml;
    }


    public void parsingAndInsert(String xml, DBOpenHelper db) throws ParserConfigurationException, IOException, SAXException {

        ITEM[] val = new ITEM[20]; //지역 별 데이터를 넣어줄 변수 생성
        for (int i = 0; i < 20; i++) { //변수 초기화
            val[i] = new ITEM();
        }

        InputSource is = new InputSource(new StringReader(xml));
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        Element root = document.getDocumentElement();
        NodeList children = root.getChildNodes(); // xml tag(into response) get


        for(int i = 0; i < children.getLength(); i++) { // in response
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element ele = (Element) node;
                String nodeName = ele.getNodeName();
                //System.out.println(nodeName);


                if (nodeName.equals("body")) { //in body
                    //System.out.println();
                    NodeList children_body = ele.getChildNodes();

                    for (int j = 0; j < children_body.getLength(); j++) {
                        Node node_body = children_body.item(j);
                        if (node_body.getNodeType() == Node.ELEMENT_NODE) {
                            Element ele_body = (Element) node_body;
                            String nodeName_body = ele_body.getNodeName();
                            //System.out.println(nodeName_body);


                            if (nodeName_body.equals("items")) { //in items
                                //System.out.println();
                                NodeList children_items = ele_body.getChildNodes();

                                for (int k = 0; k < children_items.getLength(); k++) {
                                    Node node_items = children_items.item(k);
                                    if (node_items.getNodeType() == Node.ELEMENT_NODE) {
                                        Element ele_items = (Element) node_items;
                                        String nodeName_items = ele_items.getNodeName();
                                        //System.out.println(nodeName_items);


                                        NodeList children_item = ele_items.getChildNodes(); //in item

                                        for (int m = 0; m < children_item.getLength(); m++) {
                                            Node node_item = children_item.item(m);
                                            if (node_item.getNodeType() == Node.ELEMENT_NODE) {
                                                Element ele_item = (Element) node_item;
                                                //System.out.println(ele_item.getNodeName() + " : " + ele_item.getTextContent()); // createDt : ~~~  gubun : ~~~......etc

                                                switch (ele_item.getNodeName()) { //넘겨줄 변수에 값 대입
                                                    case ("stdDay"):
                                                        val[k].setStdDay(ele_item.getTextContent());
                                                        break;
                                                    case ("gubun"):
                                                        val[k].setCity(ele_item.getTextContent());
                                                        break;
                                                    case ("deathCnt"):
                                                        val[k].setDeathCnt(ele_item.getTextContent());
                                                        break;
                                                    case ("defCnt"):
                                                        val[k].setDefCnt(ele_item.getTextContent());
                                                        break;
                                                    case ("isolIngCnt"):
                                                        val[k].setIsolIngCnt(ele_item.getTextContent());
                                                        break;
                                                    case ("overFlowCnt"):
                                                        val[k].setOverFlowCnt(ele_item.getTextContent());
                                                        break;
                                                    case ("localOccCnt"):
                                                        val[k].setLocalOccCnt(ele_item.getTextContent());
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        }
                                    }

                                    //여기서부터 DB에 데이터 삽입
                                    db.insertColumn(val[k].city, val[k].stdDay, val[k].overFlowCnt, val[k].localOccCnt, val[k].isolIngCnt, val[k].defCnt, val[k].deathCnt);
//                                    val[k].printvalue();
//                                    System.out.println();
//                                    System.out.println();
//                                    System.out.println();
                                }
                            }
                        }
                    }
                }
            }
        }

        showDatabase("_id", db);
    }

    public void showDatabase(String sort, DBOpenHelper db){
        Cursor iCursor = db.sortColumn(sort);
        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
        String res = "";
        while(iCursor.moveToNext()){
            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
            String tempCity = iCursor.getString(iCursor.getColumnIndex("City"));
            tempCity = setTextLength(tempCity,10);
            String tempstdDay = iCursor.getString(iCursor.getColumnIndex("stdDay"));
            tempstdDay = setTextLength(tempstdDay,10);
            String tempoverFlowCnt = iCursor.getString(iCursor.getColumnIndex("overFlowCnt"));
            tempoverFlowCnt = setTextLength(tempoverFlowCnt,10);
            String templocalOccCnt = iCursor.getString(iCursor.getColumnIndex("localOccCnt"));
            templocalOccCnt = setTextLength(templocalOccCnt,10);
            String tempisolIngCnt = iCursor.getString(iCursor.getColumnIndex("isolIngCnt"));
            tempisolIngCnt = setTextLength(tempisolIngCnt,10);
            String tempdefCnt = iCursor.getString(iCursor.getColumnIndex("defCnt"));
            tempdefCnt = setTextLength(tempdefCnt,10);
            String tempdeathCnt = iCursor.getString(iCursor.getColumnIndex("deathCnt"));
            tempdeathCnt = setTextLength(tempdeathCnt,10);

            res = tempCity + tempstdDay + tempoverFlowCnt + templocalOccCnt + tempisolIngCnt + tempdefCnt + tempdeathCnt;
        }
        Log.d("showDatabase", "데이터베이스 : "  + res);
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text += " ";
            }
        }
        return text;
    }

}
