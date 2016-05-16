package br.com.esign.postdenuncia.etl.feam;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        final String[] regex = {"(Dia)(\\s)(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])(\\s)(&agrave;s)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9](h)",
            "(Boletim do dia)(\\s)(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])(\\s)(atualizado &agrave;s)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9](h)",
            "(Boletim do dia)(\\s)(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])(,\\s)(atualizado, &agrave;s)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9](h)",
            "(Boletim do dia)(\\s)(0?[1-9]|[12][0-9]|3[01])(\\sde\\s)(?:Janeiro?|Fevereiro?|Março?|Abril?|Maio?|Junho?|Julho?|Agosto?|Setembro?|Outubro?|Novembro?|Dezembro?)(,\\s)(atualizado, &agrave;s)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9](h)"
        };
        final String[] pattern = {"'Dia' dd/MM '&agrave;s' HH:mm'h'",
            "'Boletim do dia' dd/MM 'atualizado &agrave;s' HH:mm'h'",
            "'Boletim do dia' dd/MM', atualizado, &agrave;s' HH:mm'h'",
            "'Boletim do dia' dd 'de' MMMM', atualizado, &agrave;s' HH:mm'h'"
        };
        Element div = doc.getElementById("articlepage");
        if (div != null) {
            String divHtml = div.html();
            for (int i = 0; i < regex.length; i++) {
                Matcher matcher = Pattern.compile(regex[i]).matcher(divHtml);
                if (matcher.find()) {
                    Date date = null;
                    try {
                        SimpleDateFormat parser = new SimpleDateFormat(pattern[i], new Locale("pt", "BR"));
                        date = parser.parse(matcher.group());
                    } catch (ParseException e) {
                        throw new RuntimeException(MessagesBundle.ERRO_OBTENCAO_DATAHORA);
                    }
                    if (date != null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                        datahoraMedicao = calendar.getTime();
                    }
                }
            }
        }
        return datahoraMedicao;
    }

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        Date datahora = obterDatahoraMedicao();
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();
        Element div = doc.getElementById("articlepage");
        Elements tables = div.getElementsByTag("table");
        for (int i = 0, j = tables.size(); i < j; i++) {
            Element tbody = tables.get(i).child(0);
            Elements trs = tbody.children();
            for (int k = 1, l = trs.size(); k < l; k++) {
                Elements tds = trs.get(k).children();
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(datahora);
                dadosMedicao.setEstacao(tds.get(0).text());

                int maiorIndice = 0;
                String[] indices = tds.get(1).text().split("/");
                for (int m = 0; m < indices.length; m++) {
                    try {
                        int indice = Integer.parseInt(indices[m].trim());
                        if (indice > maiorIndice) {
                            maiorIndice = indice;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
                dadosMedicao.setIndice((maiorIndice > 0) ? String.valueOf(maiorIndice) : null);

                dadosMedicao.setQualidadeAr(tds.get(3).text());

                String poluente = tds.get(4).text();
                if (indices.length > 1) {
                    int endIndex = poluente.indexOf(" (" + maiorIndice + ")");
                    if (endIndex != -1) {
                        int beginIndex = poluente.indexOf(") ");
                        if (beginIndex == -1 || beginIndex > endIndex) {
                            beginIndex = -2;
                        }
                        poluente = poluente.substring(beginIndex + 2, endIndex);
                        if (poluente.startsWith("e ")) {
                            poluente = poluente.substring(2);
                        }
                    }
                }
                dadosMedicao.setPoluente(poluente);

                listaDadosMedicao.add(dadosMedicao);
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://www.feam.br/qualidade-do-ar";
            String html = get(spec, null, "UTF-8").getHtmlResult();

            this.html = html;
        }

    }

}