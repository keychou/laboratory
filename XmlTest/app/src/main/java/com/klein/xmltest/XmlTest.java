package com.klein.xmltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class XmlTest extends AppCompatActivity {

    private TextView tvXMLCreate;
    private TextView tvXMLResolve;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xml_test);

        tvXMLCreate = (TextView)findViewById(R.id.tvXMLCreate);
        tvXMLResolve = (TextView)findViewById(R.id.tvXMLResolve);


        String xml = "";
        XMLDom xmlDom = new XMLDom(this);
        xml = xmlDom.XMLCreate();
        tvXMLCreate.setText(xml);


        xml = xmlDom.XMLResolve();
        tvXMLResolve.setText(xml);
    }
}
