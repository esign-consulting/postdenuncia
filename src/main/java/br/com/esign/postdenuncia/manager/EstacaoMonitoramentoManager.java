package br.com.esign.postdenuncia.manager;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.EstacaoMonitoramentoDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.google.geocode.BrazilGeocodeResponse;
import br.com.esign.postdenuncia.google.geocode.BrazilGoogleGeocode;
import br.com.esign.postdenuncia.model.Cidade;
import br.com.esign.postdenuncia.model.Coordenadas;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.Estado;
import br.com.esign.postdenuncia.model.Medicao;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.util.MessagesBundle;
import org.hibernate.resource.transaction.spi.TransactionStatus;

public class EstacaoMonitoramentoManager {

    public EstacaoMonitoramento salvar(String siglaOrgaoResponsavel,
            String nome, String decLatitude, String decLongitude,
            String utmLatitude, String utmLongitude, String utmZona,
            String endereco, String enderecoIndex) throws Exception {

        OrgaoResponsavelManager orgaoResponsavelMgr = new OrgaoResponsavelManager();
        OrgaoResponsavel orgaoResponsavel = orgaoResponsavelMgr.obterOrgaoResponsavel(siglaOrgaoResponsavel);

        boolean coordenadasDec = ((decLatitude != null && !decLatitude.isEmpty())
                || (decLongitude != null && !decLongitude.isEmpty()));

        boolean coordenadasUTM = ((utmLatitude != null && !utmLatitude.isEmpty())
                || (utmLongitude != null && !utmLongitude.isEmpty())
                || (utmZona != null && !utmZona.isEmpty()));

        Coordenadas coordenadas = null;
        if (coordenadasDec) {
            coordenadas = new Coordenadas(decLatitude, decLongitude);
        } else if (coordenadasUTM) {
            coordenadas = new Coordenadas(utmLatitude, utmLongitude, utmZona);
        }

        if (coordenadas != null) {
            return salvar(orgaoResponsavel, nome, coordenadas);
        } else {
            return salvar(orgaoResponsavel, nome, endereco, enderecoIndex);
        }
    }

    public EstacaoMonitoramento salvar(OrgaoResponsavel orgaoResponsavel,
            String nome, Coordenadas coordenadas) throws Exception {

        EstacaoMonitoramento estacaoMonitoramento = obterPeloNome(orgaoResponsavel, nome);
        return salvar(estacaoMonitoramento, coordenadas);
    }

    public EstacaoMonitoramento salvar(
            EstacaoMonitoramento estacaoMonitoramento, Coordenadas coordenadas)
            throws Exception {

        if (estacaoMonitoramento == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_OBRIGATORIA);
        }
        if (coordenadas == null) {
            throw new IllegalArgumentException(MessagesBundle.LOCALIZACAO_OBRIGATORIA);
        }

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        EstacaoMonitoramentoDAO estacaoMonitoramentoDAO = new EstacaoMonitoramentoDAO();
        try {
            BrazilGoogleGeocode googleGeocode = new BrazilGoogleGeocode(coordenadas.getLatitudeAsString(), coordenadas.getLongitudeAsString());
            BrazilGeocodeResponse geocodeResponse = (BrazilGeocodeResponse) googleGeocode.getResponseObject();
            estacaoMonitoramento.setCoordenadas(coordenadas);
            definirGeocodeResponse(estacaoMonitoramento, geocodeResponse);
            estacaoMonitoramentoDAO.save(estacaoMonitoramento);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return estacaoMonitoramento;
    }

    public EstacaoMonitoramento salvar(OrgaoResponsavel orgaoResponsavel,
            String nome, String endereco, String enderecoIndex)
            throws Exception {

        EstacaoMonitoramento estacaoMonitoramento = obterPeloNome(orgaoResponsavel, nome);
        return salvar(estacaoMonitoramento, endereco, enderecoIndex);
    }

    public EstacaoMonitoramento salvar(
            EstacaoMonitoramento estacaoMonitoramento, String endereco,
            String enderecoIndex) throws Exception {

        if (estacaoMonitoramento == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_OBRIGATORIA);
        }
        if (endereco == null || endereco.isEmpty() || enderecoIndex == null || enderecoIndex.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.LOCALIZACAO_OBRIGATORIA);
        }

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        EstacaoMonitoramentoDAO estacaoMonitoramentoDAO = new EstacaoMonitoramentoDAO();
        try {
            BrazilGoogleGeocode googleGeocode = new BrazilGoogleGeocode(endereco);
            BrazilGeocodeResponse geocodeResponse = (BrazilGeocodeResponse) googleGeocode.getResponseObject();
            geocodeResponse.setIndex(Integer.parseInt(enderecoIndex));
            Coordenadas coordenadas = geocodeResponse.getCoordenadas();
            estacaoMonitoramento.setCoordenadas(coordenadas);
            definirGeocodeResponse(estacaoMonitoramento, geocodeResponse);
            estacaoMonitoramentoDAO.save(estacaoMonitoramento);
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return estacaoMonitoramento;
    }

    public EstacaoMonitoramento obterPeloNome(
            OrgaoResponsavel orgaoResponsavel, String nome) throws Exception {

        if (orgaoResponsavel == null) {
            throw new IllegalArgumentException(MessagesBundle.ORGAO_RESPONSAVEL_OBRIGATORIO);
        }
        if (nome == null || nome.isEmpty()) {
            throw new IllegalArgumentException(MessagesBundle.NOME_ESTACAO_MONITORAMENTO_OBRIGATORIO);
        }

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        EstacaoMonitoramentoDAO estacaoMonitoramentoDAO = new EstacaoMonitoramentoDAO();
        EstacaoMonitoramento estacaoMonitoramento = null;
        try {
            estacaoMonitoramento = estacaoMonitoramentoDAO.obterPeloNome(orgaoResponsavel, nome);
            if (estacaoMonitoramento == null) {
                estacaoMonitoramento = new EstacaoMonitoramento();
                estacaoMonitoramento.setOrgaoResponsavel(orgaoResponsavel);
                estacaoMonitoramento.setNome(nome);
            }
            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return estacaoMonitoramento;
    }

    private void definirGeocodeResponse(EstacaoMonitoramento estacaoMonitoramento, BrazilGeocodeResponse geocodeResponse) throws Exception {
        if (!geocodeResponse.isBrazil()) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_RESTRITO_BRASIL);
        }

        CidadeManager cidadeMgr = new CidadeManager();
        String nomeEstado = geocodeResponse.getNomeEstado();
        String nomeCidade = geocodeResponse.getNomeCidade();
        Cidade cidade = cidadeMgr.obterCidade(nomeEstado, nomeCidade);

        estacaoMonitoramento.setEndereco(geocodeResponse.getEnderecoCompleto());
        estacaoMonitoramento.setBairro(geocodeResponse.getNomeBairro());
        estacaoMonitoramento.setCidade(cidade);
    }

    public void definirUltimaMedicao(EstacaoMonitoramento estacaoMonitoramento, boolean cache) throws Exception {
        if (estacaoMonitoramento == null) {
            throw new IllegalArgumentException(MessagesBundle.ESTACAO_MONITORAMENTO_OBRIGATORIA);
        }

        MedicaoManager medicaoManager = new MedicaoManager();
        Medicao ultimaMedicao = medicaoManager.obterUltimaMedicao(estacaoMonitoramento, cache);
        estacaoMonitoramento.setUltimaMedicao(ultimaMedicao);
    }

    public List<EstacaoMonitoramento> listar() throws Exception {
        return listar(false);
    }

    public List<EstacaoMonitoramento> listar(boolean ultimaMedicao) throws Exception {
        return listar(ultimaMedicao, false);
    }

    public List<EstacaoMonitoramento> listar(boolean ultimaMedicao, boolean cache) throws Exception {
        List<EstacaoMonitoramento> estacoesMonitoramentoList = null;

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        try {
            EstacaoMonitoramentoDAO estacaoMonitoramentoDAO = new EstacaoMonitoramentoDAO();
            estacoesMonitoramentoList = estacaoMonitoramentoDAO.findAll();
            if (estacoesMonitoramentoList != null && !estacoesMonitoramentoList.isEmpty() && ultimaMedicao) {
                for (EstacaoMonitoramento estacaoMonitoramento : estacoesMonitoramentoList) {
                    definirUltimaMedicao(estacaoMonitoramento, cache);
                }
            }

            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return estacoesMonitoramentoList;
    }

    public List<EstacaoMonitoramento> listarPorProximidade(Coordenadas coordenadas, int nEstacoes) throws Exception {
        List<EstacaoMonitoramento> estacaoMonitoramentoList = new ArrayList<>(nEstacoes);

        boolean commitOrRollback = false;
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction t = session.getTransaction();
        if (t == null || t.getStatus() != TransactionStatus.ACTIVE) {
            t = session.beginTransaction();
            commitOrRollback = true;
        }

        EstacaoMonitoramentoDAO estacaoMonitoramentoDAO = new EstacaoMonitoramentoDAO();
        try {

            BrazilGoogleGeocode googleGeocode = new BrazilGoogleGeocode(coordenadas.getLatitudeAsString(), coordenadas.getLongitudeAsString());
            BrazilGeocodeResponse geocodeResponse = (BrazilGeocodeResponse) googleGeocode.getResponseObject();
            if (geocodeResponse.isBrazil()) {
                String nomeEstado = geocodeResponse.getNomeEstado();

                EstadoManager estadoMgr = new EstadoManager();
                Estado estado = estadoMgr.obterEstado(nomeEstado);

                if (estado != null) {
                    List<EstacaoMonitoramento> list = estacaoMonitoramentoDAO.listarPorProximidade(estado, coordenadas);
                    if (list != null && !list.isEmpty()) {
                        int size = 0;
                        for (EstacaoMonitoramento estacaoMonitoramento : list) {
                            definirUltimaMedicao(estacaoMonitoramento, true);
                            Medicao ultimaMedicao = estacaoMonitoramento.getUltimaMedicao();
                            if (ultimaMedicao != null) {
                                estacaoMonitoramentoList.add(estacaoMonitoramento);
                                size++;
                                if (size == nEstacoes) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (commitOrRollback) {
                t.commit();
            }
        } catch (Exception e) {
            if (commitOrRollback) {
                t.rollback();
            }
            throw e;
        }
        return estacaoMonitoramentoList;
    }

    public EstacaoMonitoramento obterMaisProxima(Coordenadas coordenadas) throws Exception {
        List<EstacaoMonitoramento> list = listarPorProximidade(coordenadas, 1);
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }

}