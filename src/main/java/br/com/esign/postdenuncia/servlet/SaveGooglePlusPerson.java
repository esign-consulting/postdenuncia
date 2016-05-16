package br.com.esign.postdenuncia.servlet;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Transaction;

import br.com.esign.postdenuncia.dao.DenuncianteDAO;
import br.com.esign.postdenuncia.dao.GooglePlusPersonDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.google.plus.Person;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.GooglePlusPerson;
import br.com.esign.postdenuncia.util.GoogleUtil;
import br.com.esign.postdenuncia.util.MessagesBundle;
import java.text.ParseException;
import java.util.Optional;

/**
 * Servlet implementation class SaveGooglePlusPerson
 */
@SuppressWarnings("serial")
@WebServlet("/savegplusperson")
public class SaveGooglePlusPerson extends GenericServlet {

    @Inject
    private GooglePlusPersonDAO gplusPersonDAO;
    @Inject
    private DenuncianteDAO denuncianteDAO;

    /**
     * @param request
     * @param response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        logParameters(request);
        Response<GooglePlusPerson> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            String json = request.getParameter("json");
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.JSON_GOOGLE_PLUS_OBRIGATORIO);
            }
            Person person = GoogleUtil.personProfile(json);
            GooglePlusPerson gplusPerson = gplusPersonDAO.find(person.getId());
            if (gplusPerson == null) {
                gplusPerson = new GooglePlusPerson(person);
            } else {
                gplusPerson.setPerson(person);
            }

            Set<Person> people = person.getPeople();
            Set<GooglePlusPerson> gplusPeople = gplusPerson.getPeople();

            Set<GooglePlusPerson> newGplusPeople = new HashSet<>();
            for (Person person2 : people) {
                Optional<GooglePlusPerson> optional = gplusPeople.stream().filter(p -> p.getId().equals(person2.getId())).findFirst();
                GooglePlusPerson gplusPerson2 = (optional.isPresent()) ? optional.get() : null;
                if (gplusPerson2 == null) {
                    gplusPerson2 = gplusPersonDAO.find(person2.getId());
                    if (gplusPerson2 == null) {
                        gplusPerson2 = new GooglePlusPerson(person2);
                        gplusPersonDAO.save(gplusPerson2);
                    }
                    newGplusPeople.add(gplusPerson2);
                }
            }

            Set<GooglePlusPerson> oldGplusPeople = new HashSet<>();
            gplusPeople.stream().forEach((gplusPerson2) -> {
                Optional<Person> optional = people.stream().filter(p -> p.getId().equals(gplusPerson2.getId())).findFirst();
                Person person2 = (optional.isPresent()) ? optional.get() : null;
                if (person2 == null) {
                    oldGplusPeople.add(gplusPerson2);
                }
            });

            if (!newGplusPeople.isEmpty()) {
                gplusPeople.addAll(newGplusPeople);
            }
            if (!oldGplusPeople.isEmpty()) {
                gplusPeople.removeAll(oldGplusPeople);
            }
            gplusPersonDAO.save(gplusPerson);

            Denunciante denunciante = denuncianteDAO.obterPeloEmail(gplusPerson.getEmail());
            if (denunciante != null && (denunciante.getFacebookUser() == null || !denunciante.getGooglePlusPerson().equals(gplusPerson))) {
                denunciante.setGooglePlusPerson(gplusPerson);
                denuncianteDAO.save(denunciante);
            }

            t.commit();
            resp.addEntity(gplusPerson);
        } catch (IllegalArgumentException | IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}