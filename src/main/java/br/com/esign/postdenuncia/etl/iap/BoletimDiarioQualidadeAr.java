package br.com.esign.postdenuncia.etl.iap;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.esign.postdenuncia.etl.DadosMedicao;
import br.com.esign.postdenuncia.etl.FonteHtml;
import br.com.esign.postdenuncia.etl.FonteMedicao;

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
        Element select = doc.getElementById("estacao_ar1");
        Elements options = select.children();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter1 = new SimpleDateFormat("MM"), formatter2 = new SimpleDateFormat("yyyy");
        String mes_ar = formatter1.format(calendar.getTime()), ano_ar = formatter2.format(calendar.getTime());
        String dia_ar = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));

        for (int i = 0, j = options.size(); i < j; i++) {
            Element estacao_ar = options.get(i);
            DadosMedicao dadosMedicao = new DadosMedicao();
            String estacao = estacao_ar.text();
            int index = estacao.indexOf(" (*)");
            if (index != -1) {
                estacao = estacao.substring(0, index);
            }
            dadosMedicao.setEstacao(estacao);
            String dadosEstacao;
            try {
                dadosEstacao = html.obterDadosEstacao(estacao_ar.val(), mes_ar, ano_ar);
            } catch (IOException e) {
                continue;
            }
            String[] linhas = dadosEstacao.split("\n");
            int linhaHeader = 0;
            String[] colunasHeader = null;
            String[] colunasDia = null;
            for (int k = 0, l = linhas.length; k < l; k++) {
                String[] colunas = linhas[k].split(" ");
                if (colunas.length > 0) {
                    if (colunasHeader == null && (colunas[0].equals("DIA") || colunas[0].equals("SO2"))) {
                        colunasHeader = colunas;
                        linhaHeader = k;
                        continue;
                    }
                    if (colunasHeader != null && colunas[0].equals(dia_ar)) {
                        for (int m = k; m > linhaHeader; m--) {
                            String[] colunas_ar = linhas[m].split(" ");
                            if ((colunasHeader.length > 10 && colunas_ar.length > 10 && !colunas_ar[10].isEmpty() && !colunas_ar[10].equals("*"))
                                    || (colunasHeader.length == 5 && colunas_ar.length > 5 && !colunas_ar[5].isEmpty() && !colunas_ar[5].equals("ND"))) {
                                colunasDia = colunas_ar;
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (colunasHeader != null && colunasDia != null) {
                Calendar datahora = Calendar.getInstance();
                try {
                    datahora.set(Calendar.DAY_OF_MONTH, Integer.parseInt(colunasDia[0]));
                } catch (NumberFormatException e) {
                    continue;
                }
                datahora.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                datahora.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
                datahora.set(Calendar.HOUR_OF_DAY, 23);
                datahora.set(Calendar.MINUTE, 59);
                datahora.set(Calendar.SECOND, 59);
                datahora.set(Calendar.MILLISECOND, 0);
                dadosMedicao.setDatahora(datahora.getTime());

                int maiorIndice = -1;
                int posicaoIndice = -1;
                boolean estacaoAutomatica = (colunasHeader.length > 10);
                int posicaoInicial = (estacaoAutomatica) ? 3 : 1;
                int posicaoFinal = (estacaoAutomatica) ? 8 : 3;
                int posicaoQA = (estacaoAutomatica) ? 10 : 5;
                for (int k = posicaoInicial; k <= posicaoFinal; k++) {
                    try {
                        int indice = Integer.parseInt(colunasDia[k]);
                        if (indice > maiorIndice) {
                            maiorIndice = indice;
                            posicaoIndice = k;
                        }
                    } catch (NumberFormatException e) {
                    }
                }
                if (maiorIndice != -1 && posicaoIndice != -1) {
                    String poluente = colunasHeader[(estacaoAutomatica) ? posicaoIndice : posicaoIndice - 1];
                    if (poluente.equals("PM10") || poluente.equals("PI")) {
                        poluente = "MP10";
                    } else if (poluente.equals("MPTS")) {
                        poluente = "PTS";
                    }
                    dadosMedicao.setPoluente(poluente);
                    dadosMedicao.setIndice(String.valueOf(maiorIndice));
                    dadosMedicao.setQualidadeAr(colunasDia[posicaoQA]);
                }
            }
            listaDadosMedicao.add(dadosMedicao);
        }
        return listaDadosMedicao;
    }

    private class Html extends FonteHtml {

        public Html() throws IOException {
            String spec = "http://www.iap.pr.gov.br/modules/conteudo/conteudo.php?conteudo=1076";
            String html = get(spec, null, "UTF-8").getHtmlResult();

            this.html = html;
        }

        public String obterDadosEstacao(String estacao_ar, String mes_ar, String ano_ar) throws IOException {
            URL url = new URL("http://www.iap.pr.gov.br/arquivos/File/Monitoramento/qualidade_do_ar_laptec/IQA_" + estacao_ar + "_" + mes_ar + "_" + ano_ar + ".pdf");
            String dadosEstacao;
            try (PDDocument doc = PDDocument.load(url.openStream())) {
                PDFTextStripper pdf = new PDFTextStripper();
                dadosEstacao = pdf.getText(doc);
            }
            return dadosEstacao;
        }

    }

}