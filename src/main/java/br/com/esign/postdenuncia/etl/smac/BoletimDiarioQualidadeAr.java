package br.com.esign.postdenuncia.etl.smac;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

public class BoletimDiarioQualidadeAr implements FonteMedicao {

    private final Html html;
    private final Document doc;

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            BoletimDiarioQualidadeAr fonte = new BoletimDiarioQualidadeAr();
            List<DadosMedicao> dados = fonte.listarDadosMedicao();
            dados.stream().forEach((dado) -> {
                System.out.println(dado);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BoletimDiarioQualidadeAr() throws IOException {
        html = new Html();
        doc = Jsoup.parse(html.toString());
    }

    private Date obterDatahoraMedicao() {
        Date datahoraMedicao = null;
        String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(regex);
        Element div = doc.getElementById("titulo");
        Matcher matcher = pattern.matcher(div.html());
        if (matcher.find()) {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm");
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
        Element div = doc.getElementById("dados_estacoes");
        Elements trs = div.getElementsByTag("tr");
        List<String> poluentes = new ArrayList<>();
        int size = 0;
        for (int i = 1, j = trs.size(); i < j; i++) {
            Element tr = trs.get(i);
            if (i == 1) {
                Elements ths = tr.getElementsByTag("th");
                ths.stream().forEach((th) -> {
                    poluentes.add(th.text());
                });
                size = poluentes.size();
            } else {
                Elements tds = tr.getElementsByTag("td");
                if (tds.size() == size + 3) {
                    DadosMedicao dadosMedicao = new DadosMedicao();
                    dadosMedicao.setDatahora(datahora);
                    dadosMedicao.setEstacao(tds.get(0).text());
                    for (int k = 0, l = 1; k < size; k++, l++) {
                        if (tds.get(l).attr("class").equals("td_value_bold")) {
                            dadosMedicao.setPoluente(poluentes.get(k));
                            break;
                        }
                    }
                    dadosMedicao.setIndice(tds.get(size + 1).text());
                    dadosMedicao.setQualidadeAr(tds.get(size + 2).text());
                    listaDadosMedicao.add(dadosMedicao);
                }
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://jeap.rio.rj.gov.br/je-metinfosmac/boletim";
            String html = get(spec, null, "UTF-8").getHtmlResult();

            this.html = html;
        }

    }

}