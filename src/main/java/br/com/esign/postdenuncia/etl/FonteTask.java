package br.com.esign.postdenuncia.etl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.MedicaoDAO;
import br.com.esign.postdenuncia.dao.PoluenteDAO;
import br.com.esign.postdenuncia.dao.UltimaMedicaoCache;
import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Medicao;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.model.Poluente;
import br.com.esign.postdenuncia.model.QualidadeAr;

public abstract class FonteTask extends TimerTask {

    private final Logger logger = LogManager.getLogger();

    protected abstract FonteMedicao obterFonteMedicao() throws IOException;

    protected abstract OrgaoResponsavel obterOrgaoResponsavel();

    @Override
    public void run() {
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            OrgaoResponsavel orgaoResponsavel = obterOrgaoResponsavel();
            Set<EstacaoMonitoramento> estacoesMonitoramento = orgaoResponsavel.getEstacoesMonitoramento();
            if (estacoesMonitoramento == null || estacoesMonitoramento.isEmpty()) {
                t.rollback();
                return;
            }

            final Map<String, EstacaoMonitoramento> estacoesMonitoramentoMap = new HashMap<>(estacoesMonitoramento.size());
            estacoesMonitoramento.stream().forEach((e) -> estacoesMonitoramentoMap.put(e.getNome(), e));

            Set<QualidadeAr> qualidadesAr = orgaoResponsavel.getQualidadesAr();
            if (qualidadesAr == null || qualidadesAr.isEmpty()) {
                t.rollback();
                return;
            }

            final Map<String, QualidadeAr> qualidadesArMap = new HashMap<>(qualidadesAr.size());
            qualidadesAr.stream().forEach((qa) -> qualidadesArMap.put(qa.getClassificacao(), qa));

            PoluenteDAO poluenteDAO = new PoluenteDAO();
            List<Poluente> poluentes = poluenteDAO.findAll();

            final Map<String, Poluente> poluentesPorNomeMap = new HashMap<>((poluentes == null) ? 0 : poluentes.size());
            final Map<String, Poluente> poluentesPorRepresentacaoMap = new HashMap<>((poluentes == null) ? 0 : poluentes.size());

            if (poluentes != null) {
                poluentes.stream().forEach((p) -> poluentesPorNomeMap.put(p.getNome().toUpperCase(), p));
                poluentes.stream().forEach((p) -> poluentesPorRepresentacaoMap.put(p.getRepresentacao(), p));
            }

            EstacaoMonitoramentoManager estacaoMonitoramentoMgr = new EstacaoMonitoramentoManager();
            UltimaMedicaoCache cache = UltimaMedicaoCache.getCache();
            MedicaoDAO medicaoDAO = new MedicaoDAO();

            FonteMedicao fonteMedicao = obterFonteMedicao();
            List<DadosMedicao> listaDadosMedicao = fonteMedicao.listarDadosMedicao();

            Date agora = new Date();
            DecimalFormat parser = new DecimalFormat("##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
            for (DadosMedicao dadosMedicao : listaDadosMedicao) {

                String dadoEstacao = dadosMedicao.getEstacao();
                if (dadoEstacao == null || dadoEstacao.isEmpty()) {
                    continue;
                }
                EstacaoMonitoramento estacaoMonitoramento = estacoesMonitoramentoMap.get(dadoEstacao);
                Coordenadas coordenadas = dadosMedicao.getCoordenadas();
                if (estacaoMonitoramento == null) {
                    if (coordenadas == null) {
                        continue;
                    } else {
                        try {
                            estacaoMonitoramento = estacaoMonitoramentoMgr.salvar(orgaoResponsavel, dadoEstacao, coordenadas);
                            estacoesMonitoramentoMap.put(estacaoMonitoramento.getNome(), estacaoMonitoramento);
                            cache.setUltimaMedicao(estacaoMonitoramento, null);
                        } catch (Exception e) {
                            continue;
                        }
                    }
                } else if (coordenadas != null && !estacaoMonitoramento.getCoordenadas().equals(coordenadas)) {
                    try {
                        estacaoMonitoramentoMgr.salvar(estacaoMonitoramento, coordenadas);
                    } catch (Exception e) {
                    }
                }

                Date datahora = dadosMedicao.getDatahora();
                if (datahora == null || datahora.after(agora)) {
                    continue;
                }
                Medicao ultimaMedicao = cache.getUltimaMedicao(estacaoMonitoramento);
                if (ultimaMedicao != null && !datahora.after(ultimaMedicao.getDatahora())) {
                    continue;
                }

                String dadoQualidadeAr = dadosMedicao.getQualidadeAr();
                if (dadoQualidadeAr == null || dadoQualidadeAr.isEmpty()) {
                    continue;
                }
                QualidadeAr qualidadeAr = qualidadesArMap.get(dadoQualidadeAr);
                if (qualidadeAr == null) {
                    continue;
                }

                String dadoIndice = dadosMedicao.getIndice();
                Double indice = null;
                if (dadoIndice != null && !dadoIndice.isEmpty()) {
                    try {
                        indice = parser.parse(dadoIndice).doubleValue();
                    } catch (ParseException e) {
                    }
                }

                Poluente poluente = null;
                String dadoPoluente = dadosMedicao.getPoluente();
                if (dadoPoluente != null && !dadoPoluente.isEmpty()) {
                    Pattern pattern = Pattern.compile("\\((.*?)\\)");
                    Matcher matcher = pattern.matcher(dadoPoluente);
                    String value = (matcher.find())
                            ? matcher.group().replaceAll("[()]", "")
                            : dadoPoluente;
                    poluente = poluentesPorNomeMap.get(value.toUpperCase());
                    if (poluente == null) {
                        poluente = poluentesPorRepresentacaoMap.get(value);
                    }
                }

                Medicao medicao = new Medicao();
                medicao.setEstacaoMonitoramento(estacaoMonitoramento);
                medicao.setQualidadeAr(qualidadeAr);
                medicao.setDatahora(datahora);
                medicao.setIndice(indice);
                medicao.setPoluente(poluente);
                medicaoDAO.save(medicao);
                cache.setUltimaMedicao(medicao);
            }

            t.commit();
        } catch (Exception e) {
            t.rollback();
            logger.error(e.getMessage(), e);
        }
    }

}