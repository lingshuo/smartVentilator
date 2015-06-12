package cn.lisa.smartventilator.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import android.util.Xml;
import cn.lisa.smartventilator.bean.Radio;

public class RadioXmlParser {
	public List<Radio> parse(InputStream is) throws Exception {  
        List<Radio> Radios = null;  
        Radio Radio = null;  
          
//      XmlPullParserFactory factory = XmlPullParserFactory.newInstance();  
//      XmlPullParser parser = factory.newPullParser();  
          
        XmlPullParser parser = Xml.newPullParser(); //��android.util.Xml����һ��XmlPullParserʵ��  
        parser.setInput(is, "UTF-8");               //���������� ��ָ�����뷽ʽ  
  
        int eventType = parser.getEventType();  
        while (eventType != XmlPullParser.END_DOCUMENT) {  
            switch (eventType) {  
            case XmlPullParser.START_DOCUMENT:  
                Radios = new ArrayList<Radio>();  
                break;  
            case XmlPullParser.START_TAG:  
            	if (parser.getName().equals("radio")) {  
                    Radio = new Radio();  
                } else if(parser.getName().equals("id")){
                	eventType = parser.next();
                	Radio.setId(Integer.parseInt(parser.getText()));
                }else if (parser.getName().equals("name")) {  
                    eventType = parser.next();  
                    Radio.setName(parser.getText());  
                } else if (parser.getName().equals("url")) {
                    eventType = parser.next();  
                    Radio.setUrl(parser.getText());  
                }  
                break;  
            case XmlPullParser.END_TAG:  
                if (parser.getName().equals("radio")) {  
                    Radios.add(Radio);  
                    Radio = null;      
                }  
                break;  
            }
            eventType = parser.next();  
        }  
        return Radios;  
    }  
}
