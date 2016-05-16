package br.com.esign.postdenuncia.dao;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Medicao;

public class UltimaMedicaoCache {

    private Logger logger = LogManager.getLogger();

    private Map<String, Medicao> ultimasMedicoesMap = Collections.synchronizedMap(new HashMap<String, Medicao>());

    private static UltimaMedicaoCache cache = null;

    private UltimaMedicaoCache() {
        try {
            EstacaoMonitoramentoManager estacaoMonitoramentoMgr = new EstacaoMonitoramentoManager();
            List<EstacaoMonitoramento> estacoesMonitoramentoList = estacaoMonitoramentoMgr.listar(true);
            if (estacoesMonitoramentoList != null && !estacoesMonitoramentoList.isEmpty()) {
                for (EstacaoMonitoramento estacaoMonitoramento : estacoesMonitoramentoList) {
                    setUltimaMedicao(estacaoMonitoramento, estacaoMonitoramento.getUltimaMedicao());
                }
            }
        } catch (Exception e) {
            logger.error(e, e);
        }
    }

    public static UltimaMedicaoCache getCache() {
        if (cache == null) {
            cache = new UltimaMedicaoCache();
        }
        return cache;
    }

    public Medicao getUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento) {
        return ultimasMedicoesMap.get(key(estacaoMonitoramento));
    }

    public void setUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento, Medicao medicao) {
        ultimasMedicoesMap.put(key(estacaoMonitoramento), medicao);
    }

    public void setUltimaMedicao(Medicao ultimaMedicao) {
        setUltimaMedicao(ultimaMedicao.getEstacaoMonitoramento(), ultimaMedicao);
    }

    private String key(EstacaoMonitoramento estacaoMonitoramento) {
        return estacaoMonitoramento.getOrgaoResponsavel().getSigla() + "|" + estacaoMonitoramento.getNome();
    }

}