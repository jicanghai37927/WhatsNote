package club.andnext.xsltml;

import android.content.Context;
import android.text.TextUtils;
import club.andnext.utils.AssetUtils;
import club.andnext.utils.FileUtils;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class MathMLTransformer {

    static final String VERSION = "2.1.2.M";

    static final String[] FILES = {
            "mmltex.xsl",
            "cmarkup.xsl",
            "entities.xsl",
            "glayout.xsl",
            "scripts.xsl",
            "tables.xsl",
            "tokens.xsl",
    };

    Context context;

    Transformer transformer;
    MathMLErrorListener errorListener;

    public MathMLTransformer(Context context) {
        this.context = context;

        this.errorListener = new MathMLErrorListener();
    }

    public String transform(String mathml) throws TransformerException {
        if (TextUtils.isEmpty(mathml)) {
            return "";
        }

        if (transformer == null) {
            this.export();

            TransformerFactory tFac = TransformerFactory.newInstance();
            Source xslt = new StreamSource(this.getSystemId());
            Transformer t = tFac.newTransformer(xslt);

            this.transformer = t;

            if (transformer != null) {
                transformer.setErrorListener(errorListener);
            }
        }

        if (transformer == null) {
            return "";
        }

        errorListener.isError = false;

        StringWriter outLatex = new StringWriter(mathml.length() + 1024);

        Source source = new StreamSource(new StringReader(mathml));
        Result result = new StreamResult(outLatex);

        transformer.reset();
        transformer.transform(source, result);

        if (errorListener.isError) {
            return null;
        }

        String text = outLatex.toString();
        if (TextUtils.isEmpty(text)) {
            return text;
        }

        return getResult(text);
    }

    public File getSystemId() {

        File file = getDir();
        file = new File(file, "mmltex.xsl");

        return file;
    }

    public void export() {
        String root = "xsltml/" + VERSION;
        File dir = getDir();

        for (String name : FILES) {
            String source = root + "/" + name;
            File file = new File(dir, name);
            if (!file.exists()) {
                AssetUtils.copyToFile(context, source, file);
            }
        }
    }

    public void clean() throws IOException {
        File file = context.getExternalFilesDir("xsltml");

        FileUtils.forceDelete(file);
    }

    File getDir() {
        File file = context.getExternalFilesDir("xsltml");
        file = new File(file, VERSION);
        file.mkdirs();

        return file;
    }

    String getResult(String text) {
        String start = "\\[\n";
        String stop = "\n\\]";

        int begin = text.indexOf(start);
        int end = text.lastIndexOf(stop);
        if (begin < 0 || end < 0) {
            return "";
        }

        begin += start.length();
        if (begin >= end) {
            return "";
        }

        String str = text.substring(begin, end);
        str = str.trim();

        return str;
    }

    public static final boolean isMathML(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        {
            int pos = text.lastIndexOf("math>");
            if (pos >= 0) {
                return true;
            }
        }

        {
            int pos = text.indexOf("<math");
            if (pos >= 0) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    private class MathMLErrorListener implements ErrorListener {

        boolean isError = false;

        @Override
        public void warning(TransformerException exception) throws TransformerException {

        }

        @Override
        public void error(TransformerException exception) throws TransformerException {
            this.isError = true;
        }

        @Override
        public void fatalError(TransformerException exception) throws TransformerException {
            this.isError = true;
        }
    }
}
