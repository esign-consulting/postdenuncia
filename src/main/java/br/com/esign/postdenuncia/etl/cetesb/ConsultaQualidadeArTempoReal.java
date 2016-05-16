package br.com.esign.postdenuncia.etl.cetesb;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.esign.postdenuncia.etl.DadosMedicao;
import br.com.esign.postdenuncia.etl.FonteHtml;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.util.MessagesBundle;

public class ConsultaQualidadeArTempoReal implements FonteMedicao {

    private final Html html;
    private final Map<String, CoordenadasUtm> coordenadasUtmMap = new HashMap<>();

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
        carregarCoordenadasUTM(html.getConfigHtml1());
        carregarCoordenadasUTM(html.getConfigHtml2());
        carregarCoordenadasUTM(html.getConfigHtml3());
    }

    private void carregarCoordenadasUTM(String configHtml) {
        Document doc = Jsoup.parse(configHtml);
        Elements estacoesTh = doc.getElementsMatchingOwnText("Estações");
        for (Element estacaoTh : estacoesTh) {
            if (estacaoTh.tagName().equals("th")) {
                Element tableEstacao = estacaoTh.parent().parent();
                Elements trs = tableEstacao.getElementsByTag("tr");
                for (int i = 2, j = trs.size() - 1; i < j; i++) {
                    Elements tds = trs.get(i).getElementsByTag("td");

                    int size = tds.size();
                    String latitude = tds.get(size - 4).text();
                    String fuso = tds.get(size - 3).text();
                    String longitude = tds.get(size - 2).text();

                    String estacao = tds.get(0).text();
                    CoordenadasUtm coordenadasUtm = new CoordenadasUtm(latitude, fuso, longitude);
                    coordenadasUtmMap.put(estacao, coordenadasUtm);
                }
            }
        }
    }

    private Date obterDatahoraMedicao() {
        Date datahoraMedicao = null;
        String regex = "(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[012])/((19|20)\\d\\d)(\\s)([01]?[0-9]|2[0-3]):[0-5][0-9]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(html.getHtml());
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
        Document doc = Jsoup.parse(html.toString());
        Elements trs = doc.getElementsByTag("tr");
        for (Element tr : trs) {
            Elements tds = tr.getElementsByTag("td");
            if (tds.size() == 4) {
                DadosMedicao dadosMedicao = new DadosMedicao();
                dadosMedicao.setDatahora(datahora);

                String estacao = tds.get(0).text();
                dadosMedicao.setEstacao(estacao);
                dadosMedicao.setQualidadeAr(tds.get(1).text());
                dadosMedicao.setIndice(tds.get(2).text());
                dadosMedicao.setPoluente(tds.get(3).text());

                CoordenadasUtm coordenadasUtm = coordenadasUtmMap.get(estacao);
                if (coordenadasUtm != null && coordenadasUtm.isValid()) {
                    String utmLatitude = coordenadasUtm.getLatitude();
                    String utmLongitude = coordenadasUtm.getLongitude();
                    String utmZona = coordenadasUtm.getFuso().toUpperCase();
                    Coordenadas coordenadas = new Coordenadas(utmLatitude, utmLongitude, utmZona);
                    dadosMedicao.setLatitude(String.valueOf(coordenadas.getLatitude()));
                    dadosMedicao.setLongitude(String.valueOf(coordenadas.getLongitude()));
                }

                listaDadosMedicao.add(dadosMedicao);
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        private final String configHtml1, configHtml2, configHtml3;

        public Html() throws IOException {
            // login
            String spec = "http://qualar.cetesb.sp.gov.br/qualar/autenticador";
            String params = "cetesb_login=gustavomcarmo&cetesb_password=nisegu";
            List<String> cookies = post(spec, params).getCookiesResult();

            // extract
            spec = "http://qualar.cetesb.sp.gov.br/qualar/conQualidadeArTempoReal.do?method=gerarRelatorio";
            params = "nugrhi=-1&cmuncpEct=-1&nestcaMonto=-1";
            post(spec, params, cookies);
            spec = "http://qualar.cetesb.sp.gov.br/qualar/conQualidadeArTempoReal.do?method=executarImprimir";
            this.html = get(spec, cookies).getHtmlResult();

            // configurations
            configHtml1 = getConfigHtml(cookies, "A"); // Automático
            configHtml2 = getConfigHtml(cookies, "M"); // Manual
            configHtml3 = getConfigHtml(cookies, "P"); // Passivo

            // logout
            spec = "http://qualar.cetesb.sp.gov.br/qualar/home.do?method=logOut";
            get(spec, cookies);
        }

        private String getConfigHtml(List<String> cookies, String irede) throws IOException {
            String spec = "http://qualar.cetesb.sp.gov.br/qualar/relConfiguracaoEstacao.do?method=gerarRelatorio";
            String params = "irede=" + irede + "&ugrhiVO.nugrhi=-1&municipioVO.cmuncpEct=-1";
            post(spec, params, cookies);
            spec = "http://qualar.cetesb.sp.gov.br/qualar/relConfiguracaoEstacao.do?method=executarImprimir";
            return get(spec, cookies).getHtmlResult();
        }

        public String getConfigHtml1() {
            return configHtml1;
        }

        public String getConfigHtml2() {
            return configHtml2;
        }

        public String getConfigHtml3() {
            return configHtml3;
        }

    }

    private class CoordenadasUtm {

        private final String latitude;
        private final String fuso;
        private final String longitude;

        public CoordenadasUtm(String latitude, String fuso, String longitude) {
            this.latitude = latitude;
            this.fuso = fuso;
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getFuso() {
            return fuso;
        }

        public String getLongitude() {
            return longitude;
        }

        public boolean isValid() {
            return (!latitude.equals("0") && !fuso.equals("--") && !longitude.equals("0"));
        }

    }

}
