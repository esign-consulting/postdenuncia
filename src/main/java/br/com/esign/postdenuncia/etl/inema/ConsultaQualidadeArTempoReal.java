package br.com.esign.postdenuncia.etl.inema;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.esign.postdenuncia.etl.DadosMedicao;
import br.com.esign.postdenuncia.etl.FonteHtml;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.util.MessagesBundle;

public class ConsultaQualidadeArTempoReal implements FonteMedicao {

    private final Html html;
    private final Document doc;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            long t = System.currentTimeMillis();
            ConsultaQualidadeArTempoReal fonte = new ConsultaQualidadeArTempoReal();
            List<DadosMedicao> dados = fonte.listarDadosMedicao();
            System.out.println((System.currentTimeMillis() - t) + " milliseconds");
            dados.stream().forEach((dado) -> {
                System.out.println(dado);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ConsultaQualidadeArTempoReal() throws IOException {
        html = new Html();
        doc = Jsoup.parse(html.toString());
    }

    private Date obterDatahoraMedicao() {
        Date datahoraMedicao = null;
        String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)(\\s)(-)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(regex);
        Element div = doc.getElementsByClass("cetrel-abas").first();
        Matcher matcher = pattern.matcher(div.html());
        if (matcher.find()) {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy '-' HH:mm");
            try {
                datahoraMedicao = parser.parse(matcher.group());
            } catch (ParseException e) {
                throw new RuntimeException(MessagesBundle.ERRO_OBTENCAO_DATAHORA);
            }
        }
        return datahoraMedicao;
    }

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        Date datahora = obterDatahoraMedicao();
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();
        String[] ids = {"salvador", "camacari"};
        for (String id : ids) {
            Element article = doc.getElementById(id);
            Element section = article.getElementsByTag("section").first();
            Element ul = section.getElementsByTag("ul").first();
            Elements lis = ul.getElementsByTag("li");
            for (Element li : lis) {
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(datahora);
                Elements spans = li.getElementsByClass("legenda boa");
                if (spans != null) {
                    dadosMedicao.setQualidadeAr("BOA");
                } else {
                    spans = li.getElementsByClass("legenda regular");
                    if (spans != null) {
                        dadosMedicao.setQualidadeAr("REGULAR");
                    } else {
                        spans = li.getElementsByClass("legenda inadequada");
                        if (spans != null) {
                            dadosMedicao.setQualidadeAr("INADEQUADA");
                        } else {
                            spans = li.getElementsByClass("legenda ma");
                            if (spans != null) {
                                dadosMedicao.setQualidadeAr("MÁ");
                            } else {
                                spans = li.getElementsByClass("legenda pessima");
                                if (spans != null) {
                                    dadosMedicao.setQualidadeAr("PÉSSIMA");
                                } else {
                                    spans = li.getElementsByClass("legenda critica");
                                    if (spans != null) {
                                        dadosMedicao.setQualidadeAr("CRÍTICA");
                                    }
                                }
                            }
                        }
                    }
                }
                Element h3 = li.getElementsByTag("h3").first();
                dadosMedicao.setEstacao(h3.text());
                Element div = li.getElementsByClass("indices").first();
                Element table = div.getElementsByTag("table").first();
                Elements trs = table.getElementsByTag("tr");
                String dadoPoluente = null;
                double maiorIndice = 0;
                DecimalFormat parser = new DecimalFormat("##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
                for (Element tr : trs) {
                    Elements tds = tr.getElementsByTag("td");
                    for (Element td : tds) {
                        String dado = td.text();
                        if (dado.startsWith("Poluente: ")) {
                            String[] parts = dado.substring(10, dado.length()).split("(\\s)(–)(\\s)");
                            dadoPoluente = parts[parts.length - 1];
                        } else if (dado.startsWith("Índice: ")) {
                            String dadoIndice = dado.substring(8, dado.length());
                            try {
                                double indice = parser.parse(dadoIndice).doubleValue();
                                if (indice > maiorIndice) {
                                    dadosMedicao.setPoluente(("PM10".equals(dadoPoluente)) ? "MP10" : dadoPoluente);
                                    dadosMedicao.setIndice(dadoIndice);
                                    maiorIndice = indice;
                                }
                            } catch (ParseException e) {
                            }
                        }
                    }
                }
                listaDadosMedicao.add(dadosMedicao);
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://qualidadear.cetrel.com.br/iframe/";
            this.html = get(spec, null, "utf-8").getHtmlResult();
        }

    }

}