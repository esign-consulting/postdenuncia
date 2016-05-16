package br.com.esign.postdenuncia.etl.inea;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.esign.postdenuncia.etl.DadosMedicao;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.util.HtmlUtil;

public class ConsultaQualidadeArTempoReal implements FonteMedicao {

    private final Html html;

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

    public ConsultaQualidadeArTempoReal() {
        html = new Html();
    }

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        return listarDadosMedicao(Calendar.getInstance());
    }

    private List<DadosMedicao> listarDadosMedicao(Calendar calendar) {
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        final String NODATA = "0|error|500||";
        String[] estacoes = {};
        int attempts = 1;
        while (true) {
            String htmlStr = NODATA;
            try {
                htmlStr = html.getHtml(calendar);
            } catch (IOException e) {
            }
            if (!htmlStr.equals(NODATA)) {
                estacoes = obterDadosEstacoes(htmlStr);
                if (estacoes.length > 0) {
                    break;
                }
            }
            calendar.add(Calendar.HOUR_OF_DAY, -1);
            attempts++;
            if (attempts > 100) {
                break;
            }
        }

        if (estacoes.length > 0) {
            Pattern pattern = Pattern.compile("('\\d+', ){5}");
            for (String estacao : estacoes) {
                String[] dadosEstacao = split(estacao);
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(calendar.getTime());
                dadosMedicao.setEstacao(strip(dadosEstacao[0]));
                dadosMedicao.setLatitude(dadosEstacao[1]);
                dadosMedicao.setLongitude(dadosEstacao[2]);
                dadosMedicao.setQualidadeAr(strip(dadosEstacao[6]));
                Matcher matcher = pattern.matcher(estacao);
                if (matcher.find()) {
                    String[] indices = split(matcher.group());
                    int maiorIndice = 0;
                    for (int i = 0; i < indices.length; i++) {
                        int indice = Integer.parseInt(strip(indices[i]));
                        if (indice > maiorIndice) {
                            maiorIndice = indice;
                            dadosMedicao.setIndice(String.valueOf(indice));
                            dadosMedicao.setPoluente(obterPoluente(i));
                        }
                    }
                }
                listaDadosMedicao.add(dadosMedicao);
            }
        }

        return listaDadosMedicao;
    }

    private String[] obterDadosEstacoes(String htmlStr) {
        String[] estacoesStr = {};
        if (htmlStr != null && !htmlStr.isEmpty()) {
            int beginIndex = htmlStr.indexOf("setEstacoes(\"[");
            if (beginIndex != -1) {
                int endIndex = htmlStr.indexOf("]\", \"True\");", beginIndex);
                if (endIndex == -1) {
                    endIndex = htmlStr.indexOf("]\", \"False\");", beginIndex);
                }
                if (endIndex != -1) {
                    estacoesStr = htmlStr.substring(beginIndex + 14, endIndex).split("\\],\\[");
                }
            }
        }
        return estacoesStr;
    }

    private String[] split(String source) {
        return source.split(", ");
    }

    private String strip(String source) {
        return source.replaceAll("'", "");
    }

    private String obterPoluente(int i) {
        String poluente = null;
        switch (i) {
            case 0:
                poluente = "MP10";
                break;
            case 1:
                poluente = "SO2";
                break;
            case 2:
                poluente = "NO2";
                break;
            case 3:
                poluente = "O3";
                break;
            case 4:
                poluente = "CO";
                break;
        }
        return poluente;
    }

    private class Html extends HtmlUtil {

        public String getHtml(Calendar calendar) throws IOException {
            final String charsetName = "UTF-8";

            Map<String, String> headers = new HashMap<>();
            headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:29.0) Gecko/20100101 Firefox/29.0");

            String spec = "http://200.20.53.7/hotsiteinea";
            String html = get(spec, null, headers, charsetName).getHtmlResult();

            Document doc = Jsoup.parse(html);
            Element eventValidationInput = doc.getElementById("__EVENTVALIDATION");
            Element viewStateInput = doc.getElementById("__VIEWSTATE");

            StringBuilder params = new StringBuilder();
            params.append("ScriptManager1=uppGeral%7Cbotao&ddlRegiao=-1&ddlTipo=-1&ddlHora=");
            params.append(calendar.get(Calendar.HOUR_OF_DAY));
            params.append("%3A00&ddlDia=");
            params.append((new DecimalFormat("00")).format(calendar.get(Calendar.DAY_OF_MONTH)));
            params.append("&ddlMes=");
            params.append(calendar.get(Calendar.MONTH) + 1);
            params.append("&ddlAno=");
            params.append(calendar.get(Calendar.YEAR));
            params.append("&__EVENTTARGET=&__EVENTARGUMENT=&__LASTFOCUS=&__VIEWSTATE=");
            params.append(URLEncoder.encode(viewStateInput.val(), charsetName));
            params.append("&__EVENTVALIDATION=");
            params.append(URLEncoder.encode(eventValidationInput.val(), charsetName));
            params.append("&__ASYNCPOST=true&botao=Ok");

            spec = "http://200.20.53.7/hotsiteinea/default.aspx";
            html = post(spec, params.toString(), null, headers, charsetName).getHtmlResult();
            return html;
        }

    }

}