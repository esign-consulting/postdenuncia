package br.com.esign.postdenuncia.etl.iema;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();
        Elements maps = doc.getElementsByTag("map");
        for (Element map : maps) {
            if (map.attr("name").equals("MapMap")) {
                Elements areas = map.getElementsByTag("area");
                for (Element area : areas) {
                    String href = area.attr("href");
                    String htmlEstacao;
                    try {
                        htmlEstacao = html.get(href, null).getHtmlResult();
                    } catch (IOException e) {
                        continue;
                    }
                    Document docEstacao = Jsoup.parse(htmlEstacao);
                    Elements forms = docEstacao.getElementsByTag("form");
                    for (Element form : forms) {
                        if (form.attr("name").equals("frmSEA0701")) {
                            Elements tables = form.getElementsByTag("table");
                            if (tables.size() > 0) {
                                DadosMedicao dadosMedicao = new DadosMedicao();
                                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy hh:mm 'h'");
                                String datahora = "";
                                Elements trs = tables.get(0).getElementsByTag("tr");
                                for (Element tr : trs) {
                                    Elements tds = tr.getElementsByTag("td");
                                    for (Element td : tds) {
                                        String text = td.text();
                                        if (text.startsWith("Local: Estação ")) {
                                            dadosMedicao.setEstacao(text.substring(15));
                                        } else if (text.startsWith("Data: ")) {
                                            datahora += text.substring(6);
                                        } else if (text.startsWith("Hora do fechamento do boletim: ")) {
                                            datahora += (" " + text.substring(31));
                                        } else if (text.startsWith("Qualidade do Ar:")) {
                                            Elements imgs = td.getElementsByTag("img");
                                            if (imgs != null && !imgs.isEmpty()) {
                                                dadosMedicao.setQualidadeAr(imgs.get(0).attr("alt"));
                                            }
                                        } else if (text.startsWith("Poluente responsável: ")) {
                                            String poluente = text.substring(22);
                                            if (poluente.contains(" - ")) {
                                                String representacaoPoluente = poluente.split(" - ")[1];
                                                if (representacaoPoluente.equals("PM10")) {
                                                    representacaoPoluente = "MP10";
                                                }
                                                dadosMedicao.setPoluente(representacaoPoluente);
                                            }
                                        } else if (text.startsWith("Índice de Qualidade do Ar: ")) {
                                            dadosMedicao.setIndice(text.substring(27));
                                        }
                                    }
                                }
                                try {
                                    dadosMedicao.setDatahora(parser.parse(datahora));
                                } catch (ParseException e) {
                                    throw new RuntimeException(MessagesBundle.ERRO_OBTENCAO_DATAHORA);
                                }
                                listaDadosMedicao.add(dadosMedicao);
                            }
                            break;
                        }
                    }
                }
                break;
            }
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://www.meioambiente.es.gov.br/default.asp";
            String html = post(spec, "ItemMenu=16752&Parametro=&PaginaDestino=&hidCdPublicacao=", null).getHtmlResult();

            this.html = html;
        }

    }

}