package br.com.esign.postdenuncia.etl;

import br.com.esign.postdenuncia.util.HtmlUtil;

public abstract class FonteHtml extends HtmlUtil {

    protected String html;

    public String getHtml() {
        return html;
    }

    @Override
    public String toString() {
        return html;
    }

}