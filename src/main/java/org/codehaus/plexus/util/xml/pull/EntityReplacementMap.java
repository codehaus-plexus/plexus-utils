package org.codehaus.plexus.util.xml.pull;

/*
 * Copyright The Codehaus Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class EntityReplacementMap {
    final String entityName[];

    final char[] entityNameBuf[];

    final String entityReplacement[];

    final char[] entityReplacementBuf[];

    int entityEnd;

    final int entityNameHash[];

    public EntityReplacementMap(String[][] replacements) {
        int length = replacements.length;
        entityName = new String[length];
        entityNameBuf = new char[length][];
        entityReplacement = new String[length];
        entityReplacementBuf = new char[length][];
        entityNameHash = new int[length];

        for (String[] replacement : replacements) {
            defineEntityReplacementText(replacement[0], replacement[1]);
        }
    }

    private void defineEntityReplacementText(String entityName, String replacementText) {
        if (!replacementText.startsWith("&#") && this.entityName != null && replacementText.length() > 1) {
            String tmp = replacementText.substring(1, replacementText.length() - 1);
            for (int i = 0; i < this.entityName.length; i++) {
                if (this.entityName[i] != null && this.entityName[i].equals(tmp)) {
                    replacementText = this.entityReplacement[i];
                }
            }
        }

        // this is to make sure that if interning works we will take advantage of it ...
        char[] entityNameCharData = entityName.toCharArray();
        // noinspection ConstantConditions
        this.entityName[entityEnd] = newString(entityNameCharData, 0, entityName.length());
        entityNameBuf[entityEnd] = entityNameCharData;

        entityReplacement[entityEnd] = replacementText;
        entityReplacementBuf[entityEnd] = replacementText.toCharArray();
        entityNameHash[entityEnd] = fastHash(entityNameBuf[entityEnd], 0, entityNameBuf[entityEnd].length);
        ++entityEnd;
        // TODO disallow < or & in entity replacement text (or ]]>???)
        // TODO keepEntityNormalizedForAttributeValue cached as well ...
    }

    private String newString(char[] cbuf, int off, int len) {
        return new String(cbuf, off, len);
    }

    /**
     * simplistic implementation of hash function that has <b>constant</b> time to compute - so it also means
     * diminishing hash quality for long strings but for XML parsing it should be good enough ...
     */
    private static int fastHash(char ch[], int off, int len) {
        if (len == 0) return 0;
        // assert len >0
        int hash = ch[off]; // hash at beginning
        // try {
        hash = (hash << 7) + ch[off + len - 1]; // hash at the end
        // } catch(ArrayIndexOutOfBoundsException aie) {
        // aie.printStackTrace(); //should never happen ...
        // throw new RuntimeException("this is violation of pre-condition");
        // }
        if (len > 16) hash = (hash << 7) + ch[off + (len / 4)]; // 1/4 from beginning
        if (len > 8) hash = (hash << 7) + ch[off + (len / 2)]; // 1/2 of string size ...
        // notice that hash is at most done 3 times <<7 so shifted by 21 bits 8 bit value
        // so max result == 29 bits so it is quite just below 31 bits for long (2^32) ...
        // assert hash >= 0;
        return hash;
    }

    public static final EntityReplacementMap defaultEntityReplacementMap = new EntityReplacementMap(new String[][] {
        {"nbsp", "\u00a0"},
        {"iexcl", "\u00a1"},
        {"cent", "\u00a2"},
        {"pound", "\u00a3"},
        {"curren", "\u00a4"},
        {"yen", "\u00a5"},
        {"brvbar", "\u00a6"},
        {"sect", "\u00a7"},
        {"uml", "\u00a8"},
        {"copy", "\u00a9"},
        {"ordf", "\u00aa"},
        {"laquo", "\u00ab"},
        {"not", "\u00ac"},
        {"shy", "\u00ad"},
        {"reg", "\u00ae"},
        {"macr", "\u00af"},
        {"deg", "\u00b0"},
        {"plusmn", "\u00b1"},
        {"sup2", "\u00b2"},
        {"sup3", "\u00b3"},
        {"acute", "\u00b4"},
        {"micro", "\u00b5"},
        {"para", "\u00b6"},
        {"middot", "\u00b7"},
        {"cedil", "\u00b8"},
        {"sup1", "\u00b9"},
        {"ordm", "\u00ba"},
        {"raquo", "\u00bb"},
        {"frac14", "\u00bc"},
        {"frac12", "\u00bd"},
        {"frac34", "\u00be"},
        {"iquest", "\u00bf"},
        {"Agrave", "\u00c0"},
        {"Aacute", "\u00c1"},
        {"Acirc", "\u00c2"},
        {"Atilde", "\u00c3"},
        {"Auml", "\u00c4"},
        {"Aring", "\u00c5"},
        {"AElig", "\u00c6"},
        {"Ccedil", "\u00c7"},
        {"Egrave", "\u00c8"},
        {"Eacute", "\u00c9"},
        {"Ecirc", "\u00ca"},
        {"Euml", "\u00cb"},
        {"Igrave", "\u00cc"},
        {"Iacute", "\u00cd"},
        {"Icirc", "\u00ce"},
        {"Iuml", "\u00cf"},
        {"ETH", "\u00d0"},
        {"Ntilde", "\u00d1"},
        {"Ograve", "\u00d2"},
        {"Oacute", "\u00d3"},
        {"Ocirc", "\u00d4"},
        {"Otilde", "\u00d5"},
        {"Ouml", "\u00d6"},
        {"times", "\u00d7"},
        {"Oslash", "\u00d8"},
        {"Ugrave", "\u00d9"},
        {"Uacute", "\u00da"},
        {"Ucirc", "\u00db"},
        {"Uuml", "\u00dc"},
        {"Yacute", "\u00dd"},
        {"THORN", "\u00de"},
        {"szlig", "\u00df"},
        {"agrave", "\u00e0"},
        {"aacute", "\u00e1"},
        {"acirc", "\u00e2"},
        {"atilde", "\u00e3"},
        {"auml", "\u00e4"},
        {"aring", "\u00e5"},
        {"aelig", "\u00e6"},
        {"ccedil", "\u00e7"},
        {"egrave", "\u00e8"},
        {"eacute", "\u00e9"},
        {"ecirc", "\u00ea"},
        {"euml", "\u00eb"},
        {"igrave", "\u00ec"},
        {"iacute", "\u00ed"},
        {"icirc", "\u00ee"},
        {"iuml", "\u00ef"},
        {"eth", "\u00f0"},
        {"ntilde", "\u00f1"},
        {"ograve", "\u00f2"},
        {"oacute", "\u00f3"},
        {"ocirc", "\u00f4"},
        {"otilde", "\u00f5"},
        {"ouml", "\u00f6"},
        {"divide", "\u00f7"},
        {"oslash", "\u00f8"},
        {"ugrave", "\u00f9"},
        {"uacute", "\u00fa"},
        {"ucirc", "\u00fb"},
        {"uuml", "\u00fc"},
        {"yacute", "\u00fd"},
        {"thorn", "\u00fe"},
        {"yuml", "\u00ff"},

        // ----------------------------------------------------------------------
        // Special entities
        // ----------------------------------------------------------------------

        {"OElig", "\u0152"},
        {"oelig", "\u0153"},
        {"Scaron", "\u0160"},
        {"scaron", "\u0161"},
        {"Yuml", "\u0178"},
        {"circ", "\u02c6"},
        {"tilde", "\u02dc"},
        {"ensp", "\u2002"},
        {"emsp", "\u2003"},
        {"thinsp", "\u2009"},
        {"zwnj", "\u200c"},
        {"zwj", "\u200d"},
        {"lrm", "\u200e"},
        {"rlm", "\u200f"},
        {"ndash", "\u2013"},
        {"mdash", "\u2014"},
        {"lsquo", "\u2018"},
        {"rsquo", "\u2019"},
        {"sbquo", "\u201a"},
        {"ldquo", "\u201c"},
        {"rdquo", "\u201d"},
        {"bdquo", "\u201e"},
        {"dagger", "\u2020"},
        {"Dagger", "\u2021"},
        {"permil", "\u2030"},
        {"lsaquo", "\u2039"},
        {"rsaquo", "\u203a"},
        {"euro", "\u20ac"},

        // ----------------------------------------------------------------------
        // Symbol entities
        // ----------------------------------------------------------------------

        {"fnof", "\u0192"},
        {"Alpha", "\u0391"},
        {"Beta", "\u0392"},
        {"Gamma", "\u0393"},
        {"Delta", "\u0394"},
        {"Epsilon", "\u0395"},
        {"Zeta", "\u0396"},
        {"Eta", "\u0397"},
        {"Theta", "\u0398"},
        {"Iota", "\u0399"},
        {"Kappa", "\u039a"},
        {"Lambda", "\u039b"},
        {"Mu", "\u039c"},
        {"Nu", "\u039d"},
        {"Xi", "\u039e"},
        {"Omicron", "\u039f"},
        {"Pi", "\u03a0"},
        {"Rho", "\u03a1"},
        {"Sigma", "\u03a3"},
        {"Tau", "\u03a4"},
        {"Upsilon", "\u03a5"},
        {"Phi", "\u03a6"},
        {"Chi", "\u03a7"},
        {"Psi", "\u03a8"},
        {"Omega", "\u03a9"},
        {"alpha", "\u03b1"},
        {"beta", "\u03b2"},
        {"gamma", "\u03b3"},
        {"delta", "\u03b4"},
        {"epsilon", "\u03b5"},
        {"zeta", "\u03b6"},
        {"eta", "\u03b7"},
        {"theta", "\u03b8"},
        {"iota", "\u03b9"},
        {"kappa", "\u03ba"},
        {"lambda", "\u03bb"},
        {"mu", "\u03bc"},
        {"nu", "\u03bd"},
        {"xi", "\u03be"},
        {"omicron", "\u03bf"},
        {"pi", "\u03c0"},
        {"rho", "\u03c1"},
        {"sigmaf", "\u03c2"},
        {"sigma", "\u03c3"},
        {"tau", "\u03c4"},
        {"upsilon", "\u03c5"},
        {"phi", "\u03c6"},
        {"chi", "\u03c7"},
        {"psi", "\u03c8"},
        {"omega", "\u03c9"},
        {"thetasym", "\u03d1"},
        {"upsih", "\u03d2"},
        {"piv", "\u03d6"},
        {"bull", "\u2022"},
        {"hellip", "\u2026"},
        {"prime", "\u2032"},
        {"Prime", "\u2033"},
        {"oline", "\u203e"},
        {"frasl", "\u2044"},
        {"weierp", "\u2118"},
        {"image", "\u2111"},
        {"real", "\u211c"},
        {"trade", "\u2122"},
        {"alefsym", "\u2135"},
        {"larr", "\u2190"},
        {"uarr", "\u2191"},
        {"rarr", "\u2192"},
        {"darr", "\u2193"},
        {"harr", "\u2194"},
        {"crarr", "\u21b5"},
        {"lArr", "\u21d0"},
        {"uArr", "\u21d1"},
        {"rArr", "\u21d2"},
        {"dArr", "\u21d3"},
        {"hArr", "\u21d4"},
        {"forall", "\u2200"},
        {"part", "\u2202"},
        {"exist", "\u2203"},
        {"empty", "\u2205"},
        {"nabla", "\u2207"},
        {"isin", "\u2208"},
        {"notin", "\u2209"},
        {"ni", "\u220b"},
        {"prod", "\u220f"},
        {"sum", "\u2211"},
        {"minus", "\u2212"},
        {"lowast", "\u2217"},
        {"radic", "\u221a"},
        {"prop", "\u221d"},
        {"infin", "\u221e"},
        {"ang", "\u2220"},
        {"and", "\u2227"},
        {"or", "\u2228"},
        {"cap", "\u2229"},
        {"cup", "\u222a"},
        {"int", "\u222b"},
        {"there4", "\u2234"},
        {"sim", "\u223c"},
        {"cong", "\u2245"},
        {"asymp", "\u2248"},
        {"ne", "\u2260"},
        {"equiv", "\u2261"},
        {"le", "\u2264"},
        {"ge", "\u2265"},
        {"sub", "\u2282"},
        {"sup", "\u2283"},
        {"nsub", "\u2284"},
        {"sube", "\u2286"},
        {"supe", "\u2287"},
        {"oplus", "\u2295"},
        {"otimes", "\u2297"},
        {"perp", "\u22a5"},
        {"sdot", "\u22c5"},
        {"lceil", "\u2308"},
        {"rceil", "\u2309"},
        {"lfloor", "\u230a"},
        {"rfloor", "\u230b"},
        {"lang", "\u2329"},
        {"rang", "\u232a"},
        {"loz", "\u25ca"},
        {"spades", "\u2660"},
        {"clubs", "\u2663"},
        {"hearts", "\u2665"},
        {"diams", "\u2666"}
    });
}
