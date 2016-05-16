package br.com.esign.postdenuncia.etl.iap;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.esign.postdenuncia.etl.DadosMedicao;
import br.com.esign.postdenuncia.etl.FonteMedicao;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.util.JsonUtil;
import java.text.ParseException;

public class ConsultaQualidadeArTempoReal implements FonteMedicao {

    private final Logger logger = LogManager.getLogger();

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

    @Override
    public List<DadosMedicao> listarDadosMedicao() {
        List<DadosMedicao> listaDadosMedicao = new ArrayList<>();
        try {
            RegiaoList regiaoList = getRegiaoList();
            List<Regiao> regioes = regiaoList.getRegiao();
            if (regioes != null && !regioes.isEmpty()) {
                SimpleDateFormat parser = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (Regiao regiao : regioes) {
                    EstacaoList estacaoList = getEstacaoList(regiao.getSigla());
                    List<Estacao> estacoes = estacaoList.getEstacao();
                    if (estacoes != null && !estacoes.isEmpty()) {
                        for (Estacao estacao : estacoes) {
                            DadosMedicao dadosMedicao = new DadosMedicao();
                            dadosMedicao.setDatahora(parser.parse(estacao.getHora()));
                            dadosMedicao.setEstacao(estacao.getNome());
                            dadosMedicao.setQualidadeAr(estacao.getIndice());
                            dadosMedicao.setIndice(estacao.getIqa());
                            dadosMedicao.setPoluente(estacao.getPoluente());

                            String utmLatitude = String.valueOf(estacao.getY());
                            String utmLongitude = String.valueOf(estacao.getX());
                            String utmZona = "22J";
                            Coordenadas coordenadas = new Coordenadas(utmLatitude, utmLongitude, utmZona);
                            dadosMedicao.setLatitude(String.valueOf(coordenadas.getLatitude()));
                            dadosMedicao.setLongitude(String.valueOf(coordenadas.getLongitude()));

                            listaDadosMedicao.add(dadosMedicao);
                        }
                    }
                }
            }
        } catch (IOException | ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return listaDadosMedicao;
    }

    private RegiaoList getRegiaoList() throws IOException {
        String json = JsonUtil.getJson("http://iqar.institutoslactec.org.br/iap/regioesPublico.php");
        return JsonUtil.getObjectMapper().readValue(json, RegiaoList.class);
    }

    private EstacaoList getEstacaoList(String siglaRegiao) throws IOException {
        String json = JsonUtil.getJson("http://iqar.institutoslactec.org.br/iap/iqamonitorPublico.php?regional=" + siglaRegiao);
        return JsonUtil.getObjectMapper().readValue(json, EstacaoList.class);
    }

}