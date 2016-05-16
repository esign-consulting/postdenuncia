package br.com.esign.postdenuncia.observer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.enterprise.event.Observes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Transaction;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import br.com.esign.postdenuncia.dao.DenunciaDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.dao.OrgaoResponsavelDAO;
import br.com.esign.postdenuncia.manager.EstacaoMonitoramentoManager;
import br.com.esign.postdenuncia.model.Denuncia;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.EstacaoMonitoramento;
import br.com.esign.postdenuncia.model.OrgaoResponsavel;
import br.com.esign.postdenuncia.util.HtmlUtil;
import br.com.esign.postdenuncia.util.MailUtil;

public class CETESB {

    private final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            DenunciaDAO dao = new DenunciaDAO();
            Denuncia denuncia = dao.find(36);
            CETESB cetesb = new CETESB();
            cetesb.denunciarFumacaPreta(denuncia);
            t.commit();
        } catch (Exception e) {
            e.printStackTrace();
            t.rollback();
        }
    }

    public void denunciarFumacaPreta(@Observes final Denuncia denuncia) {
        final String siglaEstado = "SP";
        final String codigoTipoDenuncia = "fumacapreta";
        if (denuncia.getTipo().getCodigo().equals(codigoTipoDenuncia)
                && denuncia.getCidade().getEstado().getSigla().equals(siglaEstado)) {

            Thread thread;
            thread = new Thread() {

                @Override
                public void run() {
                    Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
                    OrgaoResponsavelDAO orgaoResponsavelDAO = new OrgaoResponsavelDAO();
                    DenunciaDAO denunciaDAO = new DenunciaDAO();
                    try {
                        Denuncia mesmaDenuncia = null;
                        OrgaoResponsavel orgaoResponsavel = orgaoResponsavelDAO.obterPelaResponsabilidade("CETESB", siglaEstado, codigoTipoDenuncia);
                        if (orgaoResponsavel != null) {
                            String protocolo = null;
                            int attempts = 1;
                            while (attempts <= 5) {
                                try {
                                    protocolo = postNovaDenuncia(denuncia);
                                    attempts = 6;
                                } catch (ConnectException ce) {
                                    attempts++;
                                    Thread.sleep(60000);
                                }
                            }
                            if (protocolo != null && !protocolo.isEmpty()) {
                                mesmaDenuncia = denunciaDAO.find(denuncia.getId());
                                mesmaDenuncia.setOrgaoResponsavel(orgaoResponsavel);
                                mesmaDenuncia.setProtocolo(protocolo);
                                denunciaDAO.save(mesmaDenuncia);
                            }
                        }
                        t.commit();
                        if (mesmaDenuncia != null) {
                            sendEmail(mesmaDenuncia);
                        }
                    } catch (IOException | InterruptedException e) {
                        logger.error(e.getMessage(), e);
                        t.rollback();
                    }
                }

                private String postNovaDenuncia(Denuncia denuncia) throws IOException {
                    String protocolo = null;

                    HtmlUtil html = new HtmlUtil();
                    String spec = "http://sistemasinter.cetesb.sp.gov.br/disque_ambiental";
                    List<String> cookies = html.get(spec, null).getCookiesResult();

                    Map<String, String> headers = new HashMap<>();
                    headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.9; rv:29.0) Gecko/20100101 Firefox/29.0");

                    spec = "http://sistemasinter.cetesb.sp.gov.br/disque_ambiental/incluir_novo.asp";
                    String result = html.post(spec, getParams(denuncia), cookies, headers).getHtmlResult();

                    protocolo = obterProtocolo(result);

                    return protocolo;
                }

                private String getParams(Denuncia denuncia) throws UnsupportedEncodingException {
                    String local = denuncia.getEndereco();
                    String mun = denuncia.getCidade().getNome();
                    Date datahora = denuncia.getDatahora();

                    final String ENC = "UTF-8";
                    StringBuilder params = new StringBuilder();
                    params.append("tipo=1"); // Fumaça Preta - Denúncia (default)
                    params.append("&placa=").append(denuncia.getInfoAdicional());
                    params.append("&local=").append(URLEncoder.encode(local.substring(0, Math.min(local.length(), 100)), ENC));
                    params.append("&mun=").append(URLEncoder.encode(mun.substring(0, Math.min(mun.length(), 30)), ENC));
                    params.append("&data=").append(URLEncoder.encode(new SimpleDateFormat("dd/MM/yyyy").format(datahora), ENC));
                    params.append("&hora=").append(new SimpleDateFormat("hhmm").format(datahora));
                    params.append("&idespecie=19"); // ÔNIBUS / MICRO-ÔNIBUS (default)
                    params.append("&denuncia=").append(URLEncoder.encode("Denúncia feita através do aplicativo Post Fumaça Preta.", ENC));
                    params.append("&retorno=").append((denuncia.isRetorno()) ? "S" : "N");
                    params.append("&nome=").append(URLEncoder.encode(denuncia.getDenunciante().getNome(), ENC));
                    params.append("&email=").append(URLEncoder.encode(denuncia.getDenunciante().getEmail(), ENC));
                    params.append("&endereco=&numero=&complemento=&bairro=&cep=&listEstados=SP&listCidades=");

                    return params.toString();
                }

                private String obterProtocolo(String html) {
                    String protocolo = null;

                    try {
                        Document doc = Jsoup.parse(html);
                        Element table = doc.getElementsByTag("table").get(0);
                        Element tr = table.getElementsByTag("tr").get(0);
                        Element td = tr.getElementsByTag("td").get(3);
                        Element p = td.getElementsByTag("p").get(1);
                        protocolo = p.text();
                    } catch (Exception e) {
                    }

                    return protocolo;
                }

                private void sendEmail(Denuncia denuncia) {
                    Denunciante denunciante = denuncia.getDenunciante();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    StringBuilder emailText = new StringBuilder();
                    emailText.append("A denúncia de fumaça preta abaixo foi devidamente enviada para a CETESB - Companhia Ambiental do Estado de São Paulo.\r\n\r\n");
                    emailText.append("Endereço: ").append(denuncia.getEndereco()).append("\r\n");
                    emailText.append("Data/hora: ").append(dateFormatter.format(denuncia.getDatahora())).append("\r\n");
                    emailText.append("Placa do veículo: ").append(denuncia.getInfoAdicional()).append("\r\n\r\n");
                    emailText.append("Por favor, anote o número do protocolo fornecido pelo órgão: ").append(denuncia.getProtocolo()).append(".");

                    EstacaoMonitoramento estacaoMonitoramento;
                    EstacaoMonitoramentoManager estacaoMonitoramentoMgr = new EstacaoMonitoramentoManager();
                    try {
                        estacaoMonitoramento = estacaoMonitoramentoMgr.obterMaisProxima(denuncia.getCoordenadas());
                    } catch (Exception e) {
                        estacaoMonitoramento = null;
                    }
                    if (estacaoMonitoramento != null) {
                        emailText.append("\r\n\r\nVeja a qualidade do ar obtida da estação de monitoramento mais próxima do local da denúncia:\r\n\r\n");
                        emailText.append("Nome da estação: ").append(estacaoMonitoramento.getNome());
                        DecimalFormat numberFormatter = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));
                        emailText.append(" (Distância: ").append(numberFormatter.format(estacaoMonitoramento.getDistancia())).append(" Km)\r\n");
                        emailText.append("Qualidade do ar: ").append(estacaoMonitoramento.getUltimaMedicao().getQualidadeAr().getClassificacao());
                        Integer indice = estacaoMonitoramento.getUltimaMedicao().getIndice().intValue();
                        emailText.append(" (Índice: ").append((indice == null) ? "Não informado" : indice).append(")\r\n");
                        emailText.append("Data/hora: ").append(dateFormatter.format(estacaoMonitoramento.getUltimaMedicao().getDatahora())).append("\r\n");
                        emailText.append("Fonte: ").append(estacaoMonitoramento.getOrgaoResponsavel().getSigla());
                        emailText.append(" - ").append(estacaoMonitoramento.getOrgaoResponsavel().getNome()).append("\r\n\r\n");
                        emailText.append("Em prol da qualidade do ar, continue denunciando veículos que emitem fumaça preta de forma irregular!");
                    }

                    StringBuilder entireEmailText = new StringBuilder();
                    entireEmailText.append("Prezad").append(denunciante.getArticle()).append(" ").append(denunciante.getNome()).append(",\r\n\r\n");
                    entireEmailText.append(emailText).append("\r\n\r\n");
                    entireEmailText.append("Atenciosamente,\r\n\r\n");
                    entireEmailText.append("Equipe do Post Denúncia.");

                    MailUtil mailUtil = new MailUtil();
                    mailUtil.sendMessage(denunciante.getEmail(), "Post Denúncia - Denúncia de fumaça preta enviada para a CETESB", entireEmailText.toString());
                }

            };
            thread.start();

        }
    }

}