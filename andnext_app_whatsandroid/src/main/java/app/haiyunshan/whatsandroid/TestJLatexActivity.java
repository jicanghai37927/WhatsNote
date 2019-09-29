package app.haiyunshan.whatsandroid;

import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import club.andnext.utils.AssetUtils;
import ru.noties.jlatexmath.JLatexMathView;

import java.io.IOException;
import java.util.ArrayList;

public class TestJLatexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_jlatex);

        JLatexMathView view = (findViewById(R.id.latex_view));
        String latex = get05();
        view.setLatex(latex);

        Log.w("AA", "latex = " + latex);
        print();
    }

    void print() {
        ArrayList<String> list = new ArrayList<>();
        list.add(get01());
        list.add(get02());
        list.add(get03());
        list.add(get04());
        list.add(get05());
        list.add(get06());
        list.add(get07());
        list.add(get08());

        for (String s : list) {
            Log.w("AA", s);
        }
    }

    StringBuffer getMathML() {
        String str = AssetUtils.getString(this, "mathml/1.mml");

        return new StringBuffer(str);
    }

    String get01() {

        String latex = "\\frac{\\partial f}{\\partial x} = 2\\,\\sqrt{a}\\,x";
//        latex = "\\frac{\\partial f}{\\partial x} = 2\\,\\sq";

        return latex;
    }

    String get02() {

        String latex = "\\begin{array}{l}";
        latex += "\\forall\\varepsilon\\in\\mathbb{R}_+^*\\ \\exists\\eta>0\\ |x-x_0|\\leq\\eta\\Longrightarrow|f(x)-f(x_0)|\\leq\\varepsilon\\\\";
        latex += "\\det\\begin{bmatrix}a_{11}&a_{12}&\\cdots&a_{1n}\\\\a_{21}&\\ddots&&\\vdots\\\\\\vdots&&\\ddots&\\vdots\\\\a_{n1}&\\cdots&\\cdots&a_{nn}\\end{bmatrix}\\overset{\\mathrm{def}}{=}\\sum_{\\sigma\\in\\mathfrak{S}_n}\\varepsilon(\\sigma)\\prod_{k=1}^n a_{k\\sigma(k)}\\\\";
        latex += "\\sideset{_\\alpha^\\beta}{_\\gamma^\\delta}{\\begin{pmatrix}a&b\\\\c&d\\end{pmatrix}}\\\\";
        latex += "\\int_0^\\infty{x^{2n} e^{-a x^2}\\,dx} = \\frac{2n-1}{2a} \\int_0^\\infty{x^{2(n-1)} e^{-a x^2}\\,dx} = \\frac{(2n-1)!!}{2^{n+1}} \\sqrt{\\frac{\\pi}{a^{2n+1}}}\\\\";
        latex += "\\int_a^b{f(x)\\,dx} = (b - a) \\sum\\limits_{n = 1}^\\infty  {\\sum\\limits_{m = 1}^{2^n  - 1} {\\left( { - 1} \\right)^{m + 1} } } 2^{ - n} f(a + m\\left( {b - a} \\right)2^{-n} )\\\\";
        latex += "\\int_{-\\pi}^{\\pi} \\sin(\\alpha x) \\sin^n(\\beta x) dx = \\textstyle{\\left \\{ \\begin{array}{cc} (-1)^{(n+1)/2} (-1)^m \\frac{2 \\pi}{2^n} \\binom{n}{m} & n \\mbox{ odd},\\ \\alpha = \\beta (2m-n) \\\\ 0 & \\mbox{otherwise} \\\\ \\end{array} \\right .}\\\\";
        latex += "L = \\int_a^b \\sqrt{ \\left|\\sum_{i,j=1}^ng_{ij}(\\gamma(t))\\left(\\frac{d}{dt}x^i\\circ\\gamma(t)\\right)\\left(\\frac{d}{dt}x^j\\circ\\gamma(t)\\right)\\right|}\\,dt\\\\";
        latex += "\\begin{array}{rl} s &= \\int_a^b\\left\\|\\frac{d}{dt}\\vec{r}\\,(u(t),v(t))\\right\\|\\,dt \\\\ &= \\int_a^b \\sqrt{u'(t)^2\\,\\vec{r}_u\\cdot\\vec{r}_u + 2u'(t)v'(t)\\, \\vec{r}_u\\cdot\\vec{r}_v+ v'(t)^2\\,\\vec{r}_v\\cdot\\vec{r}_v}\\,\\,\\, dt. \\end{array}\\\\";
        latex += "\\end{array}";

        return latex;
    }

    String get03() {

                String latex = "\\text{A long division \\longdiv{12345}{13}";

        return latex;
    }

    String get04() {

              String latex = "{a \\bangle b} {c \\brace d} {e \\brack f} {g \\choose h}";

        return latex;
    }

    String get05() {

        String latex = "\\begin{array}{cc}";
        latex += "\\fbox{\\text{A framed box with \\textdbend}}&\\shadowbox{\\text{A shadowed box}}\\cr";
        latex += "\\doublebox{\\text{A double framed box}}&\\ovalbox{\\text{An oval framed box}}\\cr";
        latex += "\\end{array}";

        return latex;
    }

    String get06() {
        String latex = "\\frac{\\partial f}{\\partial x} = 2\\,\\sqrt{a}\\,x";

        return latex;
    }

    String get07() {
        String latex = "\\[Mn{{O}_{2}}/M{{n}_{2}}{{O}_{3}}+{{H}_{2}}\\to M{{n}_{3}}{{O}_{4}}\\] ";

        return latex;
    }

    String get08() {
        String latex = "\\[M{{n}_{3}}{{O}_{4}}+{{H}_{2}}\\to MnO\\]";
        return latex;
    }
}
