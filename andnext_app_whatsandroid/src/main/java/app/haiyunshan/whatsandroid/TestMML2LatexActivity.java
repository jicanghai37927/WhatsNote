package app.haiyunshan.whatsandroid;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.xsltml.MathMLTransformer;
import fmath.conversion.ConvertFromMathMLToLatex;
import org.apache.commons.io.output.StringBuilderWriter;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

public class TestMML2LatexActivity extends AppCompatActivity {

    MathMLTransformer mathML;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_mml2_latex);
    }

    void onConvertClick(View view) {
        EditText edit = findViewById(R.id.edit_text);
        String str = edit.getText().toString();

        String result;
        if (false) {
            result = byFMath(str);
        } else {
            result = byXSLTML(str);
        }

        Log.w("AA", "latext = " + result);
    }

    String byFMath(String text) {
        String result = ConvertFromMathMLToLatex.convertToLatex(text);
        return result;
    }

    String byXSLTML(String text) {
        String result = text;

        if (mathML == null) {
            mathML = new MathMLTransformer(this);
        }

        try {
            result = mathML.transform(text);
        } catch (TransformerException e) {
            result = null;
        }

        if (result == null) {
            result = "转化出现错误";
        }

        return result;
    }

    public static String transform(String inputXML, File xslFile) throws TransformerException {

        StringWriter outLatex = new StringWriter(inputXML.length() + 1024);

        Source source = new StreamSource(new StringReader(inputXML));
        Result result = new StreamResult(outLatex);

        TransformerFactory tFac = TransformerFactory.newInstance();
        Source xslt = new StreamSource(xslFile);
        Transformer t = tFac.newTransformer(xslt);

        if (t != null) {
            t.transform(source, result);
        }

        return (t == null)? null: outLatex.toString();

    }
}
