package br.com.esign.postdenuncia.etl.inea;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        Html html = new Html();
        doc = Jsoup.parse(html.toString());
    }

    private Date obterDataHoraMedicao(String html) {
        Date datahoraMedicao = null;
        String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/(\\d\\d)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yy");
            Date dataMedicao = null;
            try {
                dataMedicao = parser.parse(matcher.group());
            } catch (ParseException e) {
                throw new RuntimeException(MessagesBundle.ERRO_OBTENCAO_DATAHORA);
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataMedicao);
            calendar.set(Calendar.HOUR_OF_DAY, 15);
            datahoraMedicao = calendar.getTime();
        }
        return datahoraMedicao;
    }

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();
        Element table = doc.getElementById("DGAutomatica");
        Date datahora = obterDataHoraMedicao(table.html());
        Elements trs = table.getElementsByTag("tr");
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() == 3 && !"Estação".equals(tds.get(0).text())) {
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(datahora);
                dadosMedicao.setEstacao(tds.get(0).text());
                dadosMedicao.setPoluente(tds.get(1).text());
                dadosMedicao.setQualidadeAr(tds.get(2).text());
                listaDadosMedicao.add(dadosMedicao);
            }
        }
        table = doc.getElementById("DGManual");
        datahora = obterDataHoraMedicao(table.html());
        trs = table.getElementsByTag("tr");
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() == 2 && !"Estação".equals(tds.get(0).text())) {
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(datahora);
                dadosMedicao.setEstacao(tds.get(0).text());
                dadosMedicao.setQualidadeAr(tds.get(1).text());
                listaDadosMedicao.add(dadosMedicao);
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://200.20.53.6/sitefeema/frmboletim.aspx";
            String html = get(spec, null).getHtmlResult();

            this.html = html;
        }

    }

}