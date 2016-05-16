package br.com.esign.postdenuncia.util;

import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;

public class Teste {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Coordenadas coordenadas = new Coordenadas("7256980", "602093", "22J");
            EstacaoMonitoramentoManager manager = new EstacaoMonitoramentoManager();
            EstacaoMonitoramento estacao = manager.obterMaisProxima(coordenadas);
            System.out.println(estacao.getDistancia());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}