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
import br.com.esign.postdenuncia.dao.FacebookUserDAO;
import br.com.esign.postdenuncia.dao.HibernateUtil;
import br.com.esign.postdenuncia.facebook.User;
import br.com.esign.postdenuncia.model.Denunciante;
import br.com.esign.postdenuncia.model.FacebookUser;
import br.com.esign.postdenuncia.util.FacebookUtil;
import br.com.esign.postdenuncia.util.MessagesBundle;
import java.text.ParseException;
import java.util.Optional;

/**
 * Servlet implementation class SaveFacebookUser
 */
@SuppressWarnings("serial")
@WebServlet("/savefbuser")
public class SaveFacebookUser extends GenericServlet {

    @Inject
    private FacebookUserDAO fbUserDAO;
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
        Response<FacebookUser> resp = new Response<>();
        Transaction t = HibernateUtil.getSessionFactory().getCurrentSession().beginTransaction();
        try {
            String json = request.getParameter("json");
            if (json == null || json.isEmpty()) {
                throw new IllegalArgumentException(MessagesBundle.JSON_FACEBOOK_OBRIGATORIO);
            }
            User user = FacebookUtil.userProfile(json);
            FacebookUser fbUser = fbUserDAO.find(user.getId());
            if (fbUser == null) {
                fbUser = new FacebookUser(user);
            } else {
                fbUser.setUser(user);
            }

            Set<User> friends = user.getFriends().getData();
            Set<FacebookUser> fbFriends = fbUser.getFriends();

            Set<FacebookUser> newFbFriends = new HashSet<>();
            for (User friend : friends) {
                Optional<FacebookUser> optional = fbFriends.stream().filter(f -> f.getId().equals(friend.getId())).findFirst();
                FacebookUser fbFriend = (optional.isPresent()) ? optional.get() : null;
                if (fbFriend == null) {
                    fbFriend = fbUserDAO.find(friend.getId());
                    if (fbFriend == null) {
                        fbFriend = new FacebookUser(friend);
                        fbUserDAO.save(fbFriend);
                    }
                    newFbFriends.add(fbFriend);
                }
            }

            Set<FacebookUser> oldFbFriends = new HashSet<>();
            fbFriends.stream().forEach((fbFriend) -> {
                Optional<User> optional = friends.stream().filter(f -> f.getId().equals(fbFriend.getId())).findFirst();
                User friend = (optional.isPresent()) ? optional.get() : null;
                if (friend == null) {
                    oldFbFriends.add(fbFriend);
                }
            });

            if (!newFbFriends.isEmpty()) {
                fbFriends.addAll(newFbFriends);
            }
            if (!oldFbFriends.isEmpty()) {
                fbFriends.removeAll(oldFbFriends);
            }
            fbUserDAO.save(fbUser);

            Denunciante denunciante = denuncianteDAO.obterPeloEmail(fbUser.getEmail());
            if (denunciante != null && (denunciante.getFacebookUser() == null || !denunciante.getFacebookUser().equals(fbUser))) {
                denunciante.setFacebookUser(fbUser);
                denuncianteDAO.save(denunciante);
            }

            t.commit();
            resp.addEntity(fbUser);
        } catch (IllegalArgumentException | IOException | ParseException e) {
            logger.error(e.getMessage(), e);
            t.rollback();
            resp.addException(e);
        }
        jsonResponse(response, resp);
    }

}